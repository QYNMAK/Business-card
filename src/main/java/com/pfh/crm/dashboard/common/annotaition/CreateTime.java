package com.pfh.crm.dashboard.common.annotaition;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 创建时间
 * @author pengfh
 * @date 2020/3/13
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CreateTime {

    /**
     * 数据库字段别名
     *
     */
    String value() default "";

}
