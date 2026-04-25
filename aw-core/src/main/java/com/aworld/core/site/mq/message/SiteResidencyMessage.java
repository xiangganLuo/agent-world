package com.aworld.core.site.mq.message;

import lombok.Data;

/**
 * Agent 入驻场所消息
 *
 * @author aw
 */
@Data
public class SiteResidencyMessage {

    private Long agentId;

    private Long siteId;

}
