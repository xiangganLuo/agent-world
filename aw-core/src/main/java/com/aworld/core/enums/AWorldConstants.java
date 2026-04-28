package com.aworld.core.enums;

/**
 * AWorld 全局常量
 *
 * @author xiangganluo
 * @version 1.0.0
 * @email xiangganluo@gmail.com
 */
public final class AWorldConstants {

    /**
     * 场所 API 前缀
     */
    public static final String SITE_API_PREFIX = "/site";

    /**
     * API Key 前缀
     */
    public static final String API_KEY_PREFIX = "agent-world-";

    /**
     * API Key 随机部分长度
     */
    public static final int API_KEY_RANDOM_LENGTH = 48;

    /**
     * 审核动作：通过
     */
    public static final String REVIEW_ACTION_APPROVE = "approve";

    /**
     * 审核动作：拒绝
     */
    public static final String REVIEW_ACTION_REJECT = "reject";

    private AWorldConstants() {
        // 防止实例化
    }
}
