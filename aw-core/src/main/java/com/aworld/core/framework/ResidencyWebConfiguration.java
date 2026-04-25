package com.aworld.core.framework;

import com.aworld.framework.web.config.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

import static com.aworld.core.enums.AWorldConstants.SITE_API_PREFIX;

/**
 * 入驻拦截器配置
 *
 * @author aw
 */
@Configuration
public class ResidencyWebConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;

    @Bean
    public ResidencyInterceptor residencyInterceptor() {
        return new ResidencyInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(residencyInterceptor())
                .addPathPatterns(webProperties.getAgentApi().getPrefix() + " + SITE_API_PREFIX + " + "/**");
    }

}
