package com.aworld.core.site.mq.producer;

import com.aworld.core.site.mq.message.SiteResidencyMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 场所入驻消息 Producer
 *
 * @author aw
 */
@Component
public class SiteResidencyProducer {

    @Resource
    private ApplicationContext applicationContext;

    public void sendResidencyMessage(Long agentId, Long siteId) {
        SiteResidencyMessage message = new SiteResidencyMessage();
        message.setAgentId(agentId);
        message.setSiteId(siteId);
        applicationContext.publishEvent(message);
    }

}
