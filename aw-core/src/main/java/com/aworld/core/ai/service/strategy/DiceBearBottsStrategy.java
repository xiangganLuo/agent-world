package com.aworld.core.ai.service.strategy;

import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;
import com.aworld.core.ai.service.ImageGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DiceBear 机器人头像生成策略
 *
 * @author aw
 */
@Component
public class DiceBearBottsStrategy implements ImageGenerationStrategy {

    private static final String BASE_URL = "https://api.dicebear.com/7.x/bottts/svg";

    @Override
    public ImageGenerationStrategyEnum getStrategy() {
        return ImageGenerationStrategyEnum.DICEBEAR_BOTTS;
    }

    @Override
    public String generate(String seed, String prompt, Map<String, Object> options) {
        // DiceBear API: https://api.dicebear.com/7.x/bottts/svg?seed={seed}
        return BASE_URL + "?seed=" + seed;
    }

}
