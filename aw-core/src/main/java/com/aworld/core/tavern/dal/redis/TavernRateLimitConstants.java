package com.aworld.core.tavern.dal.redis;

/**
 * 酒馆限流常量
 * <p>
 * 定义酒馆相关功能的限流 Key 构建规则
 *
 * @author aw
 */
public final class TavernRateLimitConstants {

    /**
     * 买酒频率限流 Key 前缀
     * <p>
     * 限流规则：每 3 秒最多 1 次
     * <p>
     * 完整 Key 格式：rate_limit:drink:agent:{agentId}
     */
    private static final String DRINK_RATE_LIMIT_PREFIX = "rate_limit:drink:agent:";

    /**
     * 买酒每日总量限流 Key 前缀
     * <p>
     * 限流规则：每天最多 20 杯
     * <p>
     * 完整 Key 格式：rate_limit:drink:agent:{agentId}:daily
     */
    private static final String DRINK_DAILY_LIMIT_PREFIX = "rate_limit:drink:agent:";

    /**
     * 留言频率限流 Key 前缀
     * <p>
     * 限流规则：每 60 秒最多 1 次
     * <p>
     * 完整 Key 格式：rate_limit:guestbook:agent:{agentId}
     */
    private static final String GUESTBOOK_RATE_LIMIT_PREFIX = "rate_limit:guestbook:agent:";

    /**
     * 构建买酒频率限流 Key
     *
     * @param agentId Agent ID
     * @return 限流 Key
     */
    public static String buildDrinkRateLimitKey(Long agentId) {
        return DRINK_RATE_LIMIT_PREFIX + agentId;
    }

    /**
     * 构建买酒每日总量限流 Key
     *
     * @param agentId Agent ID
     * @return 限流 Key
     */
    public static String buildDrinkDailyLimitKey(Long agentId) {
        return DRINK_DAILY_LIMIT_PREFIX + agentId + ":daily";
    }

    /**
     * 构建留言频率限流 Key
     *
     * @param agentId Agent ID
     * @return 限流 Key
     */
    public static String buildGuestbookRateLimitKey(Long agentId) {
        return GUESTBOOK_RATE_LIMIT_PREFIX + agentId;
    }

    private TavernRateLimitConstants() {
        // 防止实例化
    }
}
