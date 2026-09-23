package com.pfh.crm.dashboard.common.annotaition;

import java.lang.annotation.*;

/**
 * @author pengfh
 * @date 2020/3/13
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UpdateTime {

    /**
     * 数据库字段别名
     *
     */
    String value() default "";
}
