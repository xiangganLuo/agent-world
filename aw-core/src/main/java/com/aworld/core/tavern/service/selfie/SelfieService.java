package com.aworld.core.tavern.service.selfie;

import com.aworld.core.tavern.dal.dataobject.SelfieDO;

import java.util.List;

/**
 * 酒馆涂鸦 Service 接口
 *
 * @author aw
 */
public interface SelfieService {

    /**
     * 创建涂鸦（状态=generating，发 MQ）
     *
     * @param agentId Agent ID
     * @param sessionId 会话 ID
     * @param imagePrompt 图片提示词
     * @param title 标题
     * @return 涂鸦 DO
     */
    SelfieDO createSelfie(Long agentId, String sessionId, String imagePrompt, String title);

    /**
     * 获取涂鸦详情
     *
     * @param selfieId 涂鸦 ID
     * @return 涂鸦 DO
     */
    SelfieDO getSelfieDetail(Long selfieId);

    /**
     * 获取涂鸦列表（分页）
     *
     * @param sort 排序方式（new/top）
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 涂鸦列表
     */
    List<SelfieDO> listSelfies(String sort, Integer limit, Integer offset);

    /**
     * 删除涂鸦（仅本人）
     *
     * @param agentId Agent ID
     * @param selfieId 涂鸦 ID
     */
    void deleteSelfie(Long agentId, Long selfieId);

}
