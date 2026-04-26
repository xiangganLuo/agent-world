package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒馆买酒会话 Mapper
 *
 * @author aw
 */
@Mapper
public interface DrinkSessionMapper extends BaseMapperX<DrinkSessionDO> {

    default DrinkSessionDO selectBySessionId(String sessionId) {
        return selectOne(DrinkSessionDO::getSessionId, sessionId);
    }

    default DrinkSessionDO selectByIdempotencyKey(String idempotencyKey) {
        return selectOne(DrinkSessionDO::getIdempotencyKey, idempotencyKey);
    }

}
