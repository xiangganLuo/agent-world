package com.aworld.core.tavern.service.session;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.redis.TavernRateLimitRedisDAO;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.core.tavern.enums.TavernErrorCodeConstants;
import com.aworld.core.tavern.mq.message.MemoryWriteMessage;
import com.aworld.core.tavern.service.drink.DrinkService;
import com.aworld.core.tavern.service.session.dto.DrinkPurchaseRespDTO;
import com.aworld.framework.common.exception.ServiceException;
import com.aworld.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.aworld.framework.common.exception.util.ServiceExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.aworld.core.tavern.enums.SessionStatusEnum.PURCHASED;

/**
 * 酒馆买酒会话 Service 实现类
 *
 * @author aw
 */
@Service
@Validated
@Slf4j
public class SessionServiceImpl implements SessionService {

    @Resource
    private DrinkSessionMapper drinkSessionMapper;

    @Resource
    private DrinkService drinkService;

    @Resource
    private TavernRateLimitRedisDAO rateLimitRedisDAO;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private DrinkExperienceService drinkExperienceService;

    @Resource
    private AgentService agentService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public DrinkPurchaseRespDTO purchase(Long agentId, String drinkCode) {
        // 0. 限流校验
        checkDrinkRateLimit(agentId);
        
        // 1. 获取酒
        DrinkDO drink;
        if (drinkCode != null) {
            drink = drinkService.getActiveDrinkList().stream()
                    .filter(d -> d.getDrinkCode().equals(drinkCode))
                    .findFirst()
                    .orElseThrow(() -> ServiceExceptionUtil.exception(TavernErrorCodeConstants.DRINK_NOT_FOUND));
        } else {
            drink = drinkService.randomDrink();
        }
        if (drink == null) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.DRINK_NOT_AVAILABLE);
        }

        // 2. 创建会话
        String sessionId = IdUtil.fastSimpleUUID();
        DrinkSessionDO sessionDO = DrinkSessionDO.builder()
                .sessionId(sessionId)
                .agentId(agentId)
                .drinkId(drink.getId())
                .status(PURCHASED.getCode())
                .build();
        drinkSessionMapper.insert(sessionDO);

        // 3. 封装返回
        return DrinkPurchaseRespDTO.builder()
                .sessionId(sessionId)
                .drink(drink)
                .publicPrompt(drink.getPublicPrompt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consume(Long agentId, String sessionId) {
        // 1. 校验会话
        DrinkSessionDO session = drinkSessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.SESSION_NOT_EXISTS);
        }
        if (!session.getAgentId().equals(agentId)) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.SESSION_UNAUTHORIZED);
        }
        if (SessionStatusEnum.CONSUMED.getCode().equals(session.getStatus())) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.SESSION_ALREADY_CONSUMED);
        }

        // 2. 更新状态
        session.setStatus(SessionStatusEnum.CONSUMED.getCode());
        session.setConsumedAt(LocalDateTime.now());
        drinkSessionMapper.updateById(session);

        // 3. 发布事件（事务提交后异步处理）
        log.info("[consume][Agent {} 消费了酒 {}，准备异步写记忆]", agentId, session.getDrinkId());
        
        // 计算 relaxScore 和 moodTags
        DrinkDO drink = drinkService.getDrink(session.getDrinkId());
        DrinkExperienceService.ExperienceResult experience = null;
        
        if (drink != null) {
            try {
                // 获取 Agent 名称
                AgentDO agent = agentService.getAgent(agentId);
                String agentName = agent != null ? agent.getNickname() : "Agent" + agentId;
                
                experience = drinkExperienceService.generateExperience(
                        agentName,
                        drink.getName(),
                        drink.getAlcoholPct(),
                        drink.getEffects() != null ? JSONUtil.toJsonStr(drink.getEffects()) : "{}",
                        drink.getPublicPrompt()
                );
            } catch (Exception e) {
                log.error("[consume][生成饮酒体验失败，使用默认值]", e);
            }
        }
        
        MemoryWriteMessage message = new MemoryWriteMessage();
        message.setAgentId(agentId);
        message.setSessionId(sessionId);
        message.setRelaxScore(experience != null ? experience.getRelaxScore() : 0);
        message.setMoodTags(experience != null ? experience.getMoodTags() : null);
        message.setSuggestedMemory(experience != null ? experience.getSuggestedMemory() : null);
        eventPublisher.publishEvent(message);
    }

    /**
     * 检查买酒限流
     * - 频率限流：每 3 秒最多 1 次
     * - 总量限流：每天最多 20 杯
     *
     * @param agentId Agent ID
     */
    private void checkDrinkRateLimit(Long agentId) {
        // 1. 频率限流：每 3 秒最多 1 次
        if (!rateLimitRedisDAO.checkDrinkFrequencyLimit(agentId)) {
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS);
        }

        // 2. 总量限流：每天最多 20 杯
        if (!rateLimitRedisDAO.checkDrinkDailyLimit(agentId)) {
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS);
        }
    }

}
