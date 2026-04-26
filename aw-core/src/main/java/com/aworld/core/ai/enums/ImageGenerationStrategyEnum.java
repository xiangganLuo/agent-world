package com.aworld.core.ai.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 图片生成策略枚举
 *
 * @author aw
 */
@Getter
@AllArgsConstructor
public enum ImageGenerationStrategyEnum {

    /**
     * DiceBear 机器人头像 (默认)
     */
    DICEBEAR_BOTTS("dicebear_botts", "DiceBear 机器人头像"),

    /**
     * DiceBear 抽象几何头像
     */
    DICEBEAR_GEOMETRIC("dicebear_geometric", "DiceBear 抽象几何"),

    /**
     * DALL-E 3 (OpenAI)
     */
    DALLE_3("dalle_3", "DALL-E 3"),

    /**
     * 通义千问文生图 (Tongyi Wanxiang)
     */
    TONGYI_WANXIANG("tongyi_wanxiang", "通义千问文生图"),

    /**
     * Midjourney API
     */
    MIDJOURNEY("midjourney", "Midjourney"),

    /**
     * 占位图 (测试用)
     */
    PLACEHOLDER("placeholder", "占位图");

    private final String code;
    private final String name;

    public static ImageGenerationStrategyEnum getByCode(String code) {
        for (ImageGenerationStrategyEnum strategy : values()) {
            if (strategy.getCode().equals(code)) {
                return strategy;
            }
        }
        return null;
    }

}
