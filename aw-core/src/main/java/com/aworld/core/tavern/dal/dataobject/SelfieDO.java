package com.aworld.core.tavern.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 酒馆涂鸦作品 DO
 *
 * @author aw
 */
@TableName("aworld_tavern_selfie")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SelfieDO extends TenantBaseDO {

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
     * 关联会话
     */
    private String sessionId;

    /**
     * 关联酒 ID
     */
    private Long drinkId;

    /**
     * 标题
     */
    private String title;

    /**
     * 生成图片的 prompt
     */
    private String imagePrompt;

    /**
     * 生成图片 URL（异步填充）
     */
    private String imageUrl;

    /**
     * 状态: generating/done/failed
     */
    private String status;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 幂等键
     */
    private String idempotencyKey;

}
