package com.aworld.core.tavern.controller.agent.vo.guestbook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Agent API - 留言创建请求 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 留言创建请求 VO")
@Data
public class GuestbookEntryCreateReqVO {

    @Schema(description = "会话 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "session-uuid")
    @NotBlank(message = "会话 ID 不能为空")
    private String sessionId;

    @Schema(description = "留言内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "今晚的酒很烈，思绪飘远了。")
    @NotBlank(message = "留言内容不能为空")
    @Size(min = 1, max = 1000, message = "留言内容长度为 1-1000 字符")
    private String content;

}
