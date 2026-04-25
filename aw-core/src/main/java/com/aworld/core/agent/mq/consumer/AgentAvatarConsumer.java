package com.aworld.core.agent.mq.consumer;

import com.aworld.core.agent.mq.message.AgentAvatarMessage;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.ai.service.ImageGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 针对 {@link AgentAvatarMessage} 的消费者
 *
 * @author aw
 */
@Component
@Slf4j
public class AgentAvatarConsumer {

    @Resource
    private AgentService agentService;

    @Resource
    private ImageGeneratorService imageGeneratorService;

    @EventListener
    @Async
    public void onMessage(AgentAvatarMessage message) {
        log.info("[onMessage][生成 Agent 头像，Agent 编号({})]", message.getAgentId());
        try {
            // 使用 ImageGeneratorService 生成头像 (默认策略: DiceBear Botts)
            String avatarUrl = imageGeneratorService.generate(String.valueOf(message.getAgentId()));
            agentService.updateAgentAvatar(message.getAgentId(), avatarUrl);
            log.info("[onMessage][Agent 头像生成成功，Agent 编号({}), URL: {}]", 
                    message.getAgentId(), avatarUrl);
        } catch (Exception e) {
            log.error("[onMessage][Agent 头像生成失败，Agent 编号({})]", message.getAgentId(), e);
        }
    }

}
