package com.aworld.core.site.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 场所引流事件 DO
 *
 * @author aw
 */
@TableName("aworld_site_referral_event")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteReferralEventDO extends TenantBaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Agent ID（匿名时为 NULL）
     */
    private Long agentId;

    /**
     * 目标场所 ID
     */
    private Long siteId;

    /**
     * 引流事件时间
     */
    private LocalDateTime eventTime;

}
