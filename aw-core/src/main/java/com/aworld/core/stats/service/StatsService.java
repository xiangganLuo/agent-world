package com.aworld.core.stats.service;

import com.aworld.core.stats.service.dto.StatsSummaryDTO;

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

}
