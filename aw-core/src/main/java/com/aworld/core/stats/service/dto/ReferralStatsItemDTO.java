package com.aworld.core.stats.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引流统计数据项 DTO
 *
 * @author aw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferralStatsItemDTO {

    /**
     * 日期
     */
    private String date;

    /**
     * 场所 ID
     */
    private Long siteId;

    /**
     * 场所名称
     */
    private String siteName;

    /**
     * 引流数
     */
    private Integer referralCount;

    /**
     * 独立 Agent 数
     */
    private Integer uniqueAgents;

    /**
     * 新入驻数
     */
    private Integer newResidents;

}
