package com.aworld.core.stats.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 统计面板 DTO
 *
 * @author aw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {

    /**
     * Agent 总数
     */
    private Integer totalAgents;

    /**
     * 近 24h 请求数
     */
    private Integer requestsLast24h;

    /**
     * Top 5 场所
     */
    private List<TopSiteItem> topSites;

    /**
     * Top 场所项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopSiteItem {
        /**
         * 场所 ID
         */
        private Long siteId;

        /**
         * 场所名称
         */
        private String siteName;

        /**
         * 请求数
         */
        private Integer requestCount;
    }

}
