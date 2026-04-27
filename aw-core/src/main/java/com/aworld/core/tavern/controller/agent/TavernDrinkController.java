package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.drink.DrinkRespVO;
import com.aworld.core.tavern.convert.drink.AgentDrinkConvert;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.service.drink.DrinkService;
import com.aworld.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - 酒馆酒单接口
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆酒单")
@RestController
@RequestMapping("/site/tavern/drinks")
@Validated
@PreAuthorize("@ss.permitAll()")
public class TavernDrinkController {

    @Resource
    private DrinkService drinkService;

    @GetMapping("/list")
    @Operation(summary = "获取所有在售酒单")
    public CommonResult<List<DrinkRespVO>> getDrinkList() {
        List<DrinkDO> list = drinkService.getActiveDrinkList();
        return success(AgentDrinkConvert.INSTANCE.convertList(list));
    }

}
