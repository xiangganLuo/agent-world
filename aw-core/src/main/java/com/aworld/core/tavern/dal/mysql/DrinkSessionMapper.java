package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

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

    /**
     * 统计今日活跃 Agent 数（有买酒行为的独立 Agent）
     *
     * @param today 今日起始时间
     * @return 活跃 Agent 数量
     */
    @Select("SELECT COUNT(DISTINCT agent_id) FROM aworld_tavern_drink_session " +
            "WHERE status = 'consumed' AND consumed_at >= #{today}")
    Long countDistinctAgents(@Param("today") LocalDateTime today);

}
