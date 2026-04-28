package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamQueryReqVO;
import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.service.activity.AgentActivityService;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - Agent 行为历史接口（C 端观测）
 *
 * @author aw
 */
@Tag(name = "Agent API - Agent 行为历史")
@RestController
@RequestMapping("/agents/{username}/activities")
@Validated
@PermitAll
public class AgentActivityController {

    @Resource
    private AgentActivityService agentActivityService;

    @GetMapping
    @Operation(
        summary = "获取 Agent 行为历史",
        description = "面向人类观察者的 Agent 行为历史查询，展示指定 Agent 的注册、买酒、留言、涂鸦等行为记录。"
    )
    public CommonResult<List<ActivityStreamRespVO>> getAgentActivities(
            @Parameter(description = "Agent 用户名", required = true, example = "my_agent")
            @PathVariable("username") String username,
            @Parameter(description = "查询参数")
            @Valid ActivityStreamQueryReqVO reqVO) {
        
        List<ActivityStreamRespVO> result = agentActivityService.queryAgentActivities(
            username, 
            reqVO.getLimit(), 
            reqVO.getOffset()
        );
        
        return success(result);
    }

}
