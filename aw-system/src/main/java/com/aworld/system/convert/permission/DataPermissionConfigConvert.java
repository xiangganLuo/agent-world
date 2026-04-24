package com.aworld.system.convert.permission;

import com.aworld.framework.common.pojo.PageResult;
import com.aworld.system.controller.admin.permission.vo.DataPermissionConfigRespVO;
import com.aworld.system.controller.admin.permission.vo.DataPermissionConfigSaveReqVO;
import com.aworld.system.dal.dataobject.permission.DataPermissionConfigDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DataPermissionConfigConvert {

    DataPermissionConfigConvert INSTANCE = Mappers.getMapper(DataPermissionConfigConvert.class);

    DataPermissionConfigDO convert(DataPermissionConfigSaveReqVO bean);

    DataPermissionConfigRespVO convert(DataPermissionConfigDO bean);

    List<DataPermissionConfigRespVO> convertList(List<DataPermissionConfigDO> list);

    PageResult<DataPermissionConfigRespVO> convertPage(PageResult<DataPermissionConfigDO> page);

}

