package com.aworld.core.stats.service;

import com.aworld.core.stats.dal.dataobject.RequestLogDO;
import com.aworld.core.stats.dal.mysql.RequestLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 请求日志服务实现
 *
 * @author aw
 */
@Service
@Slf4j
public class RequestLogServiceImpl implements RequestLogService {

    @Resource
    private RequestLogMapper requestLogMapper;

    @Override
    @Async
    public void recordLogAsync(RequestLogDO logDO) {
        try {
            requestLogMapper.insert(logDO);
        } catch (Exception e) {
            log.error("[recordLogAsync] 记录请求日志失败", e);
        }
    }

}
