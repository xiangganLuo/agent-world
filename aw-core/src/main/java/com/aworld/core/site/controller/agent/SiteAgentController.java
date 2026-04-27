package com.aworld.core.site.controller.agent;

import com.aworld.core.site.convert.SiteConvert;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.mq.producer.SiteResidencyProducer;
import com.aworld.core.site.service.SiteService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.common.pojo.PageResult;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent App - 场所接口
 *
 * @author aw
 */
@Tag(name = "Agent Agent - 场所")
@RestController
@RequestMapping("/sites")
@Validated
public class SiteAgentController {

    @Resource
    private SiteService siteService;

    @Resource
    private SiteResidencyProducer siteResidencyProducer;

    @GetMapping("/{siteId}")
    @Operation(summary = "场所详情")
    @PermitAll
    public CommonResult<?> getSite(@PathVariable("siteId") Long siteId) {
        return success(SiteConvert.INSTANCE.convert(siteService.getOnlineSite(siteId)));
    }

    @GetMapping("/{siteId}/redirect")
    @Operation(summary = "场所引流跳转")
    @PermitAll
    public void redirect(@PathVariable Long siteId,
                         HttpServletResponse response) throws IOException {
        SiteDO site = siteService.getOnlineSite(siteId);
        // 已登录的 Agent 异步记录入驻
        Long agentId = WebFrameworkUtils.getLoginUserId();
        if (agentId != null) {
            siteResidencyProducer.sendResidencyMessage(agentId, siteId);
        }
        response.sendRedirect(site.getApiBaseUrl());
    }

    @PermitAll
    @GetMapping
    @Operation(summary = "场所列表（在线）")
    public CommonResult<PageResult<?>> listSites(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer limit) {
        IPage<SiteDO> result = siteService.listOnlineSites(new Page<>(page, limit));
        return success(new PageResult<>(SiteConvert.INSTANCE.convertList(result.getRecords()), result.getTotal()));
    }

}
