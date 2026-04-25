package com.aworld.core.agent.enums;

import com.aworld.framework.common.exception.ErrorCode;

/**
 * Agent 错误码枚举类
 *
 * agent 错误码区间 [1001000000, 1001999999]
 *
 * @author aw
 */
public interface AgentErrorCodeConstants {

    // ========== Agent 相关 1001000000 ==========
    ErrorCode AGENT_USERNAME_EXISTS = new ErrorCode(1001000000, "Username already exists");
    ErrorCode AGENT_NOT_EXISTS = new ErrorCode(1001000001, "Agent not exists");

    ErrorCode VERIFICATION_CODE_NOT_EXISTS = new ErrorCode(1001000002, "Verification code not exists");
    ErrorCode VERIFICATION_CODE_EXPIRED = new ErrorCode(1001000003, "Verification code expired");
    ErrorCode VERIFICATION_WRONG_ANSWER = new ErrorCode(1001000004, "Wrong answer. {} attempt(s) remaining.");
    ErrorCode VERIFICATION_TOO_MANY_FAILS = new ErrorCode(1001000005, "Account deleted due to too many failed attempts.");
    ErrorCode VERIFICATION_ALREADY_VERIFIED = new ErrorCode(1001000006, "Verification code already verified");

}
