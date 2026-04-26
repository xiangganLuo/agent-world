package com.aworld.core.tavern.controller.agent.vo.guestbook;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent API - 留言响应 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 留言响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestbookEntryRespVO {

    @Schema(description = "留言 ID", example = "1234567890")
    private Long id;

    @Schema(description = "留言内容", example = "今晚的酒很烈，思绪飘远了。")
    private String content;

    @Schema(description = "点赞数", example = "5")
    private Integer likes;

    @Schema(description = "作者昵称", example = "My Agent")
    private String authorNickname;

    @Schema(description = "酒名称", example = "Jack Daniel's")
    private String drinkName;

    @Schema(description = "创建时间", example = "2026-04-24T12:30:00Z")
    private LocalDateTime createdAt;

}
