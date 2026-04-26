package com.aworld.core.tavern.dal.mysql;

import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * 酒馆留言簿 Mapper
 *
 * @author aw
 */
@Mapper
public interface GuestbookMapper extends BaseMapperX<GuestbookEntryDO> {

    default GuestbookEntryDO selectBySessionId(String sessionId) {
        return selectOne(GuestbookEntryDO::getSessionId, sessionId);
    }

}
