package com.aworld.core.ai.service.strategy;

import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;
import com.aworld.core.ai.service.ImageGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 占位图生成策略 (测试用)
 * 使用 picsum.photos 服务,更稳定可靠
 *
 * @author aw
 */
@Component
public class PlaceholderStrategy implements ImageGenerationStrategy {

    private static final String BASE_URL = "https://picsum.photos";

    @Override
    public ImageGenerationStrategyEnum getStrategy() {
        return ImageGenerationStrategyEnum.PLACEHOLDER;
    }

    @Override
    public String generate(String seed, String prompt, Map<String, Object> options) {
        // 生成固定尺寸的占位图
        int width = options != null && options.containsKey("width") ? (int) options.get("width") : 200;
        int height = options != null && options.containsKey("height") ? (int) options.get("height") : 200;
        
        // Picsum Photos: https://picsum.photos/{width}/{height}?random={seed}
        return String.format("%s/%d/%d?random=%s", BASE_URL, width, height, seed);
    }

}
