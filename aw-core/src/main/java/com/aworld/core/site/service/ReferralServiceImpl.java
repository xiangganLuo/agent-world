package com.aworld.core.site.service;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.dal.dataobject.SiteReferralEventDO;
import com.aworld.core.site.dal.mysql.SiteReferralEventMapper;
import com.aworld.core.site.dal.redis.ReferralRedisDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 场所引流服务实现
 *
 * @author aw
 */
@Service
@Slf4j
public class ReferralServiceImpl implements ReferralService {

    @Resource
    private SiteReferralEventMapper referralEventMapper;

    @Resource
    private ReferralRedisDAO referralRedisDAO;

    @Resource
    private SiteService siteService;

    @Override
    public String recordReferral(Long agentId, Long siteId, String targetUrl) {
        // 1. Redis 去重检查（只对已登录 Agent）
        if (agentId != null) {
            if (referralRedisDAO.isReferralExists(agentId, siteId)) {
                log.debug("[recordReferral] 引流事件已存在，跳过记录. agentId={}, siteId={}", agentId, siteId);
                return getTargetUrl(siteId, targetUrl);
            }

            // 设置去重标记
            referralRedisDAO.markReferralRecorded(agentId, siteId);
        }

        // 2. 写入引流事件表
        try {
            SiteReferralEventDO eventDO = SiteReferralEventDO.builder()
                    .agentId(agentId)
                    .siteId(siteId)
                    .eventTime(LocalDateTime.now())
                    .build();
            referralEventMapper.insert(eventDO);
        } catch (Exception e) {
            log.error("[recordReferral] 写入引流事件失败. agentId={}, siteId={}", agentId, siteId, e);
            // 不阻断重定向流程
        }

        // 3. 返回目标 URL（由 Controller 执行 302 重定向）
        return getTargetUrl(siteId, targetUrl);
    }

    /**
     * 获取目标跳转 URL
     * 优先使用传入的 targetUrl，如果为空则从场所配置中获取 apiBaseUrl
     */
    private String getTargetUrl(Long siteId, String targetUrl) {
        // 如果传入了 targetUrl，直接使用
        if (StrUtil.isNotBlank(targetUrl)) {
            return targetUrl;
        }

        // 否则从场所配置中获取
        try {
            SiteDO site = siteService.getSite(siteId);
            if (site != null && StrUtil.isNotBlank(site.getApiBaseUrl())) {
                return site.getApiBaseUrl();
            }
        } catch (Exception e) {
            log.error("[getTargetUrl] 查询场所信息失败. siteId={}", siteId, e);
        }

        // 兜底：返回空字符串，由 Controller 处理错误
        log.warn("[getTargetUrl] 无法获取跳转 URL. siteId={}", siteId);
        return "";
    }

}
