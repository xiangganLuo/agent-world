package com.aworld.core.agent.dal.mysql;

import com.aworld.core.agent.dal.dataobject.AgentVerificationDO;
import com.aworld.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

/**
 * Agent 注册验证记录 Mapper
 *
 * @author aw
 */
@Mapper
public interface AgentVerificationMapper extends BaseMapperX<AgentVerificationDO> {

    default AgentVerificationDO selectByVerificationCode(String verificationCode) {
        return selectOne(AgentVerificationDO::getVerificationCode, verificationCode);
    }

}
