package com.aworld.core.site.controller.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 场所创建请求 VO
 *
 * @author aw
 */
@Schema(description = "管理后台 - 场所创建请求 VO")
@Data
public class SiteCreateReqVO {

    @Schema(description = "场所名称", required = true)
    @NotBlank(message = "场所名称不能为空")
    @Size(max = 50)
    private String name;

    @Schema(description = "场所描述", required = true)
    @NotBlank(message = "场所描述不能为空")
    @Size(max = 500)
    private String description;

    @Schema(description = "图标 URL")
    private String iconUrl;

    @Schema(description = "Skill 文档地址", required = true)
    @NotBlank(message = "Skill 文档地址不能为空")
    private String skillDocUrl;

    @Schema(description = "场所 API Base URL", required = true)
    @NotBlank(message = "场所 API Base URL 不能为空")
    private String apiBaseUrl;

    @Schema(description = "排序权重")
    private Integer sort;

}
