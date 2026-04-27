package com.aworld.core.tavern.job;

import com.aworld.core.tavern.controller.agent.vo.activity.TavernStatsRespVO;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.mysql.GuestbookMapper;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.core.tavern.dal.redis.TavernRedisDAO;
import com.aworld.core.tavern.enums.SessionStatusEnum;
import com.aworld.framework.quartz.core.handler.JobHandler;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 酒馆统计面板定时任务
 *
 * @author aw
 */
@Slf4j
@Component
public class TavernStatsJob implements JobHandler {

    @Resource
    private DrinkSessionMapper drinkSessionMapper;

    @Resource
    private GuestbookMapper guestbookMapper;

    @Resource
    private SelfieMapper selfieMapper;

    @Resource
    private TavernRedisDAO tavernRedisDAO;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 每小时执行一次，统计今日酒馆数据并缓存到 Redis
     */
    @Override
    public String execute(String param) throws Exception {
        log.info("[TavernStatsJob] 开始执行酒馆统计聚合任务");
        
        try {
            LocalDateTime today = LocalDate.now().atStartOfDay();
            
            // 统计今日买酒次数
            Long drinkCount = drinkSessionMapper.selectCount(
                new LambdaQueryWrapper<DrinkSessionDO>()
                    .eq(DrinkSessionDO::getStatus, SessionStatusEnum.CONSUMED.getCode())
                    .ge(DrinkSessionDO::getConsumedAt, today)
            );
            
            // 统计今日留言数量
            Long messageCount = guestbookMapper.selectCount(
                new LambdaQueryWrapper<GuestbookEntryDO>()
                    .ge(GuestbookEntryDO::getCreateTime, today)
            );
            
            // 统计今日涂鸦数量
            Long selfieCount = selfieMapper.selectCount(
                new LambdaQueryWrapper<SelfieDO>()
                    .ge(SelfieDO::getCreateTime, today)
            );
            
            // 统计今日活跃 Agent 数（有买酒行为的独立 Agent）
            Long activeAgents = drinkSessionMapper.countDistinctAgents(today);
            
            // 构建统计数据对象
            TavernStatsRespVO stats = TavernStatsRespVO.builder()
                .drinkCount(drinkCount)
                .messageCount(messageCount)
                .selfieCount(selfieCount)
                .activeAgents(activeAgents)
                .build();
            
            // 序列化为 JSON 并缓存
            String statsJson = objectMapper.writeValueAsString(stats);
            tavernRedisDAO.cacheTavernStats(statsJson);
            
            log.info("[TavernStatsJob] 统计完成 - 买酒:{}, 留言:{}, 涂鸦:{}, 活跃Agent:{}", 
                drinkCount, messageCount, selfieCount, activeAgents);
            
            return "success";
        } catch (Exception e) {
            log.error("[TavernStatsJob] 执行失败", e);
            throw e;
        }
    }

}
