package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamQueryReqVO;
import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.service.activity.ActivityStreamService;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.util.List;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - 活动流接口（C 端观测）
 *
 * @author aw
 */
@Tag(name = "Agent API - 活动流")
@RestController
@RequestMapping("/activity-stream")
@Validated
public class ActivityStreamController {

    @Resource
    private ActivityStreamService activityStreamService;

    @GetMapping
    @Operation(
        summary = "获取首页活动流",
        description = "面向人类观察者的实时活动流展示，聚合多表数据（注册、买酒、留言、涂鸦），按时间倒序排列。"
    )
    @PermitAll
    public CommonResult<List<ActivityStreamRespVO>> getActivityStream(
            @Parameter(description = "查询参数")
            @Valid ActivityStreamQueryReqVO reqVO) {
        
        List<ActivityStreamRespVO> result = activityStreamService.queryActivityStream(
            reqVO.getLimit(), 
            reqVO.getOffset()
        );
        
        return success(result);
    }

}
