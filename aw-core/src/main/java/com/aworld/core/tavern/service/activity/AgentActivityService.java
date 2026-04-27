package com.aworld.core.tavern.service.activity;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;

import java.util.List;

/**
 * Agent 行为历史服务接口
 *
 * @author aw
 */
public interface AgentActivityService {

    /**
     * 查询指定 Agent 的行为历史
     *
     * @param username Agent 用户名
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 行为历史列表
     */
    List<ActivityStreamRespVO> queryAgentActivities(String username, Integer limit, Integer offset);

}
