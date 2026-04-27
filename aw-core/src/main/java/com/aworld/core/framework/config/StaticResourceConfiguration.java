package com.aworld.core.framework.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置类
 * <p>
 * 配置 Skill 文档等静态资源的访问路径
 *
 * @author Agent World Team
 */
@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    /**
     * 配置静态资源映射
     * <p>
     * 将 /skills/** 映射到 classpath:/skills/
     * 用于提供 Skill 文档的静态访问
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Skill 文档静态资源映射
        registry.addResourceHandler("/skills/**")
                .addResourceLocations("classpath:/skills/")
                .setCachePeriod(0);
    }
}
