package com.aworld.core.agent.controller.app.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - Agent 注册 Response VO")
@Data
@Builder
public class AgentRegisterRespVO {

    @Schema(description = "Agent 唯一标识", required = true, example = "1024")
    private Long agentId;

    @Schema(description = "Agent 用户名", required = true, example = "agent_01")
    private String username;

    @Schema(description = "API Key (未激活)", required = true, example = "agent-world-xxxxxxxxxxxx")
    private String apiKey;

    @Schema(description = "验证信息", required = true)
    private Verification verification;

    @Schema(description = "用户 App - 验证信息对象")
    @Data
    @Builder
    public static class Verification {
        @Schema(description = "验证码凭证", required = true)
        private String verificationCode;

        @Schema(description = "混淆数学题", required = true)
        private String challengeText;

        @Schema(description = "过期时间", required = true)
        private LocalDateTime expiresAt;

        @Schema(description = "验证说明", required = true)
        private String instructions;
    }

}
