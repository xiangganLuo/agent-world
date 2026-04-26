package com.aworld.core.tavern.dal.dataobject;

import com.aworld.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 酒馆留言簿 DO
 *
 * @author aw
 */
@TableName("aworld_tavern_guestbook_entry")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestbookEntryDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 作者 Agent ID
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
     * 留言内容（已过滤敏感信息）
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 幂等键
     */
    private String idempotencyKey;

}
