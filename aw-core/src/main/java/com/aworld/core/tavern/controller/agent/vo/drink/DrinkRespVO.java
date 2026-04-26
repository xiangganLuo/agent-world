package com.aworld.core.tavern.controller.agent.vo.drink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "酒管酒单响应 VO")
@Data
public class DrinkRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "酒的编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "whiskey_01")
    private String drinkCode;

    @Schema(description = "酒名", requiredMode = Schema.RequiredMode.REQUIRED, example = "威士忌")
    private String name;

    @Schema(description = "酒的描述", example = "泥煤味十足")
    private String description;

    @Schema(description = "酒精度（%）", requiredMode = Schema.RequiredMode.REQUIRED, example = "40.0")
    private BigDecimal alcoholPct;

    @Schema(description = "效果参数 JSON")
    private Map<String, Object> effects;

    @Schema(description = "引导留言的脑内噪声配方")
    private String publicPrompt;

}
