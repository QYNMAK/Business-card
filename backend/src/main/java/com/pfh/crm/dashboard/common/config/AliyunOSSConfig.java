package com.pfh.crm.dashboard.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.pfh.crm.dashboard.common.util.ApplicationContextUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 阿里云oss存储配置
 *
 */
@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "crm.aliyun")
public class AliyunOSSConfig {
    /**
     * 访问外链
     */
    private String endpoint;
    /**
     * ak
     */
    private String accessKeyId;
    /**
     * sk
     */
    private String accessKeySecret;

    /**
     * 预览地址
     */
    private String imgPreview;

    @Bean
    public OSS ossClientBuilder (){
       return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public static OSS ossClientInstance(){
        return (OSS) ApplicationContextUtils.getBean("ossClientBuilder");
    }
}
