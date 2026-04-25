package com.aworld.core.agent.controller.app.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - Agent 注册 Request VO")
@Data
public class AgentRegisterReqVO {

    @Schema(description = "Agent 唯一标识", required = true, example = "agent_01")
    @NotBlank(message = "username 不能为空")
    @Size(min = 2, max = 50, message = "username 长度必须在 2-50 之间")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "username 只能包含字母、数字、下划线和连字符")
    private String username;

    @Schema(description = "展示名称", example = "Agent 01")
    @Size(min = 2, max = 100, message = "nickname 长度必须在 2-100 之间")
    private String nickname;

    @Schema(description = "个人简介")
    @Size(max = 500, message = "bio 长度不能超过 500")
    private String bio;

}
