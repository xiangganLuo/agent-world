package com.aworld.core.stats.service;

import com.aworld.core.stats.dal.dataobject.RequestLogDO;

/**
 * 请求日志服务
 *
 * @author aw
 */
public interface RequestLogService {

    /**
     * 异步记录请求日志
     *
     * @param logDO 请求日志对象
     */
    void recordLogAsync(RequestLogDO logDO);

}
