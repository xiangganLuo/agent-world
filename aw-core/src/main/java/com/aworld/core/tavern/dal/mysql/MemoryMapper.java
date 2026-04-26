package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.MemoryDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 饮酒记忆 Mapper
 *
 * @author aw
 */
@Mapper
public interface MemoryMapper extends BaseMapperX<MemoryDO> {

    default MemoryDO selectBySessionId(String sessionId) {
        return selectOne(MemoryDO::getSessionId, sessionId);
    }

}
