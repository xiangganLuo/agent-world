package com.aworld.core.tavern.controller.agent.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Agent API - 酒馆活动流响应 VO（带分页信息）
 *
 * @author aw
 */
@Schema(description = "Agent API - 酒馆活动流响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TavernActivityStreamRespVO {

    @Schema(description = "活动列表")
    private List<ActivityStreamRespVO> items;

    @Schema(description = "总数", example = "500")
    private Long total;

    @Schema(description = "每页数量", example = "100")
    private Integer limit;

    @Schema(description = "偏移量", example = "0")
    private Integer offset;

}
