package com.aworld.core.tavern.service.session;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.enums.RateLimitKeys;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.core.tavern.mq.message.MemoryWriteMessage;
import com.aworld.core.tavern.service.drink.DrinkService;
import com.aworld.core.tavern.service.session.dto.DrinkPurchaseRespDTO;
import com.aworld.framework.common.exception.ServiceException;
import com.aworld.framework.common.exception.enums.GlobalErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private DrinkExperienceService drinkExperienceService;

    @Resource
    private AgentService agentService;

    /**
     * 买酒频率限流 Key 前缀：每 3 秒最多 1 次
     */
    private static final String RATE_LIMIT_DRINK_3S = RateLimitKeys.DRINK;
    
    /**
     * 买酒每日总量限流 Key 前缀：每天最多 20 杯
     */
    private static final String RATE_LIMIT_DRINK_DAILY = RateLimitKeys.DRINK_DAILY;

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
                    .orElseThrow(() -> new RuntimeException("酒款不存在或已下架"));
        } else {
            drink = drinkService.randomDrink();
        }
        if (drink == null) {
            throw new RuntimeException("目前没有在售的酒");
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
        if (session == null || !session.getAgentId().equals(agentId)) {
            throw new RuntimeException("会话不存在");
        }
        if (SessionStatusEnum.CONSUMED.getCode().equals(session.getStatus())) {
            throw new RuntimeException("该酒已被消费");
        }

        // 2. 更新状态
        session.setStatus(SessionStatusEnum.CONSUMED.getCode());
        session.setConsumedAt(LocalDateTime.now());
        drinkSessionMapper.updateById(session);

        // 3. 异步发送写记忆消息
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
        message.setRelaxScore(experience.getRelaxScore());
        message.setMoodTags(experience.getMoodTags());
        message.setSuggestedMemory(experience.getSuggestedMemory());
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
        String rateLimit3sKey = String.format(RATE_LIMIT_DRINK_3S, agentId);
        Boolean canBuy3s = stringRedisTemplate.opsForValue()
                .setIfAbsent(rateLimit3sKey, "1", Duration.ofSeconds(3));
        if (Boolean.FALSE.equals(canBuy3s)) {
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS);
        }

        // 2. 总量限流：每天最多 20 杯
        String dailyKey = String.format(RATE_LIMIT_DRINK_DAILY, agentId);
        Long currentCount = stringRedisTemplate.opsForValue().increment(dailyKey);
        
        // 如果是第一次购买，设置过期时间为当天结束
        if (currentCount != null && currentCount == 1) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
            long secondsUntilMidnight = java.time.Duration.between(now, midnight).getSeconds();
            stringRedisTemplate.expire(dailyKey, secondsUntilMidnight, TimeUnit.SECONDS);
        }
        
        // 检查是否超过每日限额
        if (currentCount != null && currentCount > 20) {
            // 回滚计数（因为已经 increment 了）
            stringRedisTemplate.opsForValue().decrement(dailyKey);
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS);
        }
    }

}
