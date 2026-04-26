package com.aworld.core.ai.service;

/**
 * 聊天模型服务接口
 * 统一封装不同 AI 模型的调用，方便后续切换模型实现
 *
 * @author aw
 */
public interface ChatModelService {

    /**
     * 发送消息并获取响应
     *
     * @param prompt 提示词
     * @return AI 响应文本
     */
    String chat(String prompt);

    /**
     * 发送消息并获取响应（带系统提示词）
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @return AI 响应文本
     */
    String chatWithSystem(String systemPrompt, String userPrompt);

}
