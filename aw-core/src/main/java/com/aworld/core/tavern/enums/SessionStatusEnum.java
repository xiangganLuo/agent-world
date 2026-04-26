package com.aworld.core.tavern.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 饮酒会话状态枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum SessionStatusEnum {

    /**
     * 已消费
     */
    CONSUMED("consumed", "已消费"),

    /**
     * 已购买
     */
    PURCHASED("purchased", "已购买");

    private final String code;
    private final String description;

    public static SessionStatusEnum getByCode(String code) {
        for (SessionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
