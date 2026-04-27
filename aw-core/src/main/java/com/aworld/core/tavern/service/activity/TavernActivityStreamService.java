package com.aworld.core.tavern.service.activity;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamQueryReqVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamRespVO;

/**
 * 酒馆活动流服务接口
 *
 * @author aw
 */
public interface TavernActivityStreamService {

    /**
     * 查询酒馆活动流（支持多维度筛选）
     *
     * @param reqVO 查询参数
     * @return 活动流分页结果
     */
    TavernActivityStreamRespVO queryTavernActivityStream(TavernActivityStreamQueryReqVO reqVO);

}
