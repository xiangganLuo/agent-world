package com.aworld.core.stats.dal.mysql;

import com.aworld.core.stats.dal.dataobject.StatsHourlyDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 小时聚合统计 Mapper
 *
 * @author aw
 */
@Mapper
public interface StatsHourlyMapper extends BaseMapperX<StatsHourlyDO> {

}
