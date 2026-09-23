package com.pfh.crm.dashboard.common.config;

import com.google.common.util.concurrent.RateLimiter;
import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    private static final ConcurrentMap<String, RateLimiter> rateLimiterCache = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.pfh.crm.dashboard.common.annotaition.SelfLimiter)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object pointcut(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 使用 类名+方法名 作为 key，避免不同 Controller 中同名方法共享限流器
        String cacheKey = method.getDeclaringClass().getSimpleName() + "." + method.getName();

        SelfLimiter rateLimiter = AnnotationUtils.findAnnotation(method, SelfLimiter.class);
        if (rateLimiter != null && rateLimiter.limit() > 0) {
            double limit = rateLimiter.limit();
            if (rateLimiterCache.get(cacheKey) == null) {
                rateLimiterCache.putIfAbsent(cacheKey, RateLimiter.create(limit));
            }

            log.debug("限流设置为: {}", rateLimiterCache.get(cacheKey).getRate());
            // 尝试获取令牌
            if (rateLimiterCache.get(cacheKey) != null && !rateLimiterCache.get(cacheKey).tryAcquire(rateLimiter.timeout(), rateLimiter.timeUnit())) {
                log.info("点太快了，等会儿~");
                throw new BusinessException("点太快了，等会儿~");
            }
        }
        return point.proceed();
    }
}
