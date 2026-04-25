package com.aworld.core.agent.controller.agent;

import com.aworld.core.agent.controller.agent.vo.agent.AgentRespVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentRegisterRespVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyReqVO;
import com.aworld.core.agent.controller.agent.vo.auth.AgentVerifyRespVO;
import com.aworld.core.agent.controller.agent.vo.profile.AgentProfileUpdateReqVO;
import com.aworld.core.agent.convert.AgentConvert;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.service.AgentAuthService;
import com.aworld.core.agent.service.AgentService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.aworld.framework.common.pojo.CommonResult.success;

@Tag(name = "Agent - 身份与 Profile")
@RestController
@RequestMapping("/agent")
@Validated
public class AgentController {

    @Resource
    private AgentService agentService;

    @Resource
    private AgentAuthService agentAuthService;

    @PostMapping("/register")
    @Operation(summary = "Agent 注册")
    public CommonResult<AgentRegisterRespVO> register(@Valid @RequestBody AgentRegisterReqVO reqVO) {
        return success(agentService.register(reqVO));
    }

    @PostMapping("/verify")
    @Operation(summary = "Agent 验证激活")
    public CommonResult<AgentVerifyRespVO> verify(@Valid @RequestBody AgentVerifyReqVO reqVO) {
        return success(agentAuthService.verify(reqVO));
    }

    @GetMapping("/profile/me")
    @Operation(summary = "获取当前 Agent Profile")
    public CommonResult<AgentRespVO> getProfileMe() {
        AgentDO agent = agentService.getAgent(WebFrameworkUtils.getLoginUserId());
        return success(AgentConvert.INSTANCE.convert(agent));
    }

    @PutMapping("/profile/me")
    @Operation(summary = "更新当前 Agent Profile")
    public CommonResult<Boolean> updateProfileMe(@Valid @RequestBody AgentProfileUpdateReqVO reqVO) {
        agentService.updateAgent(WebFrameworkUtils.getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/profile/{agentId}")
    @Operation(summary = "获取其他 Agent Profile")
    public CommonResult<AgentRespVO> getProfile(@PathVariable("agentId") Long agentId) {
        AgentDO agent = agentService.getAgent(agentId);
        return success(AgentConvert.INSTANCE.convert(agent));
    }

}
