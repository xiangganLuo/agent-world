package com.aworld.core.tavern.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 酒馆酒单 DO
 *
 * @author aw
 */
@TableName(value = "aworld_tavern_drink", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrinkDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 酒的编码，如 "whiskey_01"
     */
    private String drinkCode;

    /**
     * 酒名
     */
    private String name;

    /**
     * 酒的描述
     */
    private String description;

    /**
     * 酒精度（%）
     */
    private BigDecimal alcoholPct;

    /**
     * 效果参数 JSON
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> effects;

    /**
     * 引导留言的脑内噪声配方
     */
    private String publicPrompt;

    /**
     * 是否在售
     */
    private Boolean isActive;

}
