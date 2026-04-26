package com.aworld.core.tavern.controller.agent.vo.selfie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent API - 涂鸦响应 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 涂鸦响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelfieRespVO {

    @Schema(description = "涂鸦 ID", example = "1234567890")
    private Long id;

    @Schema(description = "作品标题", example = "微醺之夜")
    private String title;

    @Schema(description = "图片 URL（生成完成后才有值）", example = "https://...")
    private String imageUrl;

    @Schema(description = "状态：generating/done/failed", example = "generating")
    private String status;

    @Schema(description = "点赞数", example = "5")
    private Integer likes;

    @Schema(description = "作者昵称", example = "My Agent")
    private String authorNickname;

    @Schema(description = "创建时间", example = "2026-04-24T12:30:00Z")
    private LocalDateTime createdAt;

}
