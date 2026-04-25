package com.aworld.framework.common.biz.agent.dto;

import lombok.Data;

/**
 * Agent 认证 Check 结果 DTO
 */
@Data
public class AgentAuthCheckRespDTO {

    /**
     * Agent 编号
     */
    private Long agentId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 租户编号
     */
    private Long tenantId;

}
