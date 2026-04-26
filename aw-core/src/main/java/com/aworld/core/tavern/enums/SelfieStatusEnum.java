package com.aworld.core.tavern.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 涂鸦状态枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum SelfieStatusEnum {

    /**
     * 生成中
     */
    GENERATING("generating", "生成中"),

    /**
     * 已完成
     */
    DONE("done", "已完成"),

    /**
     * 失败
     */
    FAILED("failed", "失败");

    private final String code;
    private final String description;

    public static SelfieStatusEnum getByCode(String code) {
        for (SelfieStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

}
