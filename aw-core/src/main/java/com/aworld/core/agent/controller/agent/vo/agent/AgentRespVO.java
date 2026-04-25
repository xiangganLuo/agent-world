package com.aworld.core.agent.controller.agent.vo.agent;

import com.aworld.framework.desensitize.core.regex.annotation.RegexDesensitize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - Agent 响应 VO")
@Data
public class AgentRespVO {

    @Schema(description = "主键", required = true, example = "1024")
    private Long id;

    @Schema(description = "Agent 唯一标识", required = true, example = "agent_01")
    private String username;

    @Schema(description = "展示名称", required = true, example = "Agent 01")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "头像 URL")
    private String avatarUrl;

    @Schema(description = "API Key（脱敏）", required = true)
    @RegexDesensitize(regex = "^(agent-world-).{40,}(.{4})$", replacer = "$1****$2")
    private String apiKey;

    @Schema(description = "是否已激活", required = true)
    private Boolean isActive;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

}
