package com.aworld.core.tavern.service.activity.impl;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.tavern.controller.agent.vo.activity.TavernStatsRespVO;
import com.aworld.core.tavern.dal.redis.TavernRedisDAO;
import com.aworld.core.tavern.service.activity.TavernStatsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.aworld.core.tavern.dal.redis.TavernRedisKeyConstants.CACHE_TAVERN_STATS;

/**
 * 酒馆统计服务实现类
 *
 * @author aw
 */
@Service
@Slf4j
public class TavernStatsServiceImpl implements TavernStatsService {

    @Resource
    private TavernRedisDAO tavernRedisDAO;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public TavernStatsRespVO getTodayStats() {
        String statsJson = tavernRedisDAO.getTavernStats();
        
        if (StrUtil.isBlank(statsJson)) {
            log.warn("[TavernStatsService] 统计数据缓存不存在，返回默认值");
            return TavernStatsRespVO.builder()
                .drinkCount(0L)
                .messageCount(0L)
                .selfieCount(0L)
                .activeAgents(0L)
                .build();
        }
        
        try {
            return objectMapper.readValue(statsJson, TavernStatsRespVO.class);
        } catch (Exception e) {
            log.error("[TavernStatsService] 解析统计数据失败", e);
            return TavernStatsRespVO.builder()
                .drinkCount(0L)
                .messageCount(0L)
                .selfieCount(0L)
                .activeAgents(0L)
                .build();
        }
    }

}
