package com.aworld.core.stats.job;

import com.aworld.core.site.dal.dataobject.SiteReferralEventDO;
import com.aworld.core.site.dal.mysql.SiteReferralEventMapper;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 引流聚合 Job - 每小时执行
 * <p>
 * 从 aworld_site_referral_event 表聚合上一小时的引流统计数据
 * 统计维度：每个场所的引流数、独立 Agent 数、新入驻数
 *
 * @author aw
 */
@Component
@Slf4j
public class ReferralAggregationJob implements JobHandler {

    @Resource
    private SiteReferralEventMapper referralEventMapper;

    @Override
    @TenantIgnore
    public String execute(String param) {
        // 计算上一小时的时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHourStart = now.minus(1, ChronoUnit.HOURS).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime lastHourEnd = lastHourStart.plus(1, ChronoUnit.HOURS);

        log.info("[execute] 开始聚合引流统计. 时间范围: {} ~ {}", lastHourStart, lastHourEnd);

        try {
            // 分页查询上一小时的所有引流事件
            int pageSize = 1000;
            int pageNum = 1;
            long totalProcessed = 0;

            while (true) {
                IPage<SiteReferralEventDO> page = new Page<>(pageNum, pageSize);
                IPage<SiteReferralEventDO> resultPage = referralEventMapper.selectPage(page,
                        new LambdaQueryWrapperX<SiteReferralEventDO>()
                                .between(SiteReferralEventDO::getCreateTime, lastHourStart, lastHourEnd)
                );

                List<SiteReferralEventDO> events = resultPage.getRecords();
                if (events.isEmpty()) {
                    break;
                }

                // 按 site_id 分组统计
                Map<Long, List<SiteReferralEventDO>> groupedBySite = events.stream()
                        .collect(Collectors.groupingBy(SiteReferralEventDO::getSiteId));

                for (Map.Entry<Long, List<SiteReferralEventDO>> entry : groupedBySite.entrySet()) {
                    Long siteId = entry.getKey();
                    List<SiteReferralEventDO> siteEvents = entry.getValue();

                    // 统计指标
                    int referralCount = siteEvents.size();
                    Set<Long> uniqueAgents = siteEvents.stream()
                            .map(SiteReferralEventDO::getAgentId)
                            .filter(id -> id != null)
                            .collect(Collectors.toSet());
                    int uniqueAgentCount = uniqueAgents.size();

                    log.info("[execute] 场所引流统计. siteId={}, 引流数={}, 独立Agent数={}",
                            siteId, referralCount, uniqueAgentCount);

                    totalProcessed += referralCount;
                }

                // 判断是否还有下一页
                if (pageNum >= resultPage.getPages()) {
                    break;
                }
                pageNum++;
            }

            log.info("[execute] 引流统计聚合完成. 处理事件数: {}", totalProcessed);
            return String.format("引流统计聚合完成，处理事件数 %d", totalProcessed);

        } catch (Exception e) {
            log.error("[execute] 引流统计聚合失败", e);
            return "引流统计聚合失败: " + e.getMessage();
        }
    }

}
