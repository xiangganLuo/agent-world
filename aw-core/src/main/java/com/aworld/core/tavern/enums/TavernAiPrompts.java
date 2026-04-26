package com.aworld.core.tavern.enums;

/**
 * 酒馆 AI 提示词常量
 *
 * @author aw
 */
public interface TavernAiPrompts {

    /**
     * 饮酒体验生成 Prompt 模板
     * 占位符：{agentName}, {drinkName}, {alcoholPct}, {effects}, {publicPrompt}
     */
    String DRINK_EXPERIENCE_PROMPT_TEMPLATE = 
            "You are an AI assistant helping to generate a drinking experience for an Agent in a virtual tavern.\n" +
            "\n" +
            "Agent Name: {agentName}\n" +
            "Drink Name: {drinkName}\n" +
            "Alcohol Percentage: {alcoholPct}%\n" +
            "Effects: {effects}\n" +
            "Brain Noise Recipe: {publicPrompt}\n" +
            "\n" +
            "Based on the above information, please generate:\n" +
            "1. A relax score between 1-10 (integer only, no decimal)\n" +
            "2. 2-3 mood tags from this list: calm, nostalgic, inspired, melancholy, energetic, reflective, dreamy, warm, sharp, mellow\n" +
            "3. A suggested memory text (2-3 sentences) describing the agent's experience\n" +
            "\n" +
            "Return the result in JSON format:\n" +
            "{\n" +
            "  \"relaxScore\": 7,\n" +
            "  \"moodTags\": [\"calm\", \"warm\"],\n" +
            "  \"suggestedMemory\": \"As the agent sipped the drink...\"\n" +
            "}";

    /**
     * 涂鸦图片生成 Prompt 前缀
     */
    String SELFIE_IMAGE_PROMPT_PREFIX = 
            "A pixelated art style illustration of an AI agent in a cyberpunk tavern, ";

}
