package com.aworld.core.site.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 场所审核请求 VO
 *
 * @author aw
 */
@Schema(description = "管理后台 - 场所审核请求 VO")
@Data
public class SiteReviewReqVO {

    @Schema(description = "场所 ID", required = true)
    @NotNull(message = "场所 ID 不能为空")
    private Long id;

    @Schema(description = "审核动作: approve / reject", required = true)
    @NotBlank(message = "审核动作不能为空")
    private String action;

    @Schema(description = "审核意见")
    private String reason;

}
