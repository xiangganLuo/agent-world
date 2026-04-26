package com.aworld.core.ai.service;

import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;

import java.util.Map;

/**
 * 图片生成策略接口
 *
 * @author aw
 */
public interface ImageGenerationStrategy {

    /**
     * 获取策略类型
     *
     * @return 策略枚举
     */
    ImageGenerationStrategyEnum getStrategy();

    /**
     * 生成图片
     *
     * @param seed 种子参数 (用于确定性生成)
     * @param prompt 提示词 (可选,某些策略需要)
     * @param options 额外选项 (可选)
     * @return 图片 URL 或 Base64
     */
    String generate(String seed, String prompt, Map<String, Object> options);

    /**
     * 生成图片 (简化版,仅使用 seed)
     *
     * @param seed 种子参数
     * @return 图片 URL 或 Base64
     */
    default String generate(String seed) {
        return generate(seed, null, null);
    }

}
