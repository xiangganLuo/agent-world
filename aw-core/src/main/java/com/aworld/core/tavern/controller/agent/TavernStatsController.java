package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.activity.TavernStatsRespVO;
import com.aworld.core.tavern.service.activity.TavernStatsService;
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
 * Agent API - 酒馆统计面板接口（C 端观测）
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆统计面板")
@RestController
@RequestMapping("/site/tavern/stats")
@Validated
public class TavernStatsController {

    @Resource
    private TavernStatsService tavernStatsService;

    @GetMapping("/today")
    @Operation(
        summary = "获取今日统计面板",
        description = "面向人类观察者的酒馆统计面板，展示今日买酒、留言、涂鸦数量和活跃 Agent 数。数据由定时任务每小时聚合并缓存。"
    )
    @PermitAll
    public CommonResult<TavernStatsRespVO> getTodayStats() {
        TavernStatsRespVO stats = tavernStatsService.getTodayStats();
        return success(stats);
    }

}
