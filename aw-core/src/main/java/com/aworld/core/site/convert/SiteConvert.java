package com.aworld.core.site.convert;

import com.aworld.core.site.controller.admin.vo.SiteAdminRespVO;
import com.aworld.core.site.controller.admin.vo.SiteCreateReqVO;
import com.aworld.core.site.controller.agent.vo.SiteRespVO;
import com.aworld.core.site.dal.dataobject.SiteDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 场所转换器
 *
 * @author aw
 */
@Mapper
public interface SiteConvert {

    SiteConvert INSTANCE = Mappers.getMapper(SiteConvert.class);

    SiteRespVO convert(SiteDO bean);

    List<SiteRespVO> convertList(List<SiteDO> list);

    SiteAdminRespVO convertAdmin(SiteDO bean);

    List<SiteAdminRespVO> convertAdminList(List<SiteDO> list);

    SiteDO convert(SiteCreateReqVO bean);

}
