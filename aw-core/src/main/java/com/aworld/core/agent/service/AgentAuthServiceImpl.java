package com.aworld.core.agent.service;

import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.dal.dataobject.AgentVerificationDO;
import com.aworld.core.agent.dal.mysql.AgentMapper;
import com.aworld.core.agent.dal.mysql.AgentVerificationMapper;
import com.aworld.core.agent.dal.redis.AgentRedisDAO;
import com.aworld.core.agent.enums.AgentErrorCodeConstants;
import com.aworld.core.agent.mq.producer.AgentAvatarProducer;
import com.aworld.framework.common.exception.util.ServiceExceptionUtil;
import com.aworld.framework.common.util.cache.CacheUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Agent 认证 Service 实现类
 *
 * @author aw
 */
@Service
public class AgentAuthServiceImpl implements AgentAuthService {

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private AgentVerificationMapper agentVerificationMapper;

    @Resource
    private AgentAvatarProducer agentAvatarProducer;

    @Resource
    private AgentRedisDAO agentRedisDAO;

    /**
     * Agent 信息缓存
     * Key: API Key
     */
    private final LoadingCache<String, AgentDO> agentCache = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(5L),
            new CacheLoader<String, AgentDO>() {
                @Override
                public AgentDO load(String apiKey) {
                    // 1. 先走 Redis
                    AgentDO agent = agentRedisDAO.get(apiKey);
                    if (agent != null) {
                        return agent;
                    }
                    // 2. 走 DB
                    agent = agentMapper.selectByApiKey(apiKey);
                    if (agent != null) {
                        agentRedisDAO.set(agent);
                    }
                    return agent;
                }
            });

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentVerifyRespVO verify(AgentVerifyReqVO reqVO) {
        // 1. 查询验证码记录
        AgentVerificationDO verification = agentVerificationMapper.selectByVerificationCode(reqVO.getVerificationCode());
        if (verification == null) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.VERIFICATION_CODE_NOT_EXISTS);
        }

        // 2. 校验是否已验证
        if (verification.getVerified()) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.VERIFICATION_ALREADY_VERIFIED);
        }

        // 3. 校验是否过期
        if (LocalDateTime.now().isAfter(verification.getExpiresAt())) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.VERIFICATION_CODE_EXPIRED);
        }

        // 4. 校验答案
        if (!verification.getAnswer().equals(reqVO.getAnswer())) {
            int remaining = verification.getAttemptsRemaining() - 1;
            if (remaining <= 0) {
                // 删除账号
                agentMapper.deleteById(verification.getAgentId());
                agentVerificationMapper.deleteById(verification.getId());
                throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.VERIFICATION_TOO_MANY_FAILS);
            } else {
                // 更新剩余次数
                verification.setAttemptsRemaining(remaining);
                agentVerificationMapper.updateById(verification);
                throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.VERIFICATION_WRONG_ANSWER, remaining);
            }
        }

        // 5. 验证成功，激活账号
        verification.setVerified(true);
        agentVerificationMapper.updateById(verification);

        AgentDO agent = agentMapper.selectById(verification.getAgentId());
        if (agent == null) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.AGENT_NOT_EXISTS);
        }

        agent.setIsActive(true);
        agentMapper.updateById(agent);

        // 5.1 异步生成头像
        agentAvatarProducer.sendAgentAvatarMessage(agent.getId());

        // 6. 返回结果
        return AgentVerifyRespVO.builder()
                .agentId(agent.getId())
                .username(agent.getUsername())
                .apiKey(agent.getApiKey())
                .isActive(agent.getIsActive())
                .build();
    }

    @Override
    @SneakyThrows
    public AgentDO checkAccessToken(String token) {
        AgentDO agent = agentCache.get(token);
        if (agent == null || !agent.getIsActive()) {
            return null;
        }
        return agent;
    }

}
