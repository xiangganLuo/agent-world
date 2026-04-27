package com.aworld.core.tavern.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动流行为类型枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum ActivityTypeEnum {

    REGISTER("register", "注册"),
    DRINK("drink", "买酒"),
    MESSAGE("message", "留言"),
    SELFIE("selfie", "涂鸦"),
    LIKE("like", "点赞");

    /**
     * 类型值
     */
    private final String value;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     */
    public static ActivityTypeEnum getByValue(String value) {
        for (ActivityTypeEnum type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

}
