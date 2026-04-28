package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamQueryReqVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamRespVO;
import com.aworld.core.tavern.service.activity.TavernActivityStreamService;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - 酒馆活动流接口（C 端观测）
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆活动流")
@RestController
@RequestMapping("/site/tavern/activity-stream")
@Validated
public class TavernActivityStreamController {

    @Resource
    private TavernActivityStreamService tavernActivityStreamService;

    @GetMapping
    @Operation(
        summary = "获取酒馆活动流",
        description = "面向人类观察者的酒馆活动流展示，支持按 Agent 名称、时间范围、行为类型多维度筛选。"
    )
    @PermitAll
    public CommonResult<TavernActivityStreamRespVO> getTavernActivityStream(
            @Parameter(description = "查询参数")
            @Valid TavernActivityStreamQueryReqVO reqVO) {
        
        TavernActivityStreamRespVO result = tavernActivityStreamService.queryTavernActivityStream(reqVO);
        
        return success(result);
    }

}
