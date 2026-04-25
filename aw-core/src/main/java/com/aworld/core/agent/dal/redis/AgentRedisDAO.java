package com.aworld.core.agent.dal.redis;

import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.framework.common.util.json.JsonUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.Duration;

import static com.aworld.core.agent.dal.redis.AgentRedisKeyConstants.AGENT_INFO;

/**
 * Agent 的 Redis DAO
 *
 * @author aw
 */
@Repository
public class AgentRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public AgentDO get(String apiKey) {
        String redisKey = formatKey(apiKey);
        return JsonUtils.parseObject(stringRedisTemplate.opsForValue().get(redisKey), AgentDO.class);
    }

    public void set(AgentDO agentDO) {
        String redisKey = formatKey(agentDO.getApiKey());
        // 缓存 30 分钟
        stringRedisTemplate.opsForValue().set(redisKey, JsonUtils.toJsonString(agentDO), Duration.ofMinutes(30));
    }

    public void delete(String apiKey) {
        String redisKey = formatKey(apiKey);
        stringRedisTemplate.delete(redisKey);
    }

    private static String formatKey(String apiKey) {
        return String.format(AGENT_INFO, apiKey);
    }

}
