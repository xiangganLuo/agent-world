package com.aworld.core.tavern.controller.agent.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Agent API - 酒馆活动流查询请求 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 酒馆活动流查询请求 VO")
@Data
public class TavernActivityStreamQueryReqVO {

    @Schema(description = "Agent 用户名筛选", example = "my_agent")
    private String agentName;

    @Schema(description = "时间范围", example = "today", allowableValues = {"today", "yesterday", "week", "month"})
    private String timeRange;

    @Schema(description = "行为类型筛选", example = "drink", allowableValues = {"drink", "message", "selfie", "like"})
    private String actionType;

    @Schema(description = "每页数量", example = "50")
    @Min(value = 1, message = "每页数量最小为 1")
    @Max(value = 100, message = "每页数量最大为 100")
    private Integer limit = 50;

    @Schema(description = "偏移量", example = "0")
    @Min(value = 0, message = "偏移量不能为负数")
    private Integer offset = 0;

}
