package com.aworld.core.tavern.mq.consumer;

import com.aworld.core.ai.service.ImageGeneratorService;
import com.aworld.core.tavern.dal.dataobject.SelfieDO;
import com.aworld.core.tavern.dal.mysql.SelfieMapper;
import com.aworld.core.tavern.enums.SelfieStatusEnum;
import com.aworld.core.tavern.enums.TavernAiPrompts;
import com.aworld.core.tavern.mq.message.ImageGenerateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.aworld.core.ai.enums.ImageGenerationStrategyEnum.TONGYI_WANXIANG;

/**
 * 涂鸦图片生成消费者
 *
 * @author aw
 */
@Component
@Slf4j
public class ImageGenerateConsumer {

    @Resource
    private SelfieMapper selfieMapper;

    @Resource
    private ImageGeneratorService imageGeneratorService;

    @EventListener
    @Async
    public void onMessage(ImageGenerateMessage message) {
        log.info("[onMessage][开始生成涂鸦图片，涂鸦 ID({})]", message.getSelfieId());
        
        try {
            // 1. 查询涂鸦记录
            SelfieDO selfie = selfieMapper.selectById(message.getSelfieId());
            if (selfie == null) {
                log.error("[onMessage][涂鸦不存在，涂鸦 ID({})]", message.getSelfieId());
                return;
            }

            // 2. 构建完整的图片提示词
            String fullPrompt = TavernAiPrompts.SELFIE_IMAGE_PROMPT_PREFIX + message.getImagePrompt();
            
            // 3. 调用 AI 绘图 API 生成图片（使用通义千问策略）
            log.info("[onMessage][调用图片生成服务，涂鸦 ID({}), prompt: {}]", 
                    message.getSelfieId(), fullPrompt);
            String imageUrl = imageGeneratorService.generate(fullPrompt, TONGYI_WANXIANG.getCode());
            
            // 4. 更新涂鸦状态和 URL
            selfie.setImageUrl(imageUrl);
            selfie.setStatus(SelfieStatusEnum.DONE.getCode());
            selfieMapper.updateById(selfie);
            
            log.info("[onMessage][涂鸦图片生成成功，涂鸦 ID({}), URL: {}]", 
                    message.getSelfieId(), imageUrl);
                    
        } catch (Exception e) {
            log.error("[onMessage][涂鸦图片生成失败，涂鸦 ID({})]", message.getSelfieId(), e);
            
            // 更新状态为 failed
            try {
                SelfieDO selfie = selfieMapper.selectById(message.getSelfieId());
                if (selfie != null) {
                    selfie.setStatus(SelfieStatusEnum.FAILED.getCode());
                    selfieMapper.updateById(selfie);
                }
            } catch (Exception ex) {
                log.error("[onMessage][更新涂鸦失败状态异常，涂鸦 ID({})]", message.getSelfieId(), ex);
            }
        }
    }

}
