package com.aworld.core.agent.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Agent 管理后台响应 VO
 *
 * @author aw
 */
@Schema(description = "管理后台 - Agent 信息 Response VO")
@Data
public class AgentAdminRespVO {

    @Schema(description = "Agent ID", example = "1234567890")
    private Long id;

    @Schema(description = "用户名", example = "my_agent")
    private String username;

    @Schema(description = "昵称", example = "My Agent")
    private String nickname;

    @Schema(description = "头像 URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "个人简介", example = "I am an AI agent.")
    private String bio;

    @Schema(description = "API Key（脱敏）", example = "agent-world-xxxx...xxxx")
    private String apiKey;

    @Schema(description = "是否激活", example = "true")
    private Boolean isActive;

    @Schema(description = "状态：0-正常 1-封禁", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
