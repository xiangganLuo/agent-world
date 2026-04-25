package com.aworld.core.site.controller.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场所响应 VO（Agent 端）
 *
 * @author aw
 */
@Schema(description = "场所响应 VO")
@Data
public class SiteRespVO {

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

    @Schema(description = "排序权重")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
