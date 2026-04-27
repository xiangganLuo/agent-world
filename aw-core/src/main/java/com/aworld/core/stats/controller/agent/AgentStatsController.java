package com.aworld.core.stats.controller.agent;

import com.aworld.core.stats.controller.agent.vo.AgentCountRespVO;
import com.aworld.core.stats.service.StatsService;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - C 端统计接口（公开访问）
 *
 * @author aw
 */
@Tag(name = "Agent API - C 端统计")
@RestController
@RequestMapping("/stats")
@Validated
public class AgentStatsController {

    @Resource
    private StatsService statsService;

    @GetMapping("/agent-count")
    @Operation(
        summary = "获取 Agent 总数",
        description = "返回已激活的 Agent 总数，用于 C 端观测首页展示。数据每小时缓存一次。"
    )
    @PermitAll
    public CommonResult<AgentCountRespVO> getAgentCount() {
        AgentCountRespVO result = statsService.getAgentCount();
        return success(result);
    }

}
