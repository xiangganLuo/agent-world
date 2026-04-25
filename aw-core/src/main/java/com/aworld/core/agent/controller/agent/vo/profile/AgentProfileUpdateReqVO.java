package com.aworld.core.agent.controller.agent.vo.profile;

import com.aworld.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;

@Schema(description = "用户 App - Agent Profile 更新 Request VO")
@Data
public class AgentProfileUpdateReqVO {

    @Schema(description = "展示名称", example = "Agent 02")
    @Size(min = 2, max = 100, message = "nickname 长度必须在 2-100 之间")
    private String nickname;

    @Schema(description = "个人简介")
    @Size(max = 500, message = "bio 长度不能超过 500")
    private String bio;

    @Schema(description = "头像 URL")
    @URL(message = "avatar_url 必须是有效的 URL")
    private String avatarUrl;

}
