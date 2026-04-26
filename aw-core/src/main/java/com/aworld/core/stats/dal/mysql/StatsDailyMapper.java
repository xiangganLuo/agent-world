package com.aworld.core.stats.dal.mysql;

import com.aworld.core.stats.dal.dataobject.StatsDailyDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 天聚合统计 Mapper
 *
 * @author aw
 */
@Mapper
public interface StatsDailyMapper extends BaseMapperX<StatsDailyDO> {

}
