package com.aworld.core.framework.filter;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.stats.dal.dataobject.RequestLogDO;
import com.aworld.core.stats.service.RequestLogService;
import com.aworld.framework.web.config.WebProperties;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * API 请求日志过滤器
 * <p>
 * 记录所有 /agent-api/ 和 /admin-api/ 路径下的请求日志
 *
 * @author aw
 */
@Component
@Slf4j
public class RequestLogFilter extends OncePerRequestFilter {

    @Resource
    private RequestLogService requestLogService;

    @Resource
    private WebProperties webProperties;

    @Resource
    private AgentService agentService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 记录开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 计算耗时
            int durationMs = (int) (System.currentTimeMillis() - startTime);

            // 异步记录日志（只记录特定路径）
            String path = request.getRequestURI();
            if (shouldRecordLog(path)) {
                recordLog(request, response, durationMs);
            }
        }
    }

    /**
     * 判断是否应该记录日志
     */
    private boolean shouldRecordLog(String path) {
        return path.startsWith(webProperties.getAgentApi().getPrefix());
    }

    /**
     * 记录请求日志
     */
    private void recordLog(HttpServletRequest request, HttpServletResponse response, int durationMs) {
        try {
            Long agentId = WebFrameworkUtils.getLoginUserId();
            if (agentId == null) {
                return;
            }

            AgentDO agent = agentService.getAgent(agentId);
            if (agent == null) {
                return;
            }
            // 获取 API Key
            String apiKey = agent.getApiKey();

            // 构建日志对象
            RequestLogDO logDO = RequestLogDO.builder()
                    .apiKey(apiKey)
                    .agentId(agentId)
                    .path(request.getRequestURI())
                    .method(request.getMethod())
                    .statusCode(response.getStatus())
                    .durationMs(durationMs)
                    .clientIp(getClientIp(request))
                    .build();

            // 异步写入数据库
            requestLogService.recordLogAsync(logDO);
        } catch (Exception e) {
            log.error("[RequestLogFilter] 记录请求日志失败", e);
        }
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时，第一个 IP 为真实 IP
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}
