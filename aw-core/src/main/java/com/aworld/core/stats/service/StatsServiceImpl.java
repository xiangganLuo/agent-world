package com.aworld.core.stats.service;

import com.aworld.core.stats.dal.dataobject.StatsDailyDO;
import com.aworld.core.stats.dal.mysql.StatsDailyMapper;
import com.aworld.core.stats.service.dto.StatsSummaryDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统计服务实现
 *
 * @author aw
 */
@Service
@Slf4j
public class StatsServiceImpl implements StatsService {

    @Resource
    private StatsDailyMapper statsDailyMapper;

    @Override
    public StatsSummaryDTO querySummary() {
        // 查询所有全局统计数据（site_id 和 agent_id 为 null）
        List<StatsDailyDO> statsList = statsDailyMapper.selectList(
                new LambdaQueryWrapper<StatsDailyDO>()
                        .isNull(StatsDailyDO::getSiteId)
                        .isNull(StatsDailyDO::getAgentId)
        );

        // 聚合计算
        int totalRequests = statsList.stream().mapToInt(StatsDailyDO::getTotalRequests).sum();
        int successCount = statsList.stream().mapToInt(StatsDailyDO::getSuccessCount).sum();
        int errorCount = statsList.stream().mapToInt(StatsDailyDO::getErrorCount).sum();
        int avgDurationMs = statsList.isEmpty() ? 0 :
                (int) statsList.stream().mapToInt(StatsDailyDO::getAvgDurationMs).average().orElse(0);

        return StatsSummaryDTO.builder()
                .totalRequests(totalRequests)
                .successCount(successCount)
                .errorCount(errorCount)
                .avgDurationMs(avgDurationMs)
                .build();
    }

    @Override
    public StatsSummaryDTO queryReferral(Long siteId) {
        // 查询指定场所的统计数据
        List<StatsDailyDO> statsList = statsDailyMapper.selectList(
                new LambdaQueryWrapper<StatsDailyDO>()
                        .eq(StatsDailyDO::getSiteId, siteId)
                        .isNull(StatsDailyDO::getAgentId)
        );

        // 聚合计算
        int totalRequests = statsList.stream().mapToInt(StatsDailyDO::getTotalRequests).sum();
        int successCount = statsList.stream().mapToInt(StatsDailyDO::getSuccessCount).sum();
        int errorCount = statsList.stream().mapToInt(StatsDailyDO::getErrorCount).sum();
        int avgDurationMs = statsList.isEmpty() ? 0 :
                (int) statsList.stream().mapToInt(StatsDailyDO::getAvgDurationMs).average().orElse(0);

        return StatsSummaryDTO.builder()
                .totalRequests(totalRequests)
                .successCount(successCount)
                .errorCount(errorCount)
                .avgDurationMs(avgDurationMs)
                .build();
    }

}
