package com.aworld.core.ai.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 通义千问聊天模型服务实现
 *
 * @author aw
 */
@Service
@Slf4j
public class TongyiChatModelServiceImpl implements ChatModelService {

    @Value("${ai.qwen.api-key:}")
    private String apiKey;

    @Value("${ai.qwen.endpoint:https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation}")
    private String apiEndpoint;

    @Value("${ai.qwen.model:qwen-turbo}")
    private String model;

    @Override
    public String chat(String prompt) {
        return chatWithSystem(null, prompt);
    }

    @Override
    public String chatWithSystem(String systemPrompt, String userPrompt) {
        try {
            JSONObject requestBody = buildRequestBody(systemPrompt, userPrompt);

            HttpResponse response = HttpRequest.post(apiEndpoint)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(requestBody.toString())
                    .timeout(10000)
                    .execute();

            if (!response.isOk()) {
                throw new RuntimeException("通义千问 API 调用失败: " + response.getStatus());
            }

            return parseResponse(response.body());

        } catch (Exception e) {
            log.error("[chatWithSystem][通义千问 API 调用失败]", e);
            throw new RuntimeException("AI 服务调用失败", e);
        }
    }

    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(String systemPrompt, String userPrompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);

        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();

        // 添加系统提示词（如果有）
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            JSONObject systemMessage = new JSONObject();
            systemMessage.set("role", "system");
            systemMessage.set("content", systemPrompt);
            messages.add(systemMessage);
        }

        // 添加用户提示词
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", userPrompt);
        messages.add(userMessage);

        input.set("messages", messages);
        requestBody.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);

        return requestBody;
    }

    /**
     * 解析响应
     */
    private String parseResponse(String responseBody) {
        JSONObject jsonObject = JSONUtil.parseObj(responseBody);
        JSONObject output = jsonObject.getJSONObject("output");
        JSONArray choices = output.getJSONArray("choices");

        if (choices == null || choices.isEmpty()) {
            throw new RuntimeException("通义千问 API 返回空结果");
        }

        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject msg = firstChoice.getJSONObject("message");
        return msg.getStr("content");
    }

}
