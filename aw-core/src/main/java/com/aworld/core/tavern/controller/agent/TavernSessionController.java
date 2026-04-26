package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.drink.DrinkPurchaseReqVO;
import com.aworld.core.tavern.controller.agent.vo.drink.DrinkPurchaseRespVO;
import com.aworld.core.tavern.convert.drink.AgentDrinkConvert;
import com.aworld.core.tavern.service.session.SessionService;
import com.aworld.core.tavern.service.session.dto.DrinkPurchaseRespDTO;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - 酒馆会话与消费接口
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆会话与消费")
@RestController
@RequestMapping("/site/tavern")
@Validated
public class TavernSessionController {

    @Resource
    private SessionService sessionService;

    @PostMapping("/drinks/random")
    @Operation(
        summary = "买酒（随机或指定）",
        description = "Agent 购买酒馆中的酒，可以随机获取或指定特定酒类。返回会话 ID 和酒的详细信息。"
    )
    public CommonResult<DrinkPurchaseRespVO> purchase(
            @Parameter(description = "买酒请求参数（可选，不传则随机分配）", required = false)
            @Valid @RequestBody(required = false) DrinkPurchaseReqVO reqVO) {
        String drinkCode = reqVO != null ? reqVO.getDrinkCode() : null;
        
        DrinkPurchaseRespDTO result = sessionService.purchase(WebFrameworkUtils.getLoginUserId(), drinkCode);
        
        return success(DrinkPurchaseRespVO.builder()
                .sessionId(result.getSessionId())
                .drink(AgentDrinkConvert.INSTANCE.convert(result.getDrink()))
                .publicPrompt(result.getPublicPrompt())
                .build());
    }

    @PostMapping("/sessions/{sessionId}/consume")
    @Operation(
        summary = "消费酒",
        description = "Agent 消费已购买的酒，触发 AI 生成饮酒体验并异步写入记忆。"
    )
    public CommonResult<Boolean> consume(
            @Parameter(description = "会话 ID", required = true, example = "session-uuid-12345")
            @PathVariable("sessionId") String sessionId) {
        sessionService.consume(WebFrameworkUtils.getLoginUserId(), sessionId);
        return success(true);
    }

}
