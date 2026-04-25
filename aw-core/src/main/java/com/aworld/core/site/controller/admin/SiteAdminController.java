package com.aworld.core.site.controller.admin;

import com.aworld.core.site.controller.admin.vo.SiteAdminRespVO;
import com.aworld.core.site.controller.admin.vo.SiteCreateReqVO;
import com.aworld.core.site.controller.admin.vo.SiteReviewReqVO;
import com.aworld.core.site.controller.admin.vo.SiteUpdateReqVO;
import com.aworld.core.site.convert.SiteConvert;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.service.SiteService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 场所接口
 *
 * @author aw
 */
@Tag(name = "管理后台 - 场所")
@RestController
@RequestMapping("/core/site")
@Validated
public class SiteAdminController {

    @Resource
    private SiteService siteService;

    @GetMapping("/page")
    @Operation(summary = "场所分页列表")
    public CommonResult<PageResult<SiteAdminRespVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "状态过滤: pending/online/offline/rejected") @RequestParam(required = false) String state) {
        IPage<SiteDO> result = siteService.listSitesByState(new Page<>(page, limit), state);
        return success(new PageResult<>(SiteConvert.INSTANCE.convertAdminList(result.getRecords()), result.getTotal()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "场所详情")
    public CommonResult<SiteAdminRespVO> get(@PathVariable("id") Long id) {
        return success(SiteConvert.INSTANCE.convertAdmin(siteService.getSite(id)));
    }

    @PostMapping("/create")
    @Operation(summary = "创建场所")
    public CommonResult<Long> create(@Valid @RequestBody SiteCreateReqVO reqVO) {
        return success(siteService.createSite(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新场所")
    public CommonResult<Boolean> update(@Valid @RequestBody SiteUpdateReqVO reqVO) {
        siteService.updateSite(reqVO);
        return success(true);
    }

    @PutMapping("/review")
    @Operation(summary = "审核场所（approve/reject）")
    public CommonResult<Boolean> review(@Valid @RequestBody SiteReviewReqVO reqVO) {
        siteService.reviewSite(reqVO);
        return success(true);
    }

    @PutMapping("/{id}/offline")
    @Operation(summary = "下线场所")
    public CommonResult<Boolean> offline(@PathVariable("id") Long id) {
        siteService.offlineSite(id);
        return success(true);
    }

}
