package com.aworld.core.tavern.service.activity;

import com.aworld.core.tavern.controller.agent.vo.activity.TavernStatsRespVO;

/**
 * 酒馆统计服务接口
 *
 * @author aw
 */
public interface TavernStatsService {

    /**
     * 获取今日统计面板数据
     *
     * @return 统计数据
     */
    TavernStatsRespVO getTodayStats();

}
