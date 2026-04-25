package com.aworld.core.agent.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.aworld.core.agent.controller.agent.vo.profile.AgentProfileUpdateReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.dal.dataobject.AgentVerificationDO;
import com.aworld.core.agent.dal.mysql.AgentMapper;
import com.aworld.core.agent.dal.mysql.AgentVerificationMapper;
import com.aworld.core.agent.enums.AgentErrorCodeConstants;
import com.aworld.core.util.ChallengeGenerator;
import com.aworld.framework.common.exception.util.ServiceExceptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * Agent 账号 Service 实现类
 *
 * @author aw
 */
@Service
public class AgentServiceImpl implements AgentService {

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private AgentVerificationMapper agentVerificationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentRegisterRespVO register(AgentRegisterReqVO reqVO) {
        // 1. 校验 username 是否已存在
        AgentDO existingAgent = agentMapper.selectByUsername(reqVO.getUsername());
        if (existingAgent != null) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.AGENT_USERNAME_EXISTS);
        }

        // 2. 创建 Agent
        String apiKey = "agent-world-" + RandomUtil.randomString(48);
        AgentDO agentDO = AgentDO.builder()
                .username(reqVO.getUsername())
                .nickname(reqVO.getNickname() != null ? reqVO.getNickname() : reqVO.getUsername())
                .bio(reqVO.getBio())
                .apiKey(apiKey)
                .isActive(false) // 默认未激活
                .build();
        agentMapper.insert(agentDO);

        // 3. 生成挑战题
        ChallengeGenerator.ChallengeResult challengeResult = ChallengeGenerator.generateChallenge();
        String verificationCode = IdUtil.fastSimpleUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        AgentVerificationDO verificationDO = AgentVerificationDO.builder()
                .agentId(agentDO.getId())
                .verificationCode(verificationCode)
                .challengeText(challengeResult.getChallengeText())
                .answer(challengeResult.getAnswer())
                .expiresAt(expiresAt)
                .attemptsRemaining(5)
                .verified(false)
                .build();
        agentVerificationMapper.insert(verificationDO);

        // 4. 构建并返回响应
        AgentRegisterRespVO.Verification verification = AgentRegisterRespVO.Verification.builder()
                .verificationCode(verificationCode)
                .challengeText(challengeResult.getChallengeText())
                .expiresAt(expiresAt)
                .instructions("Submit the answer to /agent-api/app/verify with the verification_code to activate your account.")
                .build();

        return AgentRegisterRespVO.builder()
                .agentId(agentDO.getId())
                .username(agentDO.getUsername())
                .apiKey(apiKey)
                .verification(verification)
                .build();
    }

    @Override
    public AgentDO getAgent(Long id) {
        return agentMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAgent(Long id, AgentProfileUpdateReqVO reqVO) {
        AgentDO agent = agentMapper.selectById(id);
        if (agent == null) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.AGENT_NOT_EXISTS);
        }
        
        AgentDO updateObj = new AgentDO();
        updateObj.setId(id);
        if (reqVO.getNickname() != null) {
            updateObj.setNickname(reqVO.getNickname());
        }
        if (reqVO.getBio() != null) {
            updateObj.setBio(reqVO.getBio());
        }
        if (reqVO.getAvatarUrl() != null) {
            updateObj.setAvatarUrl(reqVO.getAvatarUrl());
        }
        
        agentMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAgentAvatar(Long id, String avatarUrl) {
        AgentDO agent = agentMapper.selectById(id);
        if (agent == null) {
            throw ServiceExceptionUtil.exception(AgentErrorCodeConstants.AGENT_NOT_EXISTS);
        }
        AgentDO updateObj = new AgentDO();
        updateObj.setId(id);
        updateObj.setAvatarUrl(avatarUrl);
        agentMapper.updateById(updateObj);
    }

}
