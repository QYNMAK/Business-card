package com.pfh.crm.dashboard.common.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@AllArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private CrmAuthHandler interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 小程序接口走 JWT 鉴权
        // 管理员接口 /admin/** 走 X-Admin-Key 鉴权，排除 JWT 拦截
        registry.addInterceptor(interceptor)
                .excludePathPatterns(
                        "/user/login",
                        "/user/login/out",
                        "/user/register",
                        "/user/dev-login",
                        "/admin/**"
                );

        // 允许跨域（管理端 HTML 静态页面从浏览器直接调用 API）
        registry.addInterceptor(new CorsInterceptor())
                .addPathPatterns("/**")
                .order(-10);
    }

    /**
     * 简单 CORS 拦截器，允许任意来源
     */
    static class CorsInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Admin-Key");
            response.setHeader("Access-Control-Max-Age", "3600");
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                response.setStatus(HttpServletResponse.SC_OK);
                return false;
            }
            return true;
        }
    }
}
