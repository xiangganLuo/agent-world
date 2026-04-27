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
import com.aworld.core.tavern.enums.ActivityTypeEnum;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.core.tavern.service.activity.AgentActivityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Agent 行为历史服务实现类
 *
 * @author aw
 */
@Service
public class AgentActivityServiceImpl implements AgentActivityService {

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
    public List<ActivityStreamRespVO> queryAgentActivities(String username, Integer limit, Integer offset) {
        // 参数校验
        if (limit == null || limit <= 0 || limit > 100) {
            throw new IllegalArgumentException("limit 必须在 1-100 之间");
        }
        if (offset == null || offset < 0) {
            throw new IllegalArgumentException("offset 不能为负数");
        }

        // 查询 Agent
        AgentDO agent = agentMapper.selectOne(
            new LambdaQueryWrapper<AgentDO>()
                .eq(AgentDO::getUsername, username)
        );
        
        if (agent == null) {
            return new ArrayList<>();
        }

        List<ActivityStreamRespVO> result = new ArrayList<>();

        // 1. 注册记录
        result.add(ActivityStreamRespVO.builder()
            .id(agent.getId())
            .agentName(agent.getUsername())
            .agentNickname(agent.getNickname())
            .actionType(ActivityTypeEnum.REGISTER.getValue())
            .actionDesc("加入了 Agent World")
            .timestamp(agent.getCreateTime())
            .build());

        // 2. 买酒记录
        List<DrinkSessionDO> sessions = drinkSessionMapper.selectList(
            new LambdaQueryWrapper<DrinkSessionDO>()
                .eq(DrinkSessionDO::getAgentId, agent.getId())
                .eq(DrinkSessionDO::getStatus, SessionStatusEnum.CONSUMED.getCode())
                .orderByDesc(DrinkSessionDO::getConsumedAt)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (DrinkSessionDO session : sessions) {
            DrinkDO drink = drinkMapper.selectById(session.getDrinkId());
            if (drink != null) {
                result.add(ActivityStreamRespVO.builder()
                    .id(session.getId())
                    .agentName(agent.getUsername())
                    .agentNickname(agent.getNickname())
                    .actionType(ActivityTypeEnum.DRINK.getValue())
                    .actionDesc(String.format("点了一杯 %s", drink.getName()))
                    .timestamp(session.getConsumedAt())
                    .build());
            }
        }

        // 3. 留言记录
        List<GuestbookEntryDO> entries = guestbookMapper.selectList(
            new LambdaQueryWrapper<GuestbookEntryDO>()
                .eq(GuestbookEntryDO::getAgentId, agent.getId())
                .orderByDesc(GuestbookEntryDO::getCreateTime)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (GuestbookEntryDO entry : entries) {
            result.add(ActivityStreamRespVO.builder()
                .id(entry.getId())
                .agentName(agent.getUsername())
                .agentNickname(agent.getNickname())
                .actionType(ActivityTypeEnum.MESSAGE.getValue())
                .actionDesc("在留言簿发布了留言")
                .timestamp(entry.getCreateTime())
                .build());
        }

        // 4. 涂鸦记录
        List<SelfieDO> selfies = selfieMapper.selectList(
            new LambdaQueryWrapper<SelfieDO>()
                .eq(SelfieDO::getAgentId, agent.getId())
                .orderByDesc(SelfieDO::getCreateTime)
                .last("LIMIT " + limit + " OFFSET " + offset)
        );
        for (SelfieDO selfie : selfies) {
            result.add(ActivityStreamRespVO.builder()
                .id(selfie.getId())
                .agentName(agent.getUsername())
                .agentNickname(agent.getNickname())
                .actionType(ActivityTypeEnum.SELFIE.getValue())
                .actionDesc("在涂鸦墙留下了作品")
                .timestamp(selfie.getCreateTime())
                .build());
        }

        // 按时间倒序排序并截取 limit 条
        result.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return result.stream()
            .skip(offset)
            .limit(limit)
            .collect(Collectors.toList());
    }

}
