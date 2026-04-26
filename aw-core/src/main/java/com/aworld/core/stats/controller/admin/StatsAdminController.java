package com.aworld.core.stats.controller.admin;

import com.aworld.core.stats.service.StatsService;
import com.aworld.core.stats.service.dto.DashboardDTO;
import com.aworld.core.stats.service.dto.ReferralStatsItemDTO;
import com.aworld.core.stats.service.dto.StatsTimeSeriesItemDTO;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 统计分析接口
 *
 * @author aw
 */
@Tag(name = "管理后台 - 统计分析")
@RestController
@RequestMapping("/core/stats")
@Validated
public class StatsAdminController {

    @Resource
    private StatsService statsService;

    @GetMapping("/summary")
    @Operation(
        summary = "查询时序统计数据",
        description = "返回按日/周/月分组的请求统计数据，用于折线图展示。"
    )
    public CommonResult<List<StatsTimeSeriesItemDTO>> querySummary(
            @Parameter(description = "开始日期 YYYY-MM-DD") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 YYYY-MM-DD") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "场所 ID（可选）") @RequestParam(required = false) Long siteId,
            @Parameter(description = "分组维度：day/week/month") @RequestParam(defaultValue = "day") String groupBy) {
        return success(statsService.queryStatsTimeSeries(startDate, endDate, siteId, groupBy));
    }

    @GetMapping("/referral")
    @Operation(
        summary = "查询引流分析数据",
        description = "返回按场所分组的引流统计数据。"
    )
    public CommonResult<List<ReferralStatsItemDTO>> queryReferral(
            @Parameter(description = "开始日期 YYYY-MM-DD") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "结束日期 YYYY-MM-DD") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "场所 ID（可选）") @RequestParam(required = false) Long siteId,
            @Parameter(description = "分组维度：day/week/month") @RequestParam(defaultValue = "day") String groupBy) {
        return success(statsService.queryReferralStats(startDate, endDate, siteId, groupBy));
    }

    @GetMapping("/dashboard")
    @Operation(
        summary = "查询统计面板",
        description = "返回 Agent 总数、近 24h 请求数、Top 5 场所等关键指标。"
    )
    public CommonResult<DashboardDTO> queryDashboard() {
        return success(statsService.queryDashboard());
    }

}
