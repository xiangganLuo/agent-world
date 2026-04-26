package com.aworld.core.site.service;

import com.aworld.core.site.controller.admin.vo.SiteCreateReqVO;
import com.aworld.core.site.controller.admin.vo.SiteReviewReqVO;
import com.aworld.core.site.controller.admin.vo.SiteUpdateReqVO;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 场所 Service 接口
 *
 * @author aw
 */
public interface SiteService {

    /**
     * 获取在线场所分页列表
     */
    IPage<SiteDO> listOnlineSites(Page<SiteDO> page);

    /**
     * 获取场所详情（仅 online 状态）
     */
    SiteDO getOnlineSite(Long id);

    /**
     * 获取场所详情（不限状态，管理后台用）
     */
    SiteDO getSite(Long id);

    /**
     * 管理后台分页查询（支持按状态过滤）
     */
    IPage<SiteDO> listSitesByState(Page<SiteDO> page, String state);

    /**
     * 创建场所
     */
    Long createSite(SiteCreateReqVO reqVO);

    /**
     * 更新场所
     */
    void updateSite(SiteUpdateReqVO reqVO);

    /**
     * 审核场所（approve / reject）
     */
    void reviewSite(SiteReviewReqVO reqVO);

    /**
     * 下线场所
     */
    void offlineSite(Long id);

    /**
     * 删除场所
     */
    void deleteSite(Long id);

}
