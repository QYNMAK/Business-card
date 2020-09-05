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

        SelfLimiter rateLimiter = AnnotationUtils.findAnnotation(method, SelfLimiter.class);
        if (rateLimiter != null && rateLimiter.limit() > 0) {
            double limit = rateLimiter.limit();
            if (rateLimiterCache.get(method.getName()) == null) {
                rateLimiterCache.put(method.getName(), RateLimiter.create(limit));
            }

            log.info("限流设置为: " + rateLimiterCache.get(method.getName()).getRate());
            // 尝试获取令牌
            if (rateLimiterCache.get(method.getName()) != null && !rateLimiterCache.get(method.getName()).tryAcquire(rateLimiter.timeout(), rateLimiter.timeUnit())) {
                log.info("点太快了，等会儿~");
                throw new BusinessException("点太快了，等会儿~");
            }
        }
        return point.proceed();
    }
}
