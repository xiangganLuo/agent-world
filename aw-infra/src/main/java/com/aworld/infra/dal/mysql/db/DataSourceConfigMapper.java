package com.aworld.infra.dal.mysql.db;

import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import com.aworld.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
