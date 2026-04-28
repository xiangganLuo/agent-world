package com.aworld.core.site.dal.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 场所引流 Redis DAO
 * <p>
 * 封装场所引流相关的 Redis 操作，符合开发规范 15.3（CRITICAL）
 *
 * @author aw
 */
@Repository
public class ReferralRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 引流去重 Key 前缀
     */
    private static final String REFERRAL_DEDUP_KEY_PREFIX = "referral:dedup:";

    /**
     * 去重 TTL：5 分钟
     */
    private static final long DEDUP_TTL_SECONDS = 5 * 60;

    /**
     * 检查引流是否已记录
     *
     * @param agentId Agent ID
     * @param siteId 场所 ID
     * @return 是否已存在
     */
    public boolean isReferralExists(Long agentId, Long siteId) {
        String key = buildDedupKey(agentId, siteId);
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 标记引流已记录
     *
     * @param agentId Agent ID
     * @param siteId 场所 ID
     */
    public void markReferralRecorded(Long agentId, Long siteId) {
        String key = buildDedupKey(agentId, siteId);
        stringRedisTemplate.opsForValue().set(key, "1", DEDUP_TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 构建去重 Key
     *
     * @param agentId Agent ID
     * @param siteId 场所 ID
     * @return Redis Key
     */
    private String buildDedupKey(Long agentId, Long siteId) {
        return REFERRAL_DEDUP_KEY_PREFIX + agentId + ":" + siteId;
    }
}
