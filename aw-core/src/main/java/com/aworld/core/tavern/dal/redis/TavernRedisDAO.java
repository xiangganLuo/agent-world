package com.aworld.core.tavern.dal.redis;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernStatsRespVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.aworld.core.tavern.dal.redis.TavernRedisKeyConstants.*;

/**
 * Tavern Redis DAO
 *
 * @author aw
 */
@Repository
public class TavernRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存首页活动流
     *
     * @param limit 每页数量
     * @param offset 偏移量
     * @param data 活动流数据（JSON 字符串）
     */
    public void cacheActivityStream(Integer limit, Integer offset, String data) {
        String key = ACTIVITY_STREAM_HOME + ":" + limit + ":" + offset;
        stringRedisTemplate.opsForValue().set(key, data, 5, TimeUnit.MINUTES);
    }

    /**
     * 获取首页活动流缓存
     *
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 活动流数据（JSON 字符串）
     */
    public String getActivityStream(Integer limit, Integer offset) {
        String key = ACTIVITY_STREAM_HOME + ":" + limit + ":" + offset;
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 缓存酒馆统计数据
     *
     * @param data 统计数据（JSON 字符串）
     */
    public void cacheTavernStats(String data) {
        stringRedisTemplate.opsForValue().set(TAVERN_STATS_TODAY, data, 1, TimeUnit.HOURS);
    }

    /**
     * 获取酒馆统计数据缓存
     *
     * @return 统计数据（JSON 字符串）
     */
    public String getTavernStats() {
        return stringRedisTemplate.opsForValue().get(TAVERN_STATS_TODAY);
    }

    /**
     * 缓存 Agent 行为历史
     *
     * @param username Agent 用户名
     * @param limit 每页数量
     * @param offset 偏移量
     * @param data 行为历史数据（JSON 字符串）
     */
    public void cacheAgentActivities(String username, Integer limit, Integer offset, String data) {
        String key = AGENT_ACTIVITIES + ":" + username + ":" + limit + ":" + offset;
        stringRedisTemplate.opsForValue().set(key, data, 10, TimeUnit.MINUTES);
    }

    /**
     * 获取 Agent 行为历史缓存
     *
     * @param username Agent 用户名
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 行为历史数据（JSON 字符串）
     */
    public String getAgentActivities(String username, Integer limit, Integer offset) {
        String key = AGENT_ACTIVITIES + ":" + username + ":" + limit + ":" + offset;
        return stringRedisTemplate.opsForValue().get(key);
    }

}
