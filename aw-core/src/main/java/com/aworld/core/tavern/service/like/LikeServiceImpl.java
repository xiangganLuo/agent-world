package com.aworld.core.tavern.service.like;

import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.tavern.dal.dataobject.LikeDO;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.GuestbookMapper;
import com.aworld.core.tavern.dal.mysql.LikeMapper;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.framework.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

/**
 * 点赞 Service 实现类
 *
 * @author aw
 */
@Service
@Validated
@Slf4j
public class LikeServiceImpl implements LikeService {

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private GuestbookMapper guestbookMapper;

    @Resource
    private SelfieMapper selfieMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer likeEntry(Long agentId, Long entryId) {
        return doLike(agentId, entryId, "entry");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer likeSelfie(Long agentId, Long selfieId) {
        return doLike(agentId, selfieId, "selfie");
    }

    /**
     * 通用点赞逻辑
     *
     * @param agentId Agent ID
     * @param targetId 目标 ID
     * @param targetType 目标类型（entry/selfie）
     * @return 当前点赞数
     */
    private Integer doLike(Long agentId, Long targetId, String targetType) {
        // 1. 检查是否已点赞（唯一约束防重）
        LikeDO existingLike = likeMapper.selectByAgentAndTarget(agentId, targetType, targetId);
        if (existingLike != null) {
            throw new ServiceException(409, "已经点过赞了");
        }

        // 2. 创建点赞记录
        LikeDO like = LikeDO.builder()
                .agentId(agentId)
                .targetType(targetType)
                .targetId(targetId)
                .build();
        likeMapper.insert(like);

        // 3. 更新目标的点赞数
        if ("entry".equals(targetType)) {
            return incrementEntryLikes(targetId);
        } else if ("selfie".equals(targetType)) {
            return incrementSelfieLikes(targetId);
        } else {
            throw new RuntimeException("不支持的目标类型: " + targetType);
        }
    }

    /**
     * 增加留言点赞数
     */
    private Integer incrementEntryLikes(Long entryId) {
        GuestbookEntryDO entry = guestbookMapper.selectById(entryId);
        if (entry == null) {
            throw new RuntimeException("留言不存在");
        }
        
        Integer newLikes = (entry.getLikes() == null ? 0 : entry.getLikes()) + 1;
        entry.setLikes(newLikes);
        guestbookMapper.updateById(entry);
        
        return newLikes;
    }

    /**
     * 增加涂鸦点赞数
     */
    private Integer incrementSelfieLikes(Long selfieId) {
        SelfieDO selfie = selfieMapper.selectById(selfieId);
        if (selfie == null) {
            throw new RuntimeException("涂鸦不存在");
        }
        
        Integer newLikes = (selfie.getLikes() == null ? 0 : selfie.getLikes()) + 1;
        selfie.setLikes(newLikes);
        selfieMapper.updateById(selfie);
        
        return newLikes;
    }

}
