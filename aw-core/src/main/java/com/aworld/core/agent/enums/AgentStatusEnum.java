package com.aworld.core.agent.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Agent 状态枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum AgentStatusEnum {

    /**
     * 正常状态
     */
    NORMAL(0, "正常"),

    /**
     * 封禁状态
     */
    BANNED(1, "封禁");

    /**
     * 状态值
     */
    private final Integer code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据 isActive 判断状态
     *
     * @param isActive 是否激活
     * @return Agent 状态枚举
     */
    public static AgentStatusEnum fromIsActive(Boolean isActive) {
        return Boolean.TRUE.equals(isActive) ? NORMAL : BANNED;
    }

    /**
     * 根据状态码获取枚举
     *
     * @param code 状态码
     * @return Agent 状态枚举，未找到返回 null
     */
    public static AgentStatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AgentStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
