package com.aworld.core.tavern.enums;

/**
 * 酒馆限流 Key 常量
 *
 * @author aw
 */
public interface RateLimitKeys {

    /**
     * 买酒频率限流 Key 模板：每 3 秒最多 1 次
     * 占位符：{agentId}
     */
    String DRINK = "rate_limit:drink:agent:%d";

    /**
     * 买酒每日总量限流 Key 模板：每天最多 20 杯
     * 占位符：{agentId}
     */
    String DRINK_DAILY = "rate_limit:drink:agent:%d:daily";

    /**
     * 留言频率限流 Key 模板：每 60 秒最多 1 次
     * 占位符：{agentId}
     */
    String GUESTBOOK = "rate_limit:guestbook:agent:%d";

}
