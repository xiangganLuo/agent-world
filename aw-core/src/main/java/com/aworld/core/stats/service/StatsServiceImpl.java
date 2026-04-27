package com.aworld.core.stats.service;

import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.dal.mysql.AgentMapper;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.dal.dataobject.SiteReferralEventDO;
import com.aworld.core.site.dal.dataobject.SiteResidencyDO;
import com.aworld.core.site.dal.mysql.SiteMapper;
import com.aworld.core.site.dal.mysql.SiteReferralEventMapper;
import com.aworld.core.site.dal.mysql.SiteResidencyMapper;
import com.aworld.core.stats.controller.agent.vo.AgentCountRespVO;
import com.aworld.core.stats.dal.dataobject.RequestLogDO;
import com.aworld.core.stats.dal.dataobject.StatsDailyDO;
import com.aworld.core.stats.dal.mysql.RequestLogMapper;
import com.aworld.core.stats.dal.mysql.StatsDailyMapper;
import com.aworld.core.stats.service.dto.DashboardDTO;
import com.aworld.core.stats.service.dto.ReferralStatsItemDTO;
import com.aworld.core.stats.service.dto.StatsSummaryDTO;
import com.aworld.core.stats.service.dto.StatsTimeSeriesItemDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private RequestLogMapper requestLogMapper;

    @Resource
    private SiteReferralEventMapper referralEventMapper;

    @Resource
    private SiteResidencyMapper residencyMapper;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private AgentMapper agentMapper;

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

    @Override
    public DashboardDTO queryDashboard() {
        // 1. Agent 总数
        Long totalAgents = agentMapper.selectCount(null);

        // 2. 近 24h 请求数（从 request_log 表查询）
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        Integer requestsLast24h = requestLogMapper.selectCount(
                new LambdaQueryWrapper<RequestLogDO>()
                        .ge(RequestLogDO::getCreateTime, last24h)
        ).intValue();

        // 3. Top 5 场所（按最近 7 天请求数排序）
        LocalDate last7Days = LocalDate.now().minusDays(7);
        List<StatsDailyDO> siteStats = statsDailyMapper.selectList(
                new LambdaQueryWrapper<StatsDailyDO>()
                        .isNotNull(StatsDailyDO::getSiteId)
                        .ge(StatsDailyDO::getStatDate, last7Days)
                        .isNull(StatsDailyDO::getAgentId)
        );

        // 按场所 ID 分组并求和
        Map<Long, Integer> siteRequestMap = siteStats.stream()
                .collect(Collectors.groupingBy(
                        StatsDailyDO::getSiteId,
                        Collectors.summingInt(StatsDailyDO::getTotalRequests)
                ));

        // 取 Top 5
        List<DashboardDTO.TopSiteItem> topSites = siteRequestMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    SiteDO site = siteMapper.selectById(entry.getKey());
                    return DashboardDTO.TopSiteItem.builder()
                            .siteId(entry.getKey())
                            .siteName(site != null ? site.getName() : "未知场所")
                            .requestCount(entry.getValue())
                            .build();
                })
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalAgents(totalAgents.intValue())
                .requestsLast24h(requestsLast24h)
                .topSites(topSites)
                .build();
    }

    @Override
    public List<StatsTimeSeriesItemDTO> queryStatsTimeSeries(LocalDate startDate, LocalDate endDate, Long siteId, String groupBy) {
        // 构建查询条件
        LambdaQueryWrapper<StatsDailyDO> queryWrapper = new LambdaQueryWrapper<StatsDailyDO>()
                .between(StatsDailyDO::getStatDate, startDate, endDate)
                .isNull(StatsDailyDO::getAgentId);

        if (siteId != null) {
            queryWrapper.eq(StatsDailyDO::getSiteId, siteId);
        } else {
            queryWrapper.isNull(StatsDailyDO::getSiteId); // 全局统计
        }

        queryWrapper.orderByAsc(StatsDailyDO::getStatDate);

        List<StatsDailyDO> statsList = statsDailyMapper.selectList(queryWrapper);

        // 如果按周或月分组，需要进一步聚合
        if ("week".equals(groupBy) || "month".equals(groupBy)) {
            return aggregateByPeriod(statsList, groupBy);
        }

        // 按日返回
        return statsList.stream()
                .map(stat -> StatsTimeSeriesItemDTO.builder()
                        .date(stat.getStatDate().toString())
                        .totalRequests(stat.getTotalRequests())
                        .successCount(stat.getSuccessCount())
                        .errorCount(stat.getErrorCount())
                        .avgDurationMs(stat.getAvgDurationMs())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ReferralStatsItemDTO> queryReferralStats(LocalDate startDate, LocalDate endDate, Long siteId, String groupBy) {
        // 查询引流事件
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        LambdaQueryWrapper<SiteReferralEventDO> queryWrapper = new LambdaQueryWrapper<SiteReferralEventDO>()
                .between(SiteReferralEventDO::getCreateTime, startDateTime, endDateTime);

        if (siteId != null) {
            queryWrapper.eq(SiteReferralEventDO::getSiteId, siteId);
        }

        List<SiteReferralEventDO> events = referralEventMapper.selectList(queryWrapper);

        // 按日期和场所分组
        Map<String, Map<Long, List<SiteReferralEventDO>>> groupedEvents = events.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getCreateTime().toLocalDate().toString(),
                        Collectors.groupingBy(SiteReferralEventDO::getSiteId)
                ));

        // 转换为 DTO
        List<ReferralStatsItemDTO> result = new ArrayList<>();
        for (Map.Entry<String, Map<Long, List<SiteReferralEventDO>>> dateEntry : groupedEvents.entrySet()) {
            String date = dateEntry.getKey();
            for (Map.Entry<Long, List<SiteReferralEventDO>> siteEntry : dateEntry.getValue().entrySet()) {
                Long sId = siteEntry.getKey();
                List<SiteReferralEventDO> siteEvents = siteEntry.getValue();

                SiteDO site = siteMapper.selectById(sId);
                long uniqueAgents = siteEvents.stream()
                        .map(SiteReferralEventDO::getAgentId)
                        .distinct()
                        .count();

                // 计算新入驻数：在该日期首次入驻该场所的 Agent 数量
                LocalDateTime dayStart = LocalDate.parse(date).atStartOfDay();
                LocalDateTime dayEnd = LocalDate.parse(date).plusDays(1).atStartOfDay();
                
                List<SiteResidencyDO> newResidencies = residencyMapper.selectList(
                        new LambdaQueryWrapper<SiteResidencyDO>()
                                .eq(SiteResidencyDO::getSiteId, sId)
                                .between(SiteResidencyDO::getFirstVisitedAt, dayStart, dayEnd)
                );
                
                int newResidents = (int) newResidencies.stream()
                        .map(SiteResidencyDO::getAgentId)
                        .distinct()
                        .count();

                ReferralStatsItemDTO item = ReferralStatsItemDTO.builder()
                        .date(date)
                        .siteId(sId)
                        .siteName(site != null ? site.getName() : "未知场所")
                        .referralCount(siteEvents.size())
                        .uniqueAgents((int) uniqueAgents)
                        .newResidents(newResidents)
                        .build();

                result.add(item);
            }
        }

        // 按日期排序
        result.sort(Comparator.comparing(ReferralStatsItemDTO::getDate));

        return result;
    }

    /**
     * 按周期聚合统计数据（周/月）
     */
    private List<StatsTimeSeriesItemDTO> aggregateByPeriod(List<StatsDailyDO> statsList, String groupBy) {
        Map<String, List<StatsDailyDO>> grouped = statsList.stream()
                .collect(Collectors.groupingBy(stat -> {
                    if ("week".equals(groupBy)) {
                        // 按周分组：YYYY-WW
                        return stat.getStatDate().getYear() + "-W" + 
                               String.format("%02d", stat.getStatDate().get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear()));
                    } else {
                        // 按月分组：YYYY-MM
                        return stat.getStatDate().getYear() + "-" + 
                               String.format("%02d", stat.getStatDate().getMonthValue());
                    }
                }));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    List<StatsDailyDO> periodStats = entry.getValue();
                    int totalRequests = periodStats.stream().mapToInt(StatsDailyDO::getTotalRequests).sum();
                    int successCount = periodStats.stream().mapToInt(StatsDailyDO::getSuccessCount).sum();
                    int errorCount = periodStats.stream().mapToInt(StatsDailyDO::getErrorCount).sum();
                    int avgDurationMs = periodStats.isEmpty() ? 0 :
                            (int) periodStats.stream().mapToInt(StatsDailyDO::getAvgDurationMs).average().orElse(0);

                    return StatsTimeSeriesItemDTO.builder()
                            .date(entry.getKey())
                            .totalRequests(totalRequests)
                            .successCount(successCount)
                            .errorCount(errorCount)
                            .avgDurationMs(avgDurationMs)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public AgentCountRespVO getAgentCount() {
        // 查询已激活的 Agent 总数
        Long count = agentMapper.selectCount(
            new LambdaQueryWrapper<AgentDO>()
                .eq(AgentDO::getIsActive, true)
        );
        
        return AgentCountRespVO.builder()
            .totalAgents(count != null ? count : 0L)
            .build();
    }

}
