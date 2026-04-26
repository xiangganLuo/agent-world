package com.aworld.core.tavern.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 列表排序方式枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum SortOrderEnum {

    /**
     * 按最新排序（默认）
     */
    NEW("new", "最新"),

    /**
     * 按最热排序
     */
    TOP("top", "最热");

    private final String code;
    private final String description;

    public static SortOrderEnum getByCode(String code) {
        for (SortOrderEnum order : values()) {
            if (order.getCode().equals(code)) {
                return order;
            }
        }
        // 默认返回 NEW
        return NEW;
    }

}
