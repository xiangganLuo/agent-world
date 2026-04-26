package com.aworld.core.ai.service.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;
import com.aworld.core.ai.service.ImageGenerationStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 通义千问文生图策略 (Tongyi Wanxiang)
 * 
 * 基于阿里云 DashScope API 实现文生图功能
 * API 文档: https://help.aliyun.com/zh/dashscope/developer-reference/api-details-9
 *
 * @author aw
 */
@Component
@Slf4j
public class TongyiWanxiangStrategy implements ImageGenerationStrategy {

    @Value("${ai.qwen.api-key:}")
    private String apiKey;

    @Value("${ai.qwen.image.endpoint:https://dashscope.aliyuncs.com/api/v1/services/aigc/text2image/image-synthesis}")
    private String apiEndpoint;

    @Value("${ai.qwen.image.model:wanx-v1}")
    private String model;

    @Override
    public ImageGenerationStrategyEnum getStrategy() {
        return ImageGenerationStrategyEnum.TONGYI_WANXIANG;
    }

    @Override
    public String generate(String seed, String prompt, Map<String, Object> options) {
        try {
            // 构建请求体
            JSONObject requestBody = buildRequestBody(prompt);

            // 调用通义千问文生图 API
            HttpResponse response = HttpRequest.post(apiEndpoint)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .header("X-DashScope-Async", "enable") // 异步模式
                    .body(requestBody.toString())
                    .timeout(30000) // 30秒超时
                    .execute();

            if (!response.isOk()) {
                throw new RuntimeException("通义千问文生图 API 调用失败: " + response.getStatus());
            }

            // 解析响应，获取任务 ID
            JSONObject responseBody = JSONUtil.parseObj(response.body());
            String taskId = parseTaskId(responseBody);

            log.info("[generate][通义千问文生图任务提交成功，taskId: {}]", taskId);

            // 轮询查询任务状态
            return pollTaskStatus(taskId);

        } catch (Exception e) {
            log.error("[generate][通义千问文生图调用失败]", e);
            throw new RuntimeException("图片生成失败", e);
        }
    }

    /**
     * 构建请求体
     */
    private JSONObject buildRequestBody(String prompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", model);

        JSONObject input = new JSONObject();
        input.set("prompt", prompt);
        requestBody.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("size", "1024*1024"); // 默认尺寸
        parameters.set("n", 1); // 生成1张图片
        requestBody.set("parameters", parameters);

        return requestBody;
    }

    /**
     * 解析任务 ID
     */
    private String parseTaskId(JSONObject responseBody) {
        JSONObject output = responseBody.getJSONObject("output");
        if (output == null) {
            throw new RuntimeException("通义千问 API 返回格式异常");
        }
        return output.getStr("task_id");
    }

    /**
     * 轮询查询任务状态
     */
    private String pollTaskStatus(String taskId) {
        int maxRetries = 30; // 最多轮询30次
        int retryInterval = 2000; // 每次间隔2秒

        for (int i = 0; i < maxRetries; i++) {
            try {
                // 等待一段时间
                Thread.sleep(retryInterval);

                // 查询任务状态
                String statusUrl = apiEndpoint.replace("/image-synthesis", "/task-status") + "?task_id=" + taskId;
                
                HttpResponse response = HttpRequest.get(statusUrl)
                        .header("Authorization", "Bearer " + apiKey)
                        .timeout(10000)
                        .execute();

                if (!response.isOk()) {
                    log.warn("[pollTaskStatus][查询任务状态失败，taskId: {}]", taskId);
                    continue;
                }

                JSONObject responseBody = JSONUtil.parseObj(response.body());
                String status = parseTaskStatus(responseBody);

                log.debug("[pollTaskStatus][任务状态: {}, taskId: {}, 第{}次轮询]", status, taskId, i + 1);

                // 任务完成
                if ("SUCCEEDED".equals(status)) {
                    return parseImageUrl(responseBody);
                }

                // 任务失败
                if ("FAILED".equals(status)) {
                    String errorMsg = parseErrorMessage(responseBody);
                    throw new RuntimeException("通义千问文生图任务失败: " + errorMsg);
                }

                // 任务进行中或等待中，继续轮询
                if ("RUNNING".equals(status) || "PENDING".equals(status)) {
                    continue;
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("轮询被中断", e);
            }
        }

        throw new RuntimeException("通义千问文生图任务超时，taskId: " + taskId);
    }

    /**
     * 解析任务状态
     */
    private String parseTaskStatus(JSONObject responseBody) {
        JSONObject output = responseBody.getJSONObject("output");
        if (output == null) {
            return "UNKNOWN";
        }
        return output.getStr("task_status");
    }

    /**
     * 解析图片 URL
     */
    private String parseImageUrl(JSONObject responseBody) {
        JSONObject output = responseBody.getJSONObject("output");
        if (output == null) {
            throw new RuntimeException("通义千问 API 返回格式异常");
        }
        
        // 通义千问返回的图片结果在 results 数组中
        Object results = output.get("results");
        if (results instanceof cn.hutool.json.JSONArray) {
            cn.hutool.json.JSONArray resultsArray = (cn.hutool.json.JSONArray) results;
            if (!resultsArray.isEmpty()) {
                JSONObject firstResult = resultsArray.getJSONObject(0);
                return firstResult.getStr("url");
            }
        }
        
        throw new RuntimeException("未找到生成的图片 URL");
    }

    /**
     * 解析错误消息
     */
    private String parseErrorMessage(JSONObject responseBody) {
        JSONObject output = responseBody.getJSONObject("output");
        if (output != null) {
            return output.getStr("message", "未知错误");
        }
        return "未知错误";
    }

}
