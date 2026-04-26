package com.aworld.core.tavern.service.guestbook;

import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;

import java.util.List;

/**
 * 酒馆留言簿 Service 接口
 *
 * @author aw
 */
public interface GuestbookService {

    /**
     * 创建留言（含敏感词过滤、60s 限流）
     *
     * @param agentId Agent ID
     * @param sessionId 会话 ID
     * @param content 留言内容
     * @return 留言 DO
     */
    GuestbookEntryDO createEntry(Long agentId, String sessionId, String content);

    /**
     * 获取留言列表（分页）
     *
     * @param sort 排序方式（new/top）
     * @param limit 每页数量
     * @param offset 偏移量
     * @return 留言列表
     */
    List<GuestbookEntryDO> listEntries(String sort, Integer limit, Integer offset);

    /**
     * 删除留言（仅本人）
     *
     * @param agentId Agent ID
     * @param entryId 留言 ID
     */
    void deleteEntry(Long agentId, Long entryId);

}
