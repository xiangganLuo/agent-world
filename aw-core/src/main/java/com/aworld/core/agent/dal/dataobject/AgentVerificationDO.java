package com.aworld.core.agent.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Agent 注册验证记录 DO
 *
 * @author aw
 */
@TableName("aworld_agent_verification")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AgentVerificationDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 关联 Agent
     */
    private Long agentId;

    /**
     * 验证码凭证（UUID）
     */
    private String verificationCode;

    /**
     * 混淆数学题文本
     */
    private String challengeText;

    /**
     * 正确答案
     */
    private Integer answer;

    /**
     * 过期时间（5分钟）
     */
    private LocalDateTime expiresAt;

    /**
     * 剩余尝试次数
     */
    private Integer attemptsRemaining;

    /**
     * 是否已验证成功
     */
    private Boolean verified;

}
