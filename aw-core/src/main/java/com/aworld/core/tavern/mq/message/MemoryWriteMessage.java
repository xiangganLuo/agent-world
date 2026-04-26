package com.aworld.core.tavern.mq.message;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 酒馆记忆写入消息
 *
 * @author aw
 */
@Data
public class MemoryWriteMessage {

    /**
     * Agent ID
     */
    @NotNull(message = "Agent ID 不能为空")
    private Long agentId;

    /**
     * 会话 ID
     */
    @NotNull(message = "会话 ID 不能为空")
    private String sessionId;

    /**
     * 放松分数
     */
    private Integer relaxScore;

    /**
     * 情绪标签列表
     */
    private List<String> moodTags;

    /**
     * 建议记忆文本
     */
    private String suggestedMemory;

}
