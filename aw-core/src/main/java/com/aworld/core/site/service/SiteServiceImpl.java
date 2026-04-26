package com.aworld.core.site.service;

import com.aworld.core.site.controller.admin.vo.SiteCreateReqVO;
import com.aworld.core.site.controller.admin.vo.SiteReviewReqVO;
import com.aworld.core.site.controller.admin.vo.SiteUpdateReqVO;
import com.aworld.core.site.convert.SiteConvert;
import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.core.site.dal.mysql.SiteMapper;
import com.aworld.core.site.enums.SiteErrorCodeConstants;
import com.aworld.core.site.enums.SiteStateConstants;
import com.aworld.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 场所 Service 实现类
 *
 * @author aw
 */
@Service
public class SiteServiceImpl implements SiteService {

    @Resource
    private SiteMapper siteMapper;

    @Override
    public IPage<SiteDO> listOnlineSites(Page<SiteDO> page) {
        return siteMapper.selectOnlinePage(page);
    }

    @Override
    public SiteDO getOnlineSite(Long id) {
        SiteDO site = siteMapper.selectById(id);
        if (site == null || !SiteStateConstants.ONLINE.equals(site.getState())) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        return site;
    }

    @Override
    public SiteDO getSite(Long id) {
        SiteDO site = siteMapper.selectById(id);
        if (site == null) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        return site;
    }

    @Override
    public IPage<SiteDO> listSitesByState(Page<SiteDO> page, String state) {
        return siteMapper.selectPageByState(page, state);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSite(SiteCreateReqVO reqVO) {
        SiteDO site = SiteConvert.INSTANCE.convert(reqVO);
        site.setState(SiteStateConstants.PENDING);
        if (site.getSort() == null) {
            site.setSort(0);
        }
        siteMapper.insert(site);
        return site.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSite(SiteUpdateReqVO reqVO) {
        SiteDO existing = siteMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        SiteDO update = new SiteDO();
        update.setId(reqVO.getId());
        if (reqVO.getName() != null) update.setName(reqVO.getName());
        if (reqVO.getDescription() != null) update.setDescription(reqVO.getDescription());
        if (reqVO.getIconUrl() != null) update.setIconUrl(reqVO.getIconUrl());
        if (reqVO.getSkillDocUrl() != null) update.setSkillDocUrl(reqVO.getSkillDocUrl());
        if (reqVO.getApiBaseUrl() != null) update.setApiBaseUrl(reqVO.getApiBaseUrl());
        if (reqVO.getSort() != null) update.setSort(reqVO.getSort());
        siteMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewSite(SiteReviewReqVO reqVO) {
        SiteDO site = siteMapper.selectById(reqVO.getId());
        if (site == null) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        if (!SiteStateConstants.PENDING.equals(site.getState())) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_STATE_NOT_PENDING);
        }
        String action = reqVO.getAction();
        if (!"approve".equals(action) && !"reject".equals(action)) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_REVIEW_ACTION_INVALID);
        }
        SiteDO update = new SiteDO();
        update.setId(site.getId());
        update.setState("approve".equals(action) ? SiteStateConstants.ONLINE : SiteStateConstants.REJECTED);
        update.setReviewReason(reqVO.getReason());
        siteMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void offlineSite(Long id) {
        SiteDO site = siteMapper.selectById(id);
        if (site == null) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        if (!SiteStateConstants.ONLINE.equals(site.getState())) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_STATE_NOT_ONLINE);
        }
        SiteDO update = new SiteDO();
        update.setId(id);
        update.setState(SiteStateConstants.OFFLINE);
        siteMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSite(Long id) {
        SiteDO site = siteMapper.selectById(id);
        if (site == null) {
            throw ServiceExceptionUtil.exception(SiteErrorCodeConstants.SITE_NOT_EXISTS);
        }
        siteMapper.deleteById(id);
    }

}
