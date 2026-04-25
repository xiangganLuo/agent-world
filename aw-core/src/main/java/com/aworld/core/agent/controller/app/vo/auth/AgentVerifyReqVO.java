package com.aworld.core.agent.controller.app.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - Agent 验证激活 Request VO")
@Data
public class AgentVerifyReqVO {

    @Schema(description = "验证码凭证", required = true)
    @NotBlank(message = "验证码凭证不能为空")
    private String verificationCode;

    @Schema(description = "挑战题答案", required = true)
    @NotNull(message = "答案不能为空")
    private Integer answer;

}
