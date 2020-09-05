package com.pfh.crm.dashboard.common.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private CrmAuthHandler interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	// 添加拦截器，配置拦截地址
        registry.addInterceptor(interceptor).excludePathPatterns("/user/login","/user/login/out","/user/register");
    }
}
