package com.aworld.core.stats.job;

import com.aworld.core.stats.dal.mysql.RequestLogMapper;
import com.aworld.framework.quartz.core.handler.JobHandler;
import com.aworld.framework.tenant.core.aop.TenantIgnore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 请求日志清理 Job - 每天执行
 * <p>
 * 定期清理超过 30 天的原始请求日志，避免数据膨胀
 *
 * @author aw
 */
@Component
@Slf4j
public class RequestLogCleanJob implements JobHandler {

    @Resource
    private RequestLogMapper requestLogMapper;

    /**
     * 保留天数：30 天
     */
    private static final int RETAIN_DAYS = 30;

    /**
     * 每次删除的批次大小
     */
    private static final int BATCH_SIZE = 1000;

    @Override
    @TenantIgnore
    public String execute(String param) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(RETAIN_DAYS);
        
        log.info("[execute] 开始清理请求日志. 截止时间: {}", cutoffTime);

        try {
            int totalDeleted = 0;
            
            while (true) {
                // 分批删除
                int deleted = requestLogMapper.deleteBatchIds(
                        requestLogMapper.selectList(
                                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.aworld.core.stats.dal.dataobject.RequestLogDO>()
                                        .lt(com.aworld.core.stats.dal.dataobject.RequestLogDO::getCreateTime, cutoffTime)
                                        .last("LIMIT " + BATCH_SIZE)
                        ).stream()
                                .map(com.aworld.core.stats.dal.dataobject.RequestLogDO::getId)
                                .collect(java.util.stream.Collectors.toList())
                );

                if (deleted == 0) {
                    break;
                }

                totalDeleted += deleted;
                log.info("[execute] 已删除 {} 条日志", totalDeleted);
            }

            log.info("[execute] 请求日志清理完成. 共删除 {} 条", totalDeleted);
            return String.format("请求日志清理完成，共删除 %d 条", totalDeleted);

        } catch (Exception e) {
            log.error("[execute] 请求日志清理失败", e);
            return "请求日志清理失败: " + e.getMessage();
        }
    }

}
