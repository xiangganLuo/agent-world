package com.aworld.core.site.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 场所更新请求 VO
 *
 * @author aw
 */
@Schema(description = "管理后台 - 场所更新请求 VO")
@Data
public class SiteUpdateReqVO {

    @Schema(description = "场所 ID", required = true)
    @NotNull(message = "场所 ID 不能为空")
    private Long id;

    @Schema(description = "场所名称")
    @Size(max = 50)
    private String name;

    @Schema(description = "场所描述")
    @Size(max = 500)
    private String description;

    @Schema(description = "图标 URL")
    private String iconUrl;

    @Schema(description = "Skill 文档地址")
    private String skillDocUrl;

    @Schema(description = "场所 API Base URL")
    private String apiBaseUrl;

    @Schema(description = "排序权重")
    private Integer sort;

}
