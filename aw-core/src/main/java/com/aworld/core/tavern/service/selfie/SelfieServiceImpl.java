package com.aworld.core.tavern.service.selfie;

import cn.hutool.core.util.IdUtil;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.core.tavern.enums.SelfieStatusEnum;
import com.aworld.core.tavern.enums.SortOrderEnum;
import com.aworld.core.tavern.mq.message.ImageGenerateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

/**
 * 酒馆涂鸦 Service 实现类
 *
 * @author aw
 */
@Service
@Validated
@Slf4j
public class SelfieServiceImpl implements SelfieService {

    @Resource
    private SelfieMapper selfieMapper;

    @Resource
    private DrinkSessionMapper drinkSessionMapper;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SelfieDO createSelfie(Long agentId, String sessionId, String imagePrompt, String title) {
        // 1. 校验会话是否存在且属于当前 Agent
        DrinkSessionDO session = drinkSessionMapper.selectBySessionId(sessionId);
        if (session == null || !session.getAgentId().equals(agentId)) {
            throw new RuntimeException("会话不存在或无权操作");
        }

        // 2. 创建涂鸦记录（状态=generating）
        SelfieDO selfie = SelfieDO.builder()
                .agentId(agentId)
                .sessionId(sessionId)
                .drinkId(session.getDrinkId())
                .title(title != null ? title : "无题")
                .imagePrompt(imagePrompt)
                .status(SelfieStatusEnum.GENERATING.getCode())
                .likes(0)
                .idempotencyKey(IdUtil.fastSimpleUUID())
                .build();
        selfieMapper.insert(selfie);

        // 3. 发送 MQ 消息触发异步图片生成
        log.info("[createSelfie][Agent {} 创建了涂鸦 {}，准备异步生成图片]", agentId, selfie.getId());
        ImageGenerateMessage message = new ImageGenerateMessage();
        message.setSelfieId(selfie.getId());
        message.setImagePrompt(imagePrompt);
        eventPublisher.publishEvent(message);

        return selfie;
    }

    @Override
    public SelfieDO getSelfieDetail(Long selfieId) {
        SelfieDO selfie = selfieMapper.selectById(selfieId);
        if (selfie == null) {
            throw new RuntimeException("涂鸦不存在");
        }
        return selfie;
    }

    @Override
    public List<SelfieDO> listSelfies(String sort, Integer limit, Integer offset) {
        // 默认值
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }

        // 构建查询条件
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SelfieDO> queryWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        // 排序：new（最新）或 top（最热）
        if (SortOrderEnum.TOP.getCode().equals(sort)) {
            // 按点赞数降序，再按创建时间降序
            queryWrapper.orderByDesc(SelfieDO::getLikes)
                    .orderByDesc(SelfieDO::getCreateTime);
        } else {
            // 默认按创建时间降序（最新）
            queryWrapper.orderByDesc(SelfieDO::getCreateTime);
        }
        
        // 分页
        queryWrapper.last("LIMIT " + limit + " OFFSET " + offset);
        
        return selfieMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSelfie(Long agentId, Long selfieId) {
        // 查询涂鸦
        SelfieDO selfie = selfieMapper.selectById(selfieId);
        if (selfie == null) {
            throw new RuntimeException("涂鸦不存在");
        }

        // 校验是否为本人
        if (!selfie.getAgentId().equals(agentId)) {
            throw new RuntimeException("无权删除他人的涂鸦");
        }

        // 删除
        selfieMapper.deleteById(selfieId);
    }

}
