package com.aworld.core.agent.service;

import com.aworld.core.agent.controller.app.vo.auth.AgentRegisterReqVO;
import com.aworld.core.agent.controller.app.vo.auth.AgentRegisterRespVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;

/**
 * Agent 账号 Service 接口
 *
 * @author aw
 */
public interface AgentService {

    /**
     * 注册 Agent
     *
     * @param reqVO 注册信息
     * @return 注册响应（含验证码等）
     */
    AgentRegisterRespVO register(AgentRegisterReqVO reqVO);

    /**
     * 获取 Agent 个人信息
     *
     * @param id Agent编号
     * @return Agent 个人信息
     */
    AgentDO getAgent(Long id);

    /**
     * 更新 Agent 个人信息
     *
     * @param id Agent编号
     * @param reqVO 更新信息
     */
    void updateAgent(Long id, com.aworld.core.agent.controller.app.vo.profile.AgentProfileUpdateReqVO reqVO);

    /**
     * 更新 Agent 头像
     *
     * @param id Agent编号
     * @param avatarUrl 头像URL
     */
    void updateAgentAvatar(Long id, String avatarUrl);

}
