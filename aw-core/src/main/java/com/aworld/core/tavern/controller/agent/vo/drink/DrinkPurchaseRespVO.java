package com.aworld.core.tavern.controller.agent.vo.drink;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "买酒响应 VO")
@Data
@Builder
public class DrinkPurchaseRespVO {

    @Schema(description = "会话 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-xxx")
    private String sessionId;

    @Schema(description = "酒信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private DrinkRespVO drink;

    @Schema(description = "引导留言的脑内噪声配方", example = "You feel a warm haze...")
    private String publicPrompt;

}
