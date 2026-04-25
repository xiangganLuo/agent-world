package com.aworld.core.ai.service;

import cn.hutool.core.util.StrUtil;
import com.aworld.core.ai.enums.ImageGenerationStrategyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片生成服务 (策略模式)
 * 
 * 支持多种图片生成策略,通过策略枚举动态选择
 *
 * @author aw
 */
@Service
@Slf4j
public class ImageGeneratorService {

    /**
     * 策略注册表: strategyCode -> ImageGenerationStrategy
     */
    private final Map<String, ImageGenerationStrategy> strategyRegistry = new ConcurrentHashMap<>();

    /**
     * 默认策略
     */
    private ImageGenerationStrategyEnum defaultStrategy = ImageGenerationStrategyEnum.DICEBEAR_BOTTS;

    @Resource
    private List<ImageGenerationStrategy> strategies;

    /**
     * 初始化: 注册所有策略
     */
    @PostConstruct
    public void init() {
        for (ImageGenerationStrategy strategy : strategies) {
            String code = strategy.getStrategy().getCode();
            strategyRegistry.put(code, strategy);
            log.info("[ImageGeneratorService][注册图片生成策略: {}]", code);
        }
    }

    /**
     * 生成图片 (使用默认策略)
     *
     * @param seed 种子参数
     * @return 图片 URL
     */
    public String generate(String seed) {
        return generate(seed, null, null);
    }

    /**
     * 生成图片 (指定策略)
     *
     * @param seed 种子参数
     * @param strategyCode 策略编码 (为空则使用默认策略)
     * @return 图片 URL
     */
    public String generate(String seed, String strategyCode) {
        return generate(seed, strategyCode, null);
    }

    /**
     * 生成图片 (完整参数)
     *
     * @param seed 种子参数
     * @param strategyCode 策略编码 (为空则使用默认策略)
     * @param options 额外选项
     * @return 图片 URL
     */
    public String generate(String seed, String strategyCode, Map<String, Object> options) {
        // 1. 确定使用的策略
        ImageGenerationStrategy strategy = getStrategy(strategyCode);
        
        // 2. 执行生成
        log.debug("[generate][使用策略: {}, seed: {}]", strategy.getStrategy().getCode(), seed);
        String imageUrl = strategy.generate(seed, null, options);
        
        log.info("[generate][图片生成成功, strategy: {}, url: {}]", 
                strategy.getStrategy().getCode(), imageUrl);
        
        return imageUrl;
    }

    /**
     * 获取策略实例
     *
     * @param strategyCode 策略编码 (为空则使用默认策略)
     * @return 策略实例
     */
    private ImageGenerationStrategy getStrategy(String strategyCode) {
        if (StrUtil.isEmpty(strategyCode)) {
            return strategyRegistry.get(defaultStrategy.getCode());
        }
        
        ImageGenerationStrategy strategy = strategyRegistry.get(strategyCode);
        if (strategy == null) {
            log.warn("[getStrategy][未找到策略: {}, 使用默认策略: {}]", 
                    strategyCode, defaultStrategy.getCode());
            return strategyRegistry.get(defaultStrategy.getCode());
        }
        
        return strategy;
    }

    /**
     * 设置默认策略
     *
     * @param defaultStrategy 默认策略枚举
     */
    public void setDefaultStrategy(ImageGenerationStrategyEnum defaultStrategy) {
        this.defaultStrategy = defaultStrategy;
        log.info("[setDefaultStrategy][默认策略已更新为: {}]", defaultStrategy.getCode());
    }

    /**
     * 获取所有可用策略
     *
     * @return 策略编码列表
     */
    public List<String> getAvailableStrategies() {
        return strategies.stream()
                .map(s -> s.getStrategy().getCode())
                .collect(java.util.stream.Collectors.toList());
    }

}
