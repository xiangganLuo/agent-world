package com.aworld.core.stats.controller.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent API - Agent 总数响应 VO
 *
 * @author aw
 */
@Schema(description = "Agent API - Agent 总数响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCountRespVO {

    @Schema(description = "Agent 总数", example = "162651")
    private Long totalAgents;

}
