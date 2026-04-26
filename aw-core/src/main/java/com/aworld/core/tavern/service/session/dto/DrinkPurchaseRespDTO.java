package com.aworld.core.tavern.service.session.dto;

import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DrinkPurchaseRespDTO {

    /**
     * 会话 ID
     */
    private String sessionId;

    /**
     * 酒信息
     */
    private DrinkDO drink;

    /**
     * 引导留言的脑内噪声配方
     */
    private String publicPrompt;

}
