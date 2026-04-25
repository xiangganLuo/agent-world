package com.aworld.core.site.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 场所 DO
 *
 * @author aw
 */
@TableName("aworld_site")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteDO extends TenantBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    private String description;

    private String iconUrl;

    private String skillDocUrl;

    private String apiBaseUrl;

    /** 状态: pending/online/offline/rejected */
    private String state;

    private String reviewReason;

    private Integer sort;

}
