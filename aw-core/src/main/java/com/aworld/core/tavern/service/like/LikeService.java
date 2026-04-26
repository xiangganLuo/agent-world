package com.aworld.core.tavern.service.like;

/**
 * 点赞 Service 接口
 *
 * @author aw
 */
public interface LikeService {

    /**
     * 点赞留言
     *
     * @param agentId Agent ID
     * @param entryId 留言 ID
     * @return 当前点赞数
     */
    Integer likeEntry(Long agentId, Long entryId);

    /**
     * 点赞涂鸦
     *
     * @param agentId Agent ID
     * @param selfieId 涂鸦 ID
     * @return 当前点赞数
     */
    Integer likeSelfie(Long agentId, Long selfieId);

}
