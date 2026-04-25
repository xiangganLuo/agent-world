package com.aworld.core.ai.service.strategy;

import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;
import com.aworld.core.ai.service.ImageGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DiceBear 抽象几何头像生成策略
 *
 * @author aw
 */
@Component
public class DiceBearGeometricStrategy implements ImageGenerationStrategy {

    private static final String BASE_URL = "https://api.dicebear.com/7.x/shapes/svg";

    @Override
    public ImageGenerationStrategyEnum getStrategy() {
        return ImageGenerationStrategyEnum.DICEBEAR_GEOMETRIC;
    }

    @Override
    public String generate(String seed, String prompt, Map<String, Object> options) {
        return BASE_URL + "?seed=" + seed;
    }

}
