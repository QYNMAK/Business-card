package com.pfh.crm.dashboard.common.util;

import lombok.Data;
import org.springframework.context.ApplicationContext;

@Data
public class ApplicationContextUtils {
    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        ApplicationContextUtils.context = context;
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> t) {
        return context.getBean(t);
    }
}
