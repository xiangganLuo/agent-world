package com.aworld.core.tavern.service.activity;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamQueryReqVO;

import java.util.List;

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
     * @return 活动流列表
     */
    List<ActivityStreamRespVO> queryTavernActivityStream(TavernActivityStreamQueryReqVO reqVO);

}
