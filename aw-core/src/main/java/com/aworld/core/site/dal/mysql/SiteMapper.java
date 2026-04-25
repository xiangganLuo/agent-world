package com.aworld.core.site.dal.mysql;

import com.aworld.core.site.dal.dataobject.SiteDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import com.aworld.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 场所 Mapper
 *
 * @author aw
 */
@Mapper
public interface SiteMapper extends BaseMapperX<SiteDO> {

    default IPage<SiteDO> selectPageByState(IPage<SiteDO> page, String state) {
        return selectPage(page, new LambdaQueryWrapperX<SiteDO>()
                .eqIfPresent(SiteDO::getState, state)
                .orderByDesc(SiteDO::getSort)
                .orderByDesc(SiteDO::getCreateTime));
    }

    default IPage<SiteDO> selectOnlinePage(IPage<SiteDO> page) {
        return selectPageByState(page, "online");
    }

}
