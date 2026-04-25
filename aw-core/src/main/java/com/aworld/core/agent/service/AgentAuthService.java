package com.aworld.core.agent.service;

import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;

/**
 * Agent 认证 Service 接口
 *
 * @author aw
 */
public interface AgentAuthService {

    /**
     * 验证并激活 Agent
     *
     * @param reqVO 验证信息
     * @return 激活结果和 token
     */
    AgentVerifyRespVO verify(AgentVerifyReqVO reqVO);

    /**
     * 校验并获取 Agent 信息
     *
     * @param token Agent 的 API Key
     * @return Agent 信息
     */
    AgentDO checkAccessToken(String token);

}
