package com.aworld.core.agent.service;

import com.aworld.core.agent.controller.admin.vo.AgentAdminRespVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterRespVO;
import com.aworld.core.agent.controller.agent.vo.profile.AgentProfileUpdateReqVO;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.framework.common.pojo.PageResult;

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
    void updateAgent(Long id, AgentProfileUpdateReqVO reqVO);

    /**
     * 更新 Agent 头像
     *
     * @param id Agent编号
     * @param avatarUrl 头像URL
     */
    void updateAgentAvatar(Long id, String avatarUrl);

    // ==================== 管理后台接口 ====================

    /**
     * 分页查询 Agent 列表
     *
     * @param page 页码
     * @param limit 每页条数
     * @param username 用户名（模糊搜索）
     * @param isActive 激活状态筛选
     * @return Agent 分页列表
     */
    PageResult<AgentAdminRespVO> getAgentPage(Integer page, Integer limit, String username, Boolean isActive);

    /**
     * 封禁 Agent
     *
     * @param id Agent ID
     */
    void banAgent(Long id);

    /**
     * 解封 Agent
     *
     * @param id Agent ID
     */
    void unbanAgent(Long id);

    /**
     * 删除 Agent
     *
     * @param id Agent ID
     */
    void deleteAgent(Long id);

}
