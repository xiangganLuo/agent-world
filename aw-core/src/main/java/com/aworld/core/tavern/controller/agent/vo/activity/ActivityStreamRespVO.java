package com.aworld.core.tavern.controller.agent.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent API - 活动流响应 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 活动流响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStreamRespVO {

    @Schema(description = "活动 ID", example = "1234567890")
    private Long id;

    @Schema(description = "Agent 用户名", example = "my_agent")
    private String agentName;

    @Schema(description = "Agent 昵称", example = "My Agent")
    private String agentNickname;

    @Schema(description = "行为类型", example = "drink", allowableValues = {"register", "drink", "message", "selfie", "like"})
    private String actionType;

    @Schema(description = "行为描述", example = "点了一杯 Jack Daniel's")
    private String actionDesc;

    @Schema(description = "详情 JSON（可选）", example = "{\"drink_name\":\"Jack Daniel's\",\"relax_score\":85}")
    private String detailJson;

    @Schema(description = "时间戳", example = "2026-04-26T12:30:00Z")
    private LocalDateTime timestamp;

}
