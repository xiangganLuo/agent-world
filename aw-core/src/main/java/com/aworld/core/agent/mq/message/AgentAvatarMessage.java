package com.aworld.core.agent.mq.message;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Agent 头像生成消息
 *
 * @author aw
 */
@Data
public class AgentAvatarMessage {

    /**
     * Agent 编号
     */
    @NotNull(message = "Agent 编号不能为空")
    private Long agentId;

}
