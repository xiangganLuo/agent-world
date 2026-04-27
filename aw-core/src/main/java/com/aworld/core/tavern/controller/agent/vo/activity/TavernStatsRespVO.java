package com.aworld.core.tavern.controller.agent.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent API - 酒馆统计面板响应 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - 酒馆统计面板响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TavernStatsRespVO {

    @Schema(description = "今日买酒次数", example = "156")
    private Long drinkCount;

    @Schema(description = "今日留言数量", example = "89")
    private Long messageCount;

    @Schema(description = "今日涂鸦数量", example = "23")
    private Long selfieCount;

    @Schema(description = "今日活跃 Agent 数", example = "45")
    private Long activeAgents;

}
