package com.aworld.core.tavern.enums;

import com.aworld.framework.common.exception.ErrorCode;

/**
 * 酒馆错误码枚举类
 *
 * tavern 错误码区间 [1002000000, 1002999999]
 *
 * @author aw
 */
public interface TavernErrorCodeConstants {

    // ========== 会话相关 1002000000 ==========
    ErrorCode SESSION_NOT_EXISTS = new ErrorCode(1002000000, "会话不存在");
    ErrorCode SESSION_UNAUTHORIZED = new ErrorCode(1002000001, "无权操作该会话");
    ErrorCode SESSION_ALREADY_CONSUMED = new ErrorCode(1002000002, "该酒已被消费");

    // ========== 留言相关 1002000010 ==========
    ErrorCode GUESTBOOK_NOT_EXISTS = new ErrorCode(1002000010, "留言不存在");
    ErrorCode GUESTBOOK_UNAUTHORIZED = new ErrorCode(1002000011, "无权删除他人的留言");
    ErrorCode GUESTBOOK_CONTENT_EMPTY = new ErrorCode(1002000012, "留言内容不能为空");
    ErrorCode GUESTBOOK_CONTAINS_API_KEY = new ErrorCode(1002000013, "留言内容不能包含 API Key");
    ErrorCode GUESTBOOK_CONTAINS_EMAIL = new ErrorCode(1002000014, "留言内容不能包含邮箱地址");
    ErrorCode GUESTBOOK_CONTAINS_MOBILE = new ErrorCode(1002000015, "留言内容不能包含手机号码");

    // ========== 涂鸦相关 1002000020 ==========
    ErrorCode SELFIE_NOT_EXISTS = new ErrorCode(1002000020, "涂鸦不存在");
    ErrorCode SELFIE_UNAUTHORIZED = new ErrorCode(1002000021, "无权删除他人的涂鸦");

    // ========== 买酒相关 1002000030 ==========
    ErrorCode DRINK_NOT_AVAILABLE = new ErrorCode(1002000030, "目前没有在售的酒");
    ErrorCode DRINK_NOT_FOUND = new ErrorCode(1002000031, "酒款不存在或已下架");

}
