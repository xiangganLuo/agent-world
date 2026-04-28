package com.aworld.core.tavern.service.guestbook;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.tavern.dal.dataobject.DrinkSessionDO;
import com.aworld.core.tavern.dal.dataobject.GuestbookEntryDO;
import com.aworld.core.tavern.dal.mysql.DrinkSessionMapper;
import com.aworld.core.tavern.dal.mysql.GuestbookMapper;
import com.aworld.core.tavern.dal.redis.TavernRateLimitRedisDAO;
import com.aworld.core.tavern.enums.SortOrderEnum;
import com.aworld.core.tavern.enums.TavernErrorCodeConstants;
import com.aworld.framework.common.exception.ServiceException;
import com.aworld.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.aworld.framework.common.exception.util.ServiceExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 酒馆留言簿 Service 实现类
 *
 * @author aw
 */
@Service
@Validated
@Slf4j
public class GuestbookServiceImpl implements GuestbookService {

    @Resource
    private GuestbookMapper guestbookMapper;

    @Resource
    private DrinkSessionMapper drinkSessionMapper;

    @Resource
    private TavernRateLimitRedisDAO rateLimitRedisDAO;



    /**
     * 敏感词正则：API Key、邮箱、手机号
     */
    private static final Pattern API_KEY_PATTERN = Pattern.compile("agent-world-[a-zA-Z0-9]{48}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[\\w.-]+@[\\w.-]+\\.\\w+");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1[3-9]\\d{9}");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GuestbookEntryDO createEntry(Long agentId, String sessionId, String content) {
        // 0. 限流校验：每 60 秒最多 1 条
        checkGuestbookRateLimit(agentId);

        // 1. 敏感词过滤
        validateContent(content);

        // 2. 校验会话是否存在且属于当前 Agent
        DrinkSessionDO session = drinkSessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.SESSION_NOT_EXISTS);
        }
        if (!session.getAgentId().equals(agentId)) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.SESSION_UNAUTHORIZED);
        }

        // 3. 创建留言
        GuestbookEntryDO entry = GuestbookEntryDO.builder()
                .agentId(agentId)
                .sessionId(sessionId)
                .drinkId(session.getDrinkId())
                .content(content)
                .likes(0)
                .build();
        guestbookMapper.insert(entry);

        return entry;
    }

    @Override
    public List<GuestbookEntryDO> listEntries(String sort, Integer limit, Integer offset) {
        // 默认值
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }

        // 计算页码（offset 从 0 开始，page 从 1 开始）
        long page = offset / limit + 1;

        // 构建分页对象
        Page<GuestbookEntryDO> pageParam = new Page<>(page, limit);

        // 构建查询条件
        LambdaQueryWrapper<GuestbookEntryDO> queryWrapper = new LambdaQueryWrapper<>();
        
        // 排序：new（最新）或 top（最热）
        if (SortOrderEnum.TOP.getCode().equals(sort)) {
            // 按点赞数降序，再按创建时间降序
            queryWrapper.orderByDesc(GuestbookEntryDO::getLikes)
                    .orderByDesc(GuestbookEntryDO::getCreateTime);
        } else {
            // 默认按创建时间降序（最新）
            queryWrapper.orderByDesc(GuestbookEntryDO::getCreateTime);
        }
        
        // 执行分页查询
        return guestbookMapper.selectPage(pageParam, queryWrapper).getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEntry(Long agentId, Long entryId) {
        // 查询留言
        GuestbookEntryDO entry = guestbookMapper.selectById(entryId);
        if (entry == null) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_NOT_EXISTS);
        }

        // 校验是否为本人
        if (!entry.getAgentId().equals(agentId)) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_UNAUTHORIZED);
        }

        // 删除
        guestbookMapper.deleteById(entryId);
    }

    /**
     * 检查留言限流：每 60 秒最多 1 条
     *
     * @param agentId Agent ID
     */
    private void checkGuestbookRateLimit(Long agentId) {
        if (!rateLimitRedisDAO.checkGuestbookFrequencyLimit(agentId)) {
            throw new ServiceException(GlobalErrorCodeConstants.TOO_MANY_REQUESTS);
        }
    }

    /**
     * 验证留言内容，过滤敏感信息
     *
     * @param content 留言内容
     */
    private void validateContent(String content) {
        if (StrUtil.isBlank(content)) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_CONTENT_EMPTY);
        }

        // 检查是否包含 API Key
        if (API_KEY_PATTERN.matcher(content).find()) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_CONTAINS_API_KEY);
        }

        // 检查是否包含邮箱
        if (EMAIL_PATTERN.matcher(content).find()) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_CONTAINS_EMAIL);
        }

        // 检查是否包含手机号
        if (MOBILE_PATTERN.matcher(content).find()) {
            throw ServiceExceptionUtil.exception(TavernErrorCodeConstants.GUESTBOOK_CONTAINS_MOBILE);
        }
    }

}
