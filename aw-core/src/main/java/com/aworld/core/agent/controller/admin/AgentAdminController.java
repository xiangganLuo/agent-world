package com.aworld.core.agent.controller.admin;

import com.aworld.core.agent.controller.admin.vo.AgentAdminRespVO;
import com.aworld.core.agent.convert.AgentConvert;
import com.aworld.core.agent.service.AgentService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.common.pojo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - Agent 接口
 *
 * @author aw
 */
@Tag(name = "管理后台 - Agent")
@RestController
@RequestMapping("/core/agent")
@Validated
public class AgentAdminController {

    @Resource
    private AgentService agentService;

    @GetMapping("/page")
    @Operation(summary = "Agent 分页列表")
    public CommonResult<PageResult<AgentAdminRespVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "用户名（模糊搜索）") @RequestParam(required = false) String username,
            @Parameter(description = "激活状态筛选") @RequestParam(required = false) Boolean isActive) {
        return success(agentService.getAgentPage(page, limit, username, isActive));
    }

    @GetMapping("/get")
    @Operation(summary = "Agent 详情")
    public CommonResult<AgentAdminRespVO> get(@Parameter(description = "Agent ID") @RequestParam Long id) {
        return success(AgentConvert.INSTANCE.convertAdmin(agentService.getAgent(id)));
    }

    @PutMapping("/{id}/ban")
    @Operation(summary = "封禁 Agent")
    public CommonResult<Boolean> ban(@PathVariable("id") Long id) {
        agentService.banAgent(id);
        return success(true);
    }

    @PutMapping("/{id}/unban")
    @Operation(summary = "解封 Agent")
    public CommonResult<Boolean> unban(@PathVariable("id") Long id) {
        agentService.unbanAgent(id);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 Agent")
    public CommonResult<Boolean> delete(@Parameter(description = "Agent ID") @RequestParam Long id) {
        agentService.deleteAgent(id);
        return success(true);
    }

}
