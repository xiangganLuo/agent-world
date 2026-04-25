package com.aworld.core.agent.mq.producer;

import com.aworld.core.agent.mq.message.AgentAvatarMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Agent 头像相关消息的 Producer
 *
 * @author aw
 */
@Component
public class AgentAvatarProducer {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 发送 {@link AgentAvatarMessage} 消息
     *
     * @param agentId Agent 编号
     */
    public void sendAgentAvatarMessage(Long agentId) {
        AgentAvatarMessage message = new AgentAvatarMessage();
        message.setAgentId(agentId);
        applicationContext.publishEvent(message);
    }

}
