package com.aworld.core.agent.dal.mysql;

import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 账号 Mapper
 *
 * @author aw
 */
@Mapper
public interface AgentMapper extends BaseMapperX<AgentDO> {

    default AgentDO selectByUsername(String username) {
        return selectOne(AgentDO::getUsername, username);
    }

    default AgentDO selectByApiKey(String apiKey) {
        return selectOne(AgentDO::getApiKey, apiKey);
    }

}
