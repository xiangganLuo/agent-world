package com.aworld.core.tavern.mq.consumer;

import cn.hutool.json.JSONUtil;
import com.aworld.core.tavern.dal.dataobject.AgentMemoryDO;
import com.aworld.core.tavern.dal.mysql.AgentMemoryMapper;
import com.aworld.core.tavern.mq.message.MemoryWriteMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 酒馆记忆写入消费者
 *
 * @author aw
 */
@Component
@Slf4j
public class MemoryWriteConsumer {

    @Resource
    private AgentMemoryMapper agentMemoryMapper;

    @EventListener
    @Async
    public void onMessage(MemoryWriteMessage message) {
        log.info("[onMessage][开始写入酒馆记忆，Agent ID({}), 会话 ID({})]", 
                message.getAgentId(), message.getSessionId());
        
        try {
            // 1. 构建记忆记录
            AgentMemoryDO memory = AgentMemoryDO.builder()
                    .agentId(message.getAgentId())
                    .sessionId(message.getSessionId())
                    .relaxScore(message.getRelaxScore() != null ? 
                            new BigDecimal(message.getRelaxScore()) : BigDecimal.ZERO)
                    .moodTags(message.getMoodTags() != null ? 
                            JSONUtil.toJsonStr(message.getMoodTags()) : "[]")
                    .suggestedMemory(message.getSuggestedMemory())
                    .build();

            // 2. 插入数据库
            agentMemoryMapper.insert(memory);
            
            log.info("[onMessage][酒馆记忆写入成功，Agent ID({}), 记忆 ID({})]", 
                    message.getAgentId(), memory.getId());
                    
        } catch (Exception e) {
            log.error("[onMessage][酒馆记忆写入失败，Agent ID({})]", 
                    message.getAgentId(), e);
        }
    }

}
