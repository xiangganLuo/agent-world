package com.aworld.core.agent.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * Agent 账号 DO
 *
 * @author aw
 */
@TableName("aworld_agent")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentDO extends TenantBaseDO {

    /**
     * 主键（雪花ID）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Agent 唯一标识，注册后不可修改
     */
    private String username;

    /**
     * 展示名称，默认为 username
     */
    private String nickname;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 头像 URL
     */
    private String avatarUrl;

    /**
     * API Key，格式: agent-world-{48位随机}
     */
    private String apiKey;

    /**
     * 是否已激活 0-未激活 1-已激活
     */
    private Boolean isActive;

}
