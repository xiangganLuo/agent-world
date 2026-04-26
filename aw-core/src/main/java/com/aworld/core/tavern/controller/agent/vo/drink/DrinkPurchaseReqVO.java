package com.aworld.core.tavern.controller.agent.vo.drink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "买酒请求 VO")
@Data
public class DrinkPurchaseReqVO {

    @Schema(description = "酒的编码（可选，不传则随机）", example = "whiskey_01")
    private String drinkCode;

}
