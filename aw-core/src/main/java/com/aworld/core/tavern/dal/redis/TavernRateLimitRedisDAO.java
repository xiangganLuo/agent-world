package com.aworld.core.tavern.dal.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 酒馆限流 Redis DAO
 * <p>
 * 封装酒馆相关功能的限流逻辑，符合后端开发规范 15.3 (CRITICAL)
 *
 * @author aw
 */
@Repository
@Slf4j
public class TavernRateLimitRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 买酒每日限量
     */
    private static final int DRINK_DAILY_LIMIT = 20;

    /**
     * 检查买酒频率限流（每 3 秒最多 1 次）
     *
     * @param agentId Agent ID
     * @return true-允许购买，false-触发限流
     */
    public boolean checkDrinkFrequencyLimit(Long agentId) {
        String key = TavernRateLimitConstants.buildDrinkRateLimitKey(agentId);
        Boolean canBuy = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(3));
        return Boolean.TRUE.equals(canBuy);
    }

    /**
     * 检查买酒每日总量限流
     *
     * @param agentId Agent ID
     * @return true-允许购买，false-超过每日限额
     */
    public boolean checkDrinkDailyLimit(Long agentId) {
        String key = TavernRateLimitConstants.buildDrinkDailyLimitKey(agentId);
        Long currentCount = stringRedisTemplate.opsForValue().increment(key);

        // 如果是第一次购买，设置过期时间为当天结束
        if (currentCount != null && currentCount == 1) {
            setExpireAtMidnight(key);
        }

        // 检查是否超过每日限额
        if (currentCount != null && currentCount > DRINK_DAILY_LIMIT) {
            // 回滚计数（因为已经 increment 了）
            stringRedisTemplate.opsForValue().decrement(key);
            return false;
        }

        return true;
    }

    /**
     * 检查留言频率限流（每 60 秒最多 1 条）
     *
     * @param agentId Agent ID
     * @return true-允许留言，false-触发限流
     */
    public boolean checkGuestbookFrequencyLimit(Long agentId) {
        String key = TavernRateLimitConstants.buildGuestbookRateLimitKey(agentId);
        Boolean canPost = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(60));
        return Boolean.TRUE.equals(canPost);
    }

    /**
     * 设置 Key 在当天午夜过期
     *
     * @param key Redis Key
     */
    private void setExpireAtMidnight(String key) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long secondsUntilMidnight = java.time.Duration.between(now, midnight).getSeconds();
        stringRedisTemplate.expire(key, secondsUntilMidnight, TimeUnit.SECONDS);
    }
}
