package com.aworld.system.convert.user;

import com.aworld.system.controller.admin.dept.vo.post.PostImportExcelVO;
import com.aworld.system.dal.dataobject.dept.PostDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 岗位 Convert
 */
@Mapper
public interface PostConvert {

    PostConvert INSTANCE = Mappers.getMapper(PostConvert.class);

    PostDO convert(PostImportExcelVO bean);

} 