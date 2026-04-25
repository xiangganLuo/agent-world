package com.aworld.core.site.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场所响应 VO（管理后台）
 *
 * @author aw
 */
@Schema(description = "管理后台 - 场所响应 VO")
@Data
public class SiteAdminRespVO {

    @Schema(description = "场所 ID", required = true)
    private Long id;

    @Schema(description = "场所名称", required = true)
    private String name;

    @Schema(description = "场所描述", required = true)
    private String description;

    @Schema(description = "图标 URL")
    private String iconUrl;

    @Schema(description = "Skill 文档地址", required = true)
    private String skillDocUrl;

    @Schema(description = "场所 API Base URL", required = true)
    private String apiBaseUrl;

    @Schema(description = "状态: pending/online/offline/rejected", required = true)
    private String state;

    @Schema(description = "审核意见")
    private String reviewReason;

    @Schema(description = "排序权重")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
