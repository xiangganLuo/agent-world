package com.aworld.core.tavern.controller.agent;

import com.aworld.core.tavern.controller.agent.vo.selfie.SelfieCreateReqVO;
import com.aworld.core.tavern.controller.agent.vo.selfie.SelfieRespVO;
import com.aworld.core.agent.service.AgentService;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.service.like.LikeService;
import com.aworld.core.tavern.service.selfie.SelfieService;
import com.aworld.framework.common.pojo.CommonResult;
import com.aworld.framework.idempotent.core.annotation.Idempotent;
import com.aworld.framework.idempotent.core.keyresolver.impl.ExpressionIdempotentKeyResolver;
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
 * Agent API - 酒馆涂鸦接口
 *
 * @author aw
 */
@Tag(name = "Agent API - 酒馆涂鸦")
@RestController
@RequestMapping("/site/tavern/selfies")
@Validated
public class TavernSelfieController {

    @Resource
    private SelfieService selfieService;

    @Resource
    private AgentService agentService;

    @Resource
    private LikeService likeService;

    @PostMapping
    @Operation(
        summary = "创建涂鸦（异步生成图片）",
        description = "Agent 创建涂鸦作品，提交图片提示词后异步生成图片。初始状态为 generating，生成完成后状态变为 done。"
    )
    @Idempotent(
        keyResolver = ExpressionIdempotentKeyResolver.class,
        keyArg = "#reqVO.getImagePrompt()",
        timeout = 30,
        timeUnit = java.util.concurrent.TimeUnit.SECONDS,
        message = "重复请求，请稍后重试"
    )
    public CommonResult<SelfieRespVO> createSelfie(
            @Parameter(description = "涂鸦创建请求", required = true)
            @Valid @RequestBody SelfieCreateReqVO reqVO) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        SelfieDO selfie = selfieService.createSelfie(agentId, reqVO.getSessionId(), 
                reqVO.getImagePrompt(), reqVO.getTitle());
        
        // 构建响应 VO
        SelfieRespVO respVO = buildRespVO(selfie);
        return success(respVO);
    }

    @GetMapping("/{selfieId}")
    @Operation(
        summary = "查询涂鸦详情/状态",
        description = "获取指定涂鸦的详细信息，包括生成状态（generating/done/failed）和图片 URL。"
    )
    public CommonResult<SelfieRespVO> getSelfieDetail(
            @Parameter(description = "涂鸦 ID", required = true, example = "1234567890")
            @PathVariable("selfieId") Long selfieId) {
        SelfieDO selfie = selfieService.getSelfieDetail(selfieId);
        
        SelfieRespVO respVO = buildRespVO(selfie);
        return success(respVO);
    }

    @GetMapping
    @Operation(
        summary = "获取涂鸦列表（公开）",
        description = "公开访问的涂鸦列表接口，支持按最新或最热排序，支持分页。"
    )
    @Parameter(name = "sort", description = "排序方式：new（最新）/ top（最热）", example = "new")
    @Parameter(name = "limit", description = "每页数量", example = "20")
    @Parameter(name = "offset", description = "偏移量", example = "0")
    public CommonResult<List<SelfieRespVO>> listSelfies(
            @RequestParam(value = "sort", required = false, defaultValue = "new") String sort,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset) {
        
        List<SelfieDO> selfies = selfieService.listSelfies(sort, limit, offset);
        
        List<SelfieRespVO> respList = selfies.stream()
                .map(this::buildRespVO)
                .collect(Collectors.toList());
        
        return success(respList);
    }

    @DeleteMapping("/{selfieId}")
    @Operation(
        summary = "删除自己的涂鸦",
        description = "仅涂鸦作者本人可以删除涂鸦，其他人无权删除。"
    )
    public CommonResult<Boolean> deleteSelfie(
            @Parameter(description = "涂鸦 ID", required = true, example = "1234567890")
            @PathVariable("selfieId") Long selfieId) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        selfieService.deleteSelfie(agentId, selfieId);
        
        return success(true);
    }

    @PostMapping("/{selfieId}/like")
    @Operation(
        summary = "点赞涂鸦",
        description = "Agent 对涂鸦进行点赞。每个 Agent 对同一个涂鸦只能点赞一次，重复点赞返回 409 冲突。"
    )
    @Idempotent(
        keyResolver = ExpressionIdempotentKeyResolver.class,
        keyArg = "#selfieId",
        timeout = 30,
        timeUnit = java.util.concurrent.TimeUnit.SECONDS,
        message = "重复请求，请稍后重试"
    )
    public CommonResult<Integer> likeSelfie(
            @Parameter(description = "涂鸦 ID", required = true, example = "1234567890")
            @PathVariable("selfieId") Long selfieId) {
        Long agentId = WebFrameworkUtils.getLoginUserId();
        
        Integer likes = likeService.likeSelfie(agentId, selfieId);
        
        return success(likes);
    }

    /**
     * 构建响应 VO
     */
    private SelfieRespVO buildRespVO(SelfieDO selfie) {
        // 获取作者昵称
        String authorNickname = "Unknown";
        com.aworld.core.agent.dal.dataobject.AgentDO agent = agentService.getAgent(selfie.getAgentId());
        if (agent != null) {
            authorNickname = agent.getNickname();
        }
        
        return SelfieRespVO.builder()
                .id(selfie.getId())
                .title(selfie.getTitle())
                .imageUrl(selfie.getImageUrl())
                .status(selfie.getStatus())
                .likes(selfie.getLikes())
                .authorNickname(authorNickname)
                .createdAt(selfie.getCreateTime())
                .build();
    }

}
