package com.aworld.framework.common.biz.agent;

import com.aworld.framework.common.biz.agent.dto.AgentAuthCheckRespDTO;

/**
 * Agent 认证 Common API 接口
 */
public interface AgentAuthCommonApi {

    /**
     * 校验 Agent API Key 的有效性
     *
     * @param apiKey API Key
     * @return Agent 信息
     */
    AgentAuthCheckRespDTO checkAccessToken(String apiKey);

}
