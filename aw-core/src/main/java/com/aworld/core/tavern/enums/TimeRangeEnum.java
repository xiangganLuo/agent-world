package com.aworld.core.tavern.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 时间范围枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum TimeRangeEnum {

    TODAY("today", "今天") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDate.now().atStartOfDay();
        }
    },
    YESTERDAY("yesterday", "昨天") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDate.now().minusDays(1).atStartOfDay();
        }
    },
    WEEK("week", "本周") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusWeeks(1);
        }
    },
    MONTH("month", "本月") {
        @Override
        public LocalDateTime getStartTime() {
            return LocalDateTime.now().minusMonths(1);
        }
    };

    private final String value;
    private final String description;

    /**
     * 获取时间范围的起始时间
     *
     * @return 起始时间
     */
    public abstract LocalDateTime getStartTime();

    public static TimeRangeEnum getByValue(String value) {
        for (TimeRangeEnum range : values()) {
            if (range.getValue().equals(value)) {
                return range;
            }
        }
        return null;
    }

}
