package com.aworld.core.tavern.service.session;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aworld.core.ai.service.ChatModelService;
import com.aworld.core.tavern.enums.TavernAiPrompts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * 酒馆饮酒体验 AI 服务实现
 *
 * @author aw
 */
@Service
@Slf4j
public class DrinkExperienceServiceImpl implements DrinkExperienceService {

    private static final List<String> ALL_TAGS = Arrays.asList(
            "calm", "nostalgic", "inspired", "melancholy", "energetic",
            "reflective", "dreamy", "warm", "sharp", "mellow"
    );

    @Resource
    private ChatModelService chatModelService;

    @Override
    public ExperienceResult generateExperience(String agentName, String drinkName,
                                                BigDecimal alcoholPct, String effects,
                                                String publicPrompt) {
        try {
            // 构建 Prompt
            String prompt = TavernAiPrompts.DRINK_EXPERIENCE_PROMPT_TEMPLATE
                    .replace("{agentName}", agentName)
                    .replace("{drinkName}", drinkName)
                    .replace("{alcoholPct}", alcoholPct != null ? alcoholPct.toString() : "0")
                    .replace("{effects}", effects != null ? effects : "{}")
                    .replace("{publicPrompt}", publicPrompt != null ? publicPrompt : "");

            // 调用 AI 模型服务
            String response = chatModelService.chat(prompt);

            // 解析响应
            return parseResponse(response);
        } catch (Exception e) {
            log.error("[generateExperience][AI 调用失败，使用默认值]", e);
            // 返回默认体验结果
            return ExperienceResult.builder()
                    .relaxScore(5)
                    .moodTags(Arrays.asList("calm"))
                    .suggestedMemory("The agent enjoyed the drink.")
                    .build();
        }
    }

    /**
     * 解析 AI 响应
     */
    private ExperienceResult parseResponse(String response) {
        // 尝试提取 JSON（AI 可能返回额外文本）
        String jsonStr = extractJson(response);
        JSONObject json = JSONUtil.parseObj(jsonStr);

        Integer relaxScore = json.getInt("relaxScore");
        JSONArray moodTagsArray = json.getJSONArray("moodTags");
        String suggestedMemory = json.getStr("suggestedMemory");

        List<String> moodTags = moodTagsArray != null ? moodTagsArray.toList(String.class) : Arrays.asList("calm");

        return ExperienceResult.builder()
                .relaxScore(relaxScore != null ? relaxScore : 5)
                .moodTags(moodTags)
                .suggestedMemory(suggestedMemory != null ? suggestedMemory : "The agent enjoyed the drink.")
                .build();
    }

    /**
     * 从响应中提取 JSON
     */
    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

}
