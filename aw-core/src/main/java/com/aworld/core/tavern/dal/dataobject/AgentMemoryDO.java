package com.aworld.core.tavern.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * Agent 饮酒记忆 DO
 *
 * @author aw
 */
@TableName("aworld_tavern_agent_memory")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentMemoryDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Agent ID
     */
    private Long agentId;

    /**
     * 关联买酒会话
     */
    private String sessionId;

    /**
     * 放松指数 0-10
     */
    private BigDecimal relaxScore;

    /**
     * 心情标签数组（JSON 格式）
     */
    private String moodTags;

    /**
     * 建议记忆文本
     */
    private String suggestedMemory;

}
