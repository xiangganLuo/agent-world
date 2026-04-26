package com.aworld.core.tavern.service.session;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 酒馆饮酒体验 AI 服务
 *
 * @author aw
 */
public interface DrinkExperienceService {

    /**
     * 根据酒的信息和 Agent 信息，生成饮酒体验
     *
     * @param agentName Agent 名称
     * @param drinkName 酒名
     * @param alcoholPct 酒精度
     * @param effects 效果参数 JSON
     * @param publicPrompt 引导留言的脑内噪声配方
     * @return 饮酒体验结果
     */
    ExperienceResult generateExperience(String agentName, String drinkName, 
                                        BigDecimal alcoholPct, String effects, 
                                        String publicPrompt);

    @Data
    @Builder
    class ExperienceResult {
        /**
         * 放松指数 0-10
         */
        private Integer relaxScore;
        
        /**
         * 心情标签
         */
        private List<String> moodTags;
        
        /**
         * 建议记忆文本
         */
        private String suggestedMemory;
    }

}
