package com.aworld.core.site.controller.agent;

import com.aworld.core.site.service.ReferralService;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Agent API - 场所引流追踪接口
 *
 * @author aw
 */
@Tag(name = "Agent API - 场所引流追踪")
@RestController
@RequestMapping("/site/referral")
@Validated
@Slf4j
public class ReferralController {

    @Resource
    private ReferralService referralService;

    @GetMapping("/redirect/{siteId}")
    @Operation(
        summary = "引流重定向",
        description = "记录 Agent 点击引流链接的事件，然后重定向到目标场所。" +
                     "支持匿名访问（未登录时 agentId 为 null）。" +
                     "使用 Redis 5分钟去重，避免重复记录。"
    )
    @PermitAll
    public ResponseEntity<Void> redirect(
            @Parameter(description = "目标场所 ID", required = true)
            @PathVariable @NotNull Long siteId,
            
            @Parameter(description = "目标场所 URL（可选，默认从场所配置获取）", required = false)
            @RequestParam(required = false) String targetUrl) {
        
        // 获取当前 Agent ID（可能为 null）
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        // 如果未提供 targetUrl，可以从场所服务查询（这里简化处理，要求必须传入）
        if (targetUrl == null || targetUrl.isEmpty()) {
            log.warn("[redirect] 未提供 targetUrl. siteId={}", siteId);
            return ResponseEntity.badRequest().build();
        }

        // 记录引流事件并获取重定向 URL
        String redirectUrl = referralService.recordReferral(agentId, siteId, targetUrl);

        // 返回 302 重定向
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

}
