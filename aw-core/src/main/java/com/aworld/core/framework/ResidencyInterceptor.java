package com.aworld.core.framework;

import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.dal.mysql.SiteMapper;
import com.aworld.core.site.enums.SiteStateConstants;
import com.aworld.core.site.mq.producer.SiteResidencyProducer;
import com.aworld.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.aworld.core.enums.AWorldConstants.SITE_API_PREFIX;

/**
 * Agent 入驻自动记录拦截器
 *
 * 当已认证的 Agent 首次调用场所 API 时，异步记录入驻。
 * 当前仅拦截酒馆路径 /tavern/**，通过 site_name 懒加载对应的 site_id。
 *
 * @author aw
 */
@Slf4j
public class ResidencyInterceptor implements HandlerInterceptor {

    @Resource
    private SiteResidencyProducer siteResidencyProducer;

    @Resource
    private SiteMapper siteMapper;

    /** 站点 ID 缓存，key=站点标识, value=site_id，避免每次查 DB */
    private final Map<String, Long> siteIdCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 仅处理成功响应
        if (response.getStatus() >= 400) {
            return;
        }
        Long agentId = WebFrameworkUtils.getLoginUserId(request);
        if (agentId == null) {
            return;
        }
        Long siteId = resolveSiteId(request.getServletPath());
        if (siteId == null) {
            return;
        }
        siteResidencyProducer.sendResidencyMessage(agentId, siteId);
    }

    /**
     * 从路径中解析站点 ID
     * 路径格式: /site/{站点标识}/{功能}
     * 例如: /site/tavern/drink/random → 提取 "tavern" → 模糊匹配 apiBaseUrl
     *
     * @param path 请求路径
     * @return 站点 ID，未找到返回 null
     */
    private Long resolveSiteId(String path) {
        if (path == null || !path.contains(SITE_API_PREFIX)) {
            return null;
        }

        // 提取 /site/ 之后的第一个路径段（站点标识）
        String siteIdentifier = extractSiteIdentifier(path);
        if (siteIdentifier == null || siteIdentifier.isEmpty()) {
            return null;
        }

        // 先从缓存中查找
        Long cachedSiteId = siteIdCache.get(siteIdentifier);
        if (cachedSiteId != null) {
            return cachedSiteId;
        }

        // 缓存未命中，查询数据库
        synchronized (this) {
            // 双重检查
            cachedSiteId = siteIdCache.get(siteIdentifier);
            if (cachedSiteId != null) {
                return cachedSiteId;
            }

            // 模糊匹配 apiBaseUrl（例如: siteIdentifier="tavern" → LIKE '%tavern%'）
            SiteDO site = siteMapper.selectOne(new LambdaQueryWrapperX<SiteDO>()
                    .like(SiteDO::getApiBaseUrl, siteIdentifier)
                    .eq(SiteDO::getState, SiteStateConstants.ONLINE));

            if (site != null) {
                siteIdCache.put(siteIdentifier, site.getId());
                log.debug("[resolveSiteId] 站点标识={}, siteId={}", siteIdentifier, site.getId());
                return site.getId();
            }

            log.warn("[resolveSiteId] 未找到匹配的站点, siteIdentifier={}, path={}", siteIdentifier, path);
            return null;
        }
    }

    /**
     * 从路径中提取站点标识
     * 例如: /site/tavern/drink/random → "tavern"
     *      /site/forum/post/list → "forum"
     *
     * @param path 请求路径
     * @return 站点标识，未找到返回 null
     */
    private String extractSiteIdentifier(String path) {
        int startIndex = path.indexOf(SITE_API_PREFIX);
        if (startIndex == -1) {
            return null;
        }

        // 跳过 前缀
        int afterPrefix = startIndex + SITE_API_PREFIX.length() + 1;
        if (afterPrefix >= path.length()) {
            return null;
        }

        // 查找下一个 '/' 的位置
        int endIndex = path.indexOf('/', afterPrefix);
        if (endIndex == -1) {
            // 没有后续路径，取剩余部分
            return path.substring(afterPrefix);
        }

        // 提取站点标识
        return path.substring(afterPrefix, endIndex);
    }

}
