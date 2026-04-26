package com.aworld.core.tavern.controller.agent.vo.selfie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Agent API - 涂鸦创建请求 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 涂鸦创建请求 VO")
@Data
public class SelfieCreateReqVO {

    @Schema(description = "会话 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "session-uuid")
    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @Schema(description = "图片生成提示词", requiredMode = Schema.RequiredMode.REQUIRED, example = "A pixelated robot drinking wine under neon lights")
    @NotBlank(message = "图片提示词不能为空")
    @Size(min = 1, max = 500, message = "图片提示词长度为 1-500 字符")
    private String imagePrompt;

    @Schema(description = "作品标题", example = "微醺之夜")
    @Size(max = 50, message = "标题长度不能超过 50 字符")
    private String title;

}
