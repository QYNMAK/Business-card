package com.pfh.crm.dashboard.common.annotaition;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 限流注解
 * @author funsonli
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SelfLimiter {

    // 限制次数 每秒
    double limit() default 10;

    //超时时长
    int timeout() default 1000;

    //超时时间单位
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
