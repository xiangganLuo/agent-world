package com.aworld.core.stats.dal.mysql;

import com.aworld.core.stats.dal.dataobject.RequestLogDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * API 请求日志 Mapper
 *
 * @author aw
 */
@Mapper
public interface RequestLogMapper extends BaseMapperX<RequestLogDO> {

}
