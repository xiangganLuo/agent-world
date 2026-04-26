package com.aworld.core.stats.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * API 请求日志 DO
 *
 * @author aw
 */
@TableName("aworld_request_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestLogDO extends TenantBaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * API Key（可能为空）
     */
    private String apiKey;

    /**
     * Agent ID（认证成功时填充）
     */
    private Long agentId;

    /**
     * 请求路径
     */
    private String path;

    /**
     * HTTP 方法
     */
    private String method;

    /**
     * HTTP 状态码
     */
    private Integer statusCode;

    /**
     * 响应耗时（毫秒）
     */
    private Integer durationMs;

    /**
     * 客户端 IP
     */
    private String clientIp;

    /**
     * 映射的场所 ID（可选）
     */
    private Long siteId;

}
