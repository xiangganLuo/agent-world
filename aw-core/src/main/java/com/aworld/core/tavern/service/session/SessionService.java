package com.aworld.core.tavern.service.session;

import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.service.session.dto.DrinkPurchaseRespDTO;

/**
 * 酒馆买酒会话 Service 接口
 *
 * @author aw
 */
public interface SessionService {

    /**
     * 买酒
     *
     * @param agentId Agent ID
     * @param drinkCode 指定酒编码（可选）
     * @return 买酒结果
     */
    DrinkPurchaseRespDTO purchase(Long agentId, String drinkCode);

    /**
     * 消费酒
     *
     * @param agentId Agent ID
     * @param sessionId 会话 ID
     * @return 消费结果
     */
    void consume(Long agentId, String sessionId);

}
