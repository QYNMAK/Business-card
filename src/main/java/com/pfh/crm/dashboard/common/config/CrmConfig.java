package com.pfh.crm.dashboard.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "crm.internal")
public class CrmConfig {

    /**
     * 小程序 appId
     */
    private String appId;

    /**
     * 小程序 appSecret
     */
    private String appSecret;

    private String appLoginUrl;

}
