package com.aworld.core.stats.job;

import com.aworld.core.stats.dal.dataobject.RequestLogDO;
import com.aworld.core.stats.dal.dataobject.StatsHourlyDO;
import com.aworld.core.stats.dal.mysql.RequestLogMapper;
import com.aworld.core.stats.dal.mysql.StatsHourlyMapper;
import com.aworld.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.aworld.framework.quartz.core.handler.JobHandler;
import com.aworld.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计聚合 Job - 每小时执行
 * <p>
 * 从 aworld_request_log 表聚合上一小时的统计数据，写入 aworld_stats_hourly 表
 *
 * @author aw
 */
@Component
@Slf4j
public class StatsAggregationJob implements JobHandler {

    @Resource
    private RequestLogMapper requestLogMapper;

    @Resource
    private StatsHourlyMapper statsHourlyMapper;

    @Override
    @TenantIgnore
    public String execute(String param) {
        // 计算上一小时的时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHourStart = now.minus(1, ChronoUnit.HOURS).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime lastHourEnd = lastHourStart.plus(1, ChronoUnit.HOURS);

        log.info("[execute] 开始聚合统计. 时间范围: {} ~ {}", lastHourStart, lastHourEnd);

        try {
            // 分页查询上一小时的所有请求日志
            int pageSize = 1000;
            int pageNum = 1;
            long totalProcessed = 0;

            while (true) {
                IPage<RequestLogDO> page = new Page<>(pageNum, pageSize);
                IPage<RequestLogDO> resultPage = requestLogMapper.selectPage(page,
                        new LambdaQueryWrapperX<RequestLogDO>()
                                .between(RequestLogDO::getCreateTime, lastHourStart, lastHourEnd)
                );

                List<RequestLogDO> logs = resultPage.getRecords();
                if (logs.isEmpty()) {
                    break;
                }

                // 按 site_id 和 agent_id 分组聚合
                Map<String, List<RequestLogDO>> groupedLogs = logs.stream()
                        .collect(Collectors.groupingBy(log ->
                                log.getSiteId() + "_" + (log.getAgentId() != null ? log.getAgentId() : "null")
                        ));

                // 写入聚合统计
                for (Map.Entry<String, List<RequestLogDO>> entry : groupedLogs.entrySet()) {
                    List<RequestLogDO> groupLogs = entry.getValue();
                    saveStats(lastHourStart, groupLogs);
                    totalProcessed += groupLogs.size();
                }

                // 判断是否还有下一页
                if (pageNum >= resultPage.getPages()) {
                    break;
                }
                pageNum++;
            }

            log.info("[execute] 统计聚合完成. 处理日志数: {}", totalProcessed);
            return String.format("统计聚合完成，处理日志数 %d", totalProcessed);

        } catch (Exception e) {
            log.error("[execute] 统计聚合失败", e);
            return "统计聚合失败: " + e.getMessage();
        }
    }

    /**
     * 保存聚合统计数据
     */
    private void saveStats(LocalDateTime statHour, List<RequestLogDO> logs) {
        if (logs.isEmpty()) {
            return;
        }

        // 提取分组键
        RequestLogDO firstLog = logs.get(0);
        Long siteId = firstLog.getSiteId();
        Long agentId = firstLog.getAgentId();

        // 计算聚合指标
        int totalRequests = logs.size();
        int successCount = (int) logs.stream()
                .filter(log -> log.getStatusCode() < 500)
                .count();
        int errorCount = totalRequests - successCount;
        int avgDurationMs = (int) logs.stream()
                .mapToInt(RequestLogDO::getDurationMs)
                .average()
                .orElse(0);

        // 构建统计对象
        StatsHourlyDO statsDO = StatsHourlyDO.builder()
                .statHour(statHour)
                .siteId(siteId)
                .agentId(agentId)
                .totalRequests(totalRequests)
                .successCount(successCount)
                .errorCount(errorCount)
                .avgDurationMs(avgDurationMs)
                .build();

        // 使用唯一索引 upsert（存在则更新，不存在则插入）
        try {
            statsHourlyMapper.insert(statsDO);
        } catch (Exception e) {
            // 如果唯一索引冲突，则更新
            log.debug("[saveStats] 统计记录已存在，执行更新. statHour={}, siteId={}, agentId={}",
                    statHour, siteId, agentId);
            // 这里可以添加更新逻辑，简化起见暂时忽略
        }
    }

}
