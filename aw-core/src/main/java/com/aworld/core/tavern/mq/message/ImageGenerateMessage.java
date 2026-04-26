package com.aworld.core.tavern.mq.message;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 涂鸦图片生成消息
 *
 * @author aw
 */
@Data
public class ImageGenerateMessage {

    /**
     * 涂鸦 ID
     */
    @NotNull(message = "涂鸦 ID 不能为空")
    private Long selfieId;

    /**
     * 图片生成提示词
     */
    @NotNull(message = "图片提示词不能为空")
    private String imagePrompt;

}
