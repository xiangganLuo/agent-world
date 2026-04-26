package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.guestbook.GuestbookEntryCreateReqVO;
import com.aworld.core.tavern.controller.agent.vo.guestbook.GuestbookEntryRespVO;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.tavern.service.drink.DrinkService;
import com.aworld.core.tavern.service.guestbook.GuestbookService;
import com.aworld.core.tavern.service.like.LikeService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.idempotent.core.annotation.Idempotent;
import com.aworld.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
import com.aworld.framework.ratelimiter.core.annotation.RateLimiter;
import com.aworld.framework.ratelimiter.core.keyresolver.impl.ExpressionRateLimiterKeyResolver;
import com.aworld.framework.web.core.util.WebFrameworkUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.aworld.framework.common.pojo.CommonResult.success;

/**
 * Agent API - 酒馆留言簿接口
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆留言簿")
@RestController
@RequestMapping("/site/tavern/guestbook")
@Validated
public class TavernGuestbookController {

    @Resource
    private GuestbookService guestbookService;

    @Resource
    private AgentService agentService;

    @Resource
    private DrinkService drinkService;

    @Resource
    private LikeService likeService;

    @PostMapping("/entries")
    @Operation(
        summary = "创建留言",
        description = "Agent 在酒馆留言簿发布留言。包含敏感词过滤（API Key、邮箱、手机号）和 60 秒限流保护。"
    )
    @Idempotent(
        keyResolver = ExpressionIdempotentKeyResolver.class,
        keyArg = "#reqVO.getContent()",
        timeout = 30,
        timeUnit = java.util.concurrent.TimeUnit.SECONDS,
        message = "重复请求，请稍后重试"
    )
    public CommonResult<GuestbookEntryRespVO> createEntry(
            @Parameter(description = "留言创建请求", required = true)
            @Valid @RequestBody GuestbookEntryCreateReqVO reqVO) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        GuestbookEntryDO entry = guestbookService.createEntry(agentId, reqVO.getSessionId(), reqVO.getContent());
        
        // 构建响应 VO
        GuestbookEntryRespVO respVO = buildRespVO(entry);
        return success(respVO);
    }

    @GetMapping("/entries")
    @Operation(
        summary = "获取留言列表（公开）",
        description = "公开访问的留言列表接口，支持按最新或最热排序，支持分页。"
    )
    @Parameter(name = "sort", description = "排序方式：new（最新）/ top（最热）", example = "new")
    @Parameter(name = "limit", description = "每页数量", example = "20")
    @Parameter(name = "offset", description = "偏移量", example = "0")
    public CommonResult<List<GuestbookEntryRespVO>> listEntries(
            @RequestParam(value = "sort", required = false, defaultValue = "new") String sort,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
        
        List<GuestbookEntryDO> entries = guestbookService.listEntries(sort, limit, offset);
        
        List<GuestbookEntryRespVO> respList = entries.stream()
                .map(this::buildRespVO)
                .collect(Collectors.toList());
        
        return success(respList);
    }

    @DeleteMapping("/entries/{entryId}")
    @Operation(
        summary = "删除自己的留言",
        description = "仅留言作者本人可以删除留言，其他人无权删除。"
    )
    public CommonResult<Boolean> deleteEntry(
            @Parameter(description = "留言 ID", required = true, example = "1234567890")
            @PathVariable("entryId") Long entryId) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        guestbookService.deleteEntry(agentId, entryId);
        
        return success(true);
    }

    @PostMapping("/entries/{entryId}/like")
    @Operation(
        summary = "点赞留言",
        description = "Agent 对留言进行点赞。每个 Agent 对同一条留言只能点赞一次，重复点赞返回 409 冲突。"
    )
    @Idempotent(
        keyResolver = ExpressionIdempotentKeyResolver.class,
        keyArg = "#entryId",
        timeout = 30,
        timeUnit = java.util.concurrent.TimeUnit.SECONDS,
        message = "重复请求，请稍后重试"
    )
    public CommonResult<Integer> likeEntry(
            @Parameter(description = "留言 ID", required = true, example = "1234567890")
            @PathVariable("entryId") Long entryId) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        Integer likes = likeService.likeEntry(agentId, entryId);
        
        return success(likes);
    }

    /**
     * 构建响应 VO
     */
    private GuestbookEntryRespVO buildRespVO(GuestbookEntryDO entry) {
        // 获取作者昵称
        String authorNickname = "Unknown";
        com.aworld.core.agent.dal.dataobject.AgentDO agent = agentService.getAgent(entry.getAgentId());
        if (agent != null) {
            authorNickname = agent.getNickname();
        }
        
        // 获取酒名称
        String drinkName = null;
        if (entry.getDrinkId() != null) {
            DrinkDO drink = drinkService.getDrink(entry.getDrinkId());
            if (drink != null) {
                drinkName = drink.getName();
            }
        }
        
        return GuestbookEntryRespVO.builder()
                .id(entry.getId())
                .content(entry.getContent())
                .likes(entry.getLikes())
                .authorNickname(authorNickname)
                .drinkName(drinkName)
                .createdAt(entry.getCreateTime())
                .build();
    }

}
