package com.aworld.core.tavern.dal.redis;

/**
 * Tavern Redis Key 常量
 *
 * @author aw
 */
public interface TavernRedisKeyConstants {

    /**
     * Spring Cache 缓存名称 - 首页活动流
     */
    String CACHE_ACTIVITY_STREAM = "tavern:activity_stream";

    /**
     * Spring Cache 缓存名称 - 酒馆统计面板
     */
    String CACHE_TAVERN_STATS = "tavern:stats";

    /**
     * Spring Cache 缓存名称 - Agent 行为历史
     */
    String CACHE_AGENT_ACTIVITIES = "tavern:agent_activities";

    /**
     * Redis Key 前缀 - 首页活动流（手动操作时使用）
     * 格式: activity_stream:home:{limit}:{offset}
     */
    String ACTIVITY_STREAM_HOME = "activity_stream:home";

    /**
     * Redis Key 前缀 - 酒馆统计面板（手动操作时使用）
     * 格式: tavern:stats:today
     */
    String TAVERN_STATS_TODAY = "tavern:stats:today";

    /**
     * Redis Key 前缀 - Agent 行为历史（手动操作时使用）
     * 格式: agent_activities:{username}:{limit}:{offset}
     */
    String AGENT_ACTIVITIES = "agent_activities";

}
