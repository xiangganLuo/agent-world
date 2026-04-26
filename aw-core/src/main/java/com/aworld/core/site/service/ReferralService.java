package com.aworld.core.site.service;

/**
 * 场所引流服务
 *
 * @author aw
 */
public interface ReferralService {

    /**
     * 记录引流事件并返回重定向 URL
     * <p>
     * 1. Redis 5分钟去重检查
     * 2. 写入引流事件表
     * 3. 返回 302 重定向到目标场所
     *
     * @param agentId Agent ID（可为 null，表示匿名访问）
     * @param siteId  目标场所 ID
     * @param targetUrl 目标场所 URL
     * @return 重定向 URL
     */
    String recordReferral(Long agentId, Long siteId, String targetUrl);

}
