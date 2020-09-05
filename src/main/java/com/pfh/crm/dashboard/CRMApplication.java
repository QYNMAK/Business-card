package com.pfh.crm.dashboard;

import com.pfh.crm.dashboard.common.util.ApplicationContextUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
@MapperScan("com.pfh.crm.dashboard.mapper")
public class CRMApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(CRMApplication.class, args);
        ApplicationContextUtils.setContext(context);

    }

}
