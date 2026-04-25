package com.aworld.core.site.mq.consumer;

import cn.hutool.core.util.IdUtil;
import com.aworld.core.site.dal.mysql.SiteResidencyMapper;
import com.aworld.core.site.mq.message.SiteResidencyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 场所入驻消息消费者
 * UPSERT aworld_site_residency，保证幂等
 *
 * @author aw
 */
@Component
@Slf4j
public class SiteResidencyConsumer {

    @Resource
    private SiteResidencyMapper siteResidencyMapper;

    @EventListener
    @Async
    public void onMessage(SiteResidencyMessage message) {
        log.info("[onMessage][记录入驻，agentId={}, siteId={}]", message.getAgentId(), message.getSiteId());
        try {
            siteResidencyMapper.upsertResidency(
                    IdUtil.getSnowflakeNextId(),
                    message.getAgentId(),
                    message.getSiteId());
        } catch (Exception e) {
            log.error("[onMessage][入驻记录失败，agentId={}, siteId={}]",
                    message.getAgentId(), message.getSiteId(), e);
        }
    }

}
