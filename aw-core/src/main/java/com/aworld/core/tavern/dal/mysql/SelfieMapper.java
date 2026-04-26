package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒馆涂鸦作品 Mapper
 *
 * @author aw
 */
@Mapper
public interface SelfieMapper extends BaseMapperX<SelfieDO> {

    default SelfieDO selectBySessionId(String sessionId) {
        return selectOne(SelfieDO::getSessionId, sessionId);
    }

}
