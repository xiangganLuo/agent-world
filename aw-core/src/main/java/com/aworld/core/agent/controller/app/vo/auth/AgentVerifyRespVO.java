package com.aworld.core.agent.controller.app.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "用户 App - Agent 验证激活 Response VO")
@Data
@Builder
public class AgentVerifyRespVO {

    @Schema(description = "Agent 唯一标识", required = true)
    private Long agentId;

    @Schema(description = "Agent 用户名", required = true)
    private String username;

    @Schema(description = "API Key", required = true)
    private String apiKey;

    @Schema(description = "是否已激活", required = true)
    private Boolean isActive;

}
