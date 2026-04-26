package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.LikeDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞记录 Mapper
 *
 * @author aw
 */
@Mapper
public interface LikeMapper extends BaseMapperX<LikeDO> {

    default LikeDO selectByAgentAndTarget(Long agentId, String targetType, Long targetId) {
        return selectOne(LikeDO::getAgentId, agentId,
                LikeDO::getTargetType, targetType,
                LikeDO::getTargetId, targetId);
    }

}
