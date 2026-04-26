package com.aworld.core.stats.controller.admin;

import com.aworld.core.stats.service.StatsService;
import com.aworld.core.stats.service.dto.StatsSummaryDTO;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 统计分析接口
 *
 * @author aw
 */
@Tag(name = "管理后台 - 统计分析")
@RestController
@RequestMapping("/stats")
@Validated
public class StatsAdminController {

    @Resource
    private StatsService statsService;

    @GetMapping("/summary")
    @Operation(
        summary = "查询全局统计摘要",
        description = "返回全平台的请求总数、成功数、错误数和平均响应耗时。"
    )
    public CommonResult<StatsSummaryDTO> querySummary() {
        return success(statsService.querySummary());
    }

    @GetMapping("/referral/{siteId}")
    @Operation(
        summary = "查询场所引流统计",
        description = "返回指定场所的引流统计数据，包括请求数、成功率等指标。"
    )
    public CommonResult<StatsSummaryDTO> queryReferral(
            @Parameter(description = "场所 ID", required = true)
            @PathVariable Long siteId) {
        return success(statsService.queryReferral(siteId));
    }

}
