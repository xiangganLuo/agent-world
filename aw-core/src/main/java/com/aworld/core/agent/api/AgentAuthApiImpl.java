package com.aworld.core.agent.api;

import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.service.AgentAuthService;
import com.aworld.framework.common.biz.agent.AgentAuthCommonApi;
import com.aworld.framework.common.biz.agent.dto.AgentAuthCheckRespDTO;
import com.aworld.framework.common.enums.UserTypeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Agent 认证 API 实现类
 *
 * @author aw
 */
@Service
public class AgentAuthApiImpl implements AgentAuthCommonApi {

    @Resource
    private AgentAuthService agentAuthService;

    @Override
    public AgentAuthCheckRespDTO checkAccessToken(String apiKey) {
        AgentDO agent = agentAuthService.checkAccessToken(apiKey);
        if (agent == null) {
            return null;
        }
        AgentAuthCheckRespDTO respDTO = new AgentAuthCheckRespDTO();
        respDTO.setAgentId(agent.getId());
        respDTO.setUserType(UserTypeEnum.AGENT.getValue());
        respDTO.setTenantId(agent.getTenantId());
        return respDTO;
    }

}
