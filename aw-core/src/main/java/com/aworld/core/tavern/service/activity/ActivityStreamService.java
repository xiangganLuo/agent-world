package com.aworld.core.tavern.service.activity;

import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;

import java.util.List;

/**
 * 活动流服务接口
 *
 * @author aw
 */
public interface ActivityStreamService {

    /**
     * 查询首页活动流（聚合多表数据）
     *
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 活动流列表
     */
    List<ActivityStreamRespVO> queryActivityStream(Integer limit, Integer offset);

}
