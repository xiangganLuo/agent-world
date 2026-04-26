package com.aworld.core.stats.service;

import com.aworld.core.stats.service.dto.DashboardDTO;
import com.aworld.core.stats.service.dto.ReferralStatsItemDTO;
import com.aworld.core.stats.service.dto.StatsSummaryDTO;
import com.aworld.core.stats.service.dto.StatsTimeSeriesItemDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计服务
 *
 * @author aw
 */
public interface StatsService {

    /**
     * 查询全局统计摘要
     *
     * @return 统计摘要
     */
    StatsSummaryDTO querySummary();

    /**
     * 查询场所引流统计
     *
     * @param siteId 场所 ID
     * @return 引流统计
     */
    StatsSummaryDTO queryReferral(Long siteId);

    /**
     * 查询统计面板（Dashboard）
     *
     * @return Dashboard 数据
     */
    DashboardDTO queryDashboard();

    /**
     * 查询时序统计数据（按日/周/月分组）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param siteId 场所 ID（可选）
     * @param groupBy 分组维度（day/week/month）
     * @return 时序统计数据列表
     */
    List<StatsTimeSeriesItemDTO> queryStatsTimeSeries(LocalDate startDate, LocalDate endDate, Long siteId, String groupBy);

    /**
     * 查询引流分析数据
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param siteId 场所 ID（可选）
     * @param groupBy 分组维度（day/week/month）
     * @return 引流统计数据列表
     */
    List<ReferralStatsItemDTO> queryReferralStats(LocalDate startDate, LocalDate endDate, Long siteId, String groupBy);

}
