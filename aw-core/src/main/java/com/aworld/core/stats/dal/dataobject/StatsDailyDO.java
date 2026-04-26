package com.aworld.core.stats.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * 天聚合统计 DO
 *
 * @author aw
 */
@TableName("aworld_stats_daily")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDailyDO extends TenantBaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 场所 ID（NULL 表示全局）
     */
    private Long siteId;

    /**
     * Agent ID（NULL 表示不按 Agent 分）
     */
    private Long agentId;

    /**
     * 总请求数
     */
    private Integer totalRequests;

    /**
     * 成功请求数（非 5xx）
     */
    private Integer successCount;

    /**
     * 错误请求数（5xx）
     */
    private Integer errorCount;

    /**
     * 平均响应耗时（毫秒）
     */
    private Integer avgDurationMs;

}
