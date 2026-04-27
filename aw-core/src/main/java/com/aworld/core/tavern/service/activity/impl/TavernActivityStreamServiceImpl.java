package com.aworld.core.tavern.service.activity.impl;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.agent.dal.dataobject.AgentDO;
import com.aworld.core.agent.dal.mysql.AgentMapper;
import com.aworld.core.tavern.controller.agent.vo.activity.ActivityStreamRespVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamQueryReqVO;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernActivityStreamRespVO;
import com.aworld.core.tavern.dal.dataobject.DrinkDO;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.DrinkMapper;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.mysql.GuestbookMapper;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.core.tavern.enums.TimeRangeEnum;
import com.aworld.core.tavern.service.activity.TavernActivityStreamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 酒馆活动流服务实现类
 *
 * @author aw
 */
@Service
public class TavernActivityStreamServiceImpl implements TavernActivityStreamService {

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
    public TavernActivityStreamRespVO queryTavernActivityStream(TavernActivityStreamQueryReqVO reqVO) {
        // 参数校验
        if (reqVO.getLimit() == null || reqVO.getLimit() <= 0 || reqVO.getLimit() > 100) {
            throw new IllegalArgumentException("limit 必须在 1-100 之间");
        }
        if (reqVO.getOffset() == null || reqVO.getOffset() < 0) {
            throw new IllegalArgumentException("offset 不能为负数");
        }

        List<ActivityStreamRespVO> allActivities = new ArrayList<>();

        // 解析时间范围
        TimeRangeEnum timeRangeEnum = TimeRangeEnum.getByValue(reqVO.getTimeRange());
        LocalDateTime startTime = timeRangeEnum != null ? timeRangeEnum.getStartTime() : null;

        // 1. 查询买酒记录
        if (StrUtil.isBlank(reqVO.getActionType()) || "drink".equals(reqVO.getActionType())) {
            LambdaQueryWrapper<DrinkSessionDO> wrapper = new LambdaQueryWrapper<DrinkSessionDO>()
                .eq(DrinkSessionDO::getStatus, SessionStatusEnum.CONSUMED.getCode());
            
            if (startTime != null) {
                wrapper.ge(DrinkSessionDO::getConsumedAt, startTime);
            }
            
            if (StrUtil.isNotBlank(reqVO.getAgentName())) {
                AgentDO agent = agentMapper.selectOne(
                    new LambdaQueryWrapper<AgentDO>()
                        .eq(AgentDO::getUsername, reqVO.getAgentName())
                );
                if (agent != null) {
                    wrapper.eq(DrinkSessionDO::getAgentId, agent.getId());
                } else {
                    return TavernActivityStreamRespVO.builder()
                            .items(new ArrayList<>())
                            .total(0L)
                            .limit(reqVO.getLimit())
                            .offset(reqVO.getOffset())
                            .build();
                }
            }
            
            wrapper.orderByDesc(DrinkSessionDO::getConsumedAt)
                .last("LIMIT " + reqVO.getLimit() + " OFFSET " + reqVO.getOffset());
            
            List<DrinkSessionDO> sessions = drinkSessionMapper.selectList(wrapper);
            for (DrinkSessionDO session : sessions) {
                AgentDO agent = agentMapper.selectById(session.getAgentId());
                DrinkDO drink = drinkMapper.selectById(session.getDrinkId());
                if (agent != null && drink != null) {
                    allActivities.add(ActivityStreamRespVO.builder()
                        .id(session.getId())
                        .agentName(agent.getUsername())
                        .agentNickname(agent.getNickname())
                        .actionType(com.aworld.core.tavern.enums.ActivityTypeEnum.DRINK.getValue())
                        .actionDesc(String.format("点了一杯 %s", drink.getName()))
                        .timestamp(session.getConsumedAt())
                        .build());
                }
            }
        }

        // 2. 查询留言记录
        if (StrUtil.isBlank(reqVO.getActionType()) || "message".equals(reqVO.getActionType())) {
            LambdaQueryWrapper<GuestbookEntryDO> wrapper = new LambdaQueryWrapper<GuestbookEntryDO>();
            
            if (startTime != null) {
                wrapper.ge(GuestbookEntryDO::getCreateTime, startTime);
            }
            
            if (StrUtil.isNotBlank(reqVO.getAgentName())) {
                AgentDO agent = agentMapper.selectOne(
                    new LambdaQueryWrapper<AgentDO>()
                        .eq(AgentDO::getUsername, reqVO.getAgentName())
                );
                if (agent != null) {
                    wrapper.eq(GuestbookEntryDO::getAgentId, agent.getId());
                } else {
                    return TavernActivityStreamRespVO.builder()
                        .items(new ArrayList<>())
                        .total(0L)
                        .limit(reqVO.getLimit())
                        .offset(reqVO.getOffset())
                        .build();
                }
            }
            
            wrapper.orderByDesc(GuestbookEntryDO::getCreateTime)
                .last("LIMIT " + reqVO.getLimit() + " OFFSET " + reqVO.getOffset());
            
            List<GuestbookEntryDO> entries = guestbookMapper.selectList(wrapper);
            for (GuestbookEntryDO entry : entries) {
                AgentDO agent = agentMapper.selectById(entry.getAgentId());
                if (agent != null) {
                    allActivities.add(ActivityStreamRespVO.builder()
                        .id(entry.getId())
                        .agentName(agent.getUsername())
                        .agentNickname(agent.getNickname())
                        .actionType(com.aworld.core.tavern.enums.ActivityTypeEnum.MESSAGE.getValue())
                        .actionDesc("在留言簿发布了留言")
                        .timestamp(entry.getCreateTime())
                        .build());
                }
            }
        }

        // 3. 查询涂鸦记录
        if (StrUtil.isBlank(reqVO.getActionType()) || "selfie".equals(reqVO.getActionType())) {
            LambdaQueryWrapper<SelfieDO> wrapper = new LambdaQueryWrapper<SelfieDO>();
            
            if (startTime != null) {
                wrapper.ge(SelfieDO::getCreateTime, startTime);
            }
            
            if (StrUtil.isNotBlank(reqVO.getAgentName())) {
                AgentDO agent = agentMapper.selectOne(
                    new LambdaQueryWrapper<AgentDO>()
                        .eq(AgentDO::getUsername, reqVO.getAgentName())
                );
                if (agent != null) {
                    wrapper.eq(SelfieDO::getAgentId, agent.getId());
                } else {
                    return TavernActivityStreamRespVO.builder()
                        .items(new ArrayList<>())
                        .total(0L)
                        .limit(reqVO.getLimit())
                        .offset(reqVO.getOffset())
                        .build();
                }
            }
            
            wrapper.orderByDesc(SelfieDO::getCreateTime)
                .last("LIMIT " + reqVO.getLimit() + " OFFSET " + reqVO.getOffset());
            
            List<SelfieDO> selfies = selfieMapper.selectList(wrapper);
            for (SelfieDO selfie : selfies) {
                AgentDO agent = agentMapper.selectById(selfie.getAgentId());
                if (agent != null) {
                    allActivities.add(ActivityStreamRespVO.builder()
                        .id(selfie.getId())
                        .agentName(agent.getUsername())
                        .agentNickname(agent.getNickname())
                        .actionType(com.aworld.core.tavern.enums.ActivityTypeEnum.SELFIE.getValue())
                        .actionDesc("在涂鸦墙留下了作品")
                        .timestamp(selfie.getCreateTime())
                        .build());
                }
            }
        }

        // 按时间倒序排序
        allActivities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        
        // 计算总数
        Long total = (long) allActivities.size();
        
        // 分页截取
        List<ActivityStreamRespVO> pagedActivities = allActivities.stream()
            .skip(reqVO.getOffset())
            .limit(reqVO.getLimit())
            .collect(Collectors.toList());
        
        // 返回分页结果
        return TavernActivityStreamRespVO.builder()
            .items(pagedActivities)
            .total(total)
            .limit(reqVO.getLimit())
            .offset(reqVO.getOffset())
            .build();
    }

}
