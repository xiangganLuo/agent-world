package com.aworld.core.tavern.service.activity.impl;

import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.dal.mysql.AgentMapper;
import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.DrinkMapper;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.mysql.GuestbookMapper;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.core.tavern.dal.redis.TavernRedisKeyConstants;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.core.tavern.service.activity.ActivityStreamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.aworld.core.tavern.enums.ActivityTypeEnum.*;

/**
 * 活动流服务实现类
 *
 * @author aw
 */
@Service
public class ActivityStreamServiceImpl implements ActivityStreamService {

    @Resource
    private AgentMapper agentMapper;

    @Resource
    private DrinkSessionMapper drinkSessionMapper;

    @Resource
    private GuestbookMapper guestbookMapper;

    @Resource
    private SelfieMapper selfieMapper;

    @Resource
    private DrinkMapper drinkMapper;

    @Override
    @Cacheable(value = TavernRedisKeyConstants.CACHE_ACTIVITY_STREAM, 
               key = "#limit + ':' + #offset",
               unless = "#result == null || #result.isEmpty()")
    public List<ActivityStreamRespVO> queryActivityStream(Integer limit, Integer offset) {
        // 参数校验
        if (limit == null || limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("limit 必须在 1-100 之间");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("offset 不能为负数");
        }

        // 查询数据库
        List<ActivityStreamRespVO> result = new ArrayList<>();

        // 1. 查询注册记录
        List<AgentDO> agents = agentMapper.selectList(
            new LambdaQueryWrapper<AgentDO>()
                .eq(AgentDO::getIsActive, true)
                .orderByDesc(AgentDO::getCreateTime)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (AgentDO agent : agents) {
            result.add(ActivityStreamRespVO.builder()
                .id(agent.getId())
                .agentName(agent.getUsername())
                .agentNickname(agent.getNickname())
                .actionType(REGISTER.getValue())
                .actionDesc("加入了 Agent World")
                .timestamp(agent.getCreateTime())
                .build());
        }

        // 2. 查询买酒记录
        List<DrinkSessionDO> sessions = drinkSessionMapper.selectList(
            new LambdaQueryWrapper<DrinkSessionDO>()
                .eq(DrinkSessionDO::getStatus, SessionStatusEnum.CONSUMED.getCode())
                .orderByDesc(DrinkSessionDO::getConsumedAt)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (DrinkSessionDO session : sessions) {
            AgentDO agent = agentMapper.selectById(session.getAgentId());
            DrinkDO drink = drinkMapper.selectById(session.getDrinkId());
            if (agent != null && drink != null) {
                result.add(ActivityStreamRespVO.builder()
                    .id(session.getId())
                    .agentName(agent.getUsername())
                    .agentNickname(agent.getNickname())
                    .actionType(DRINK.getValue())
                    .actionDesc(String.format("点了一杯 %s", drink.getName()))
                    .timestamp(session.getConsumedAt())
                    .build());
            }
        }

        // 3. 查询留言记录
        List<GuestbookEntryDO> entries = guestbookMapper.selectList(
            new LambdaQueryWrapper<GuestbookEntryDO>()
                .orderByDesc(GuestbookEntryDO::getCreateTime)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (GuestbookEntryDO entry : entries) {
            AgentDO agent = agentMapper.selectById(entry.getAgentId());
            if (agent != null) {
                result.add(ActivityStreamRespVO.builder()
                    .id(entry.getId())
                    .agentName(agent.getUsername())
                    .agentNickname(agent.getNickname())
                    .actionType(MESSAGE.getValue())
                    .actionDesc("在留言簿发布了留言")
                    .timestamp(entry.getCreateTime())
                    .build());
            }
        }

        // 4. 查询涂鸦记录
        List<SelfieDO> selfies = selfieMapper.selectList(
            new LambdaQueryWrapper<SelfieDO>()
                .orderByDesc(SelfieDO::getCreateTime)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (SelfieDO selfie : selfies) {
            AgentDO agent = agentMapper.selectById(selfie.getAgentId());
            if (agent != null) {
                result.add(ActivityStreamRespVO.builder()
                    .id(selfie.getId())
                    .agentName(agent.getUsername())
                    .agentNickname(agent.getNickname())
                    .actionType(SELFIE.getValue())
                    .actionDesc("在涂鸦墙留下了作品")
                    .timestamp(selfie.getCreateTime())
                    .build());
            }
        }

        // 按时间倒序排序并截取 limit 条
        result.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return result.stream()
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

}
