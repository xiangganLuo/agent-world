package com.aworld.core.stats.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计摘要 DTO
 *
 * @author aw
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsSummaryDTO {

    /**
     * 总请求数
     */
    private Integer totalRequests;

    /**
     * 成功请求数
     */
    private Integer successCount;

    /**
     * 错误请求数
     */
    private Integer errorCount;

    /**
     * 平均响应耗时（毫秒）
     */
    private Integer avgDurationMs;

}
