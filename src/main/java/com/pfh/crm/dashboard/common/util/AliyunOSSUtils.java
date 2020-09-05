package com.pfh.crm.dashboard.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectResult;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Slf4j
public class AliyunOSSUtils {

    private AliyunOSSUtils() {
    }

    /**
     * oss文件上传
     *
     * @param content 文件
     * @param ojbName oss唯一文件名称
     */
    private static void upload(byte[] content, String ojbName) {
        OSS ossClient;

        // 创建OSSClient实例。
        ossClient = AliyunOSSConfig.ossClientInstance();


        PutObjectResult result = ossClient.putObject("crm-pfh", ojbName, new ByteArrayInputStream(content));
        if (!result.getClientCRC().equals(result.getServerCRC())) {
            throw new BusinessException("图片上传失败");
        }

    }

    /**
     * 图片上传
     *
     * @param file 图片
     * @return 文件唯一id
     */
    public static String uploadImg(MultipartFile file) {
        String objName = "images/" + UUID.randomUUID().toString();
        try {
            upload(file.getBytes(), objName);
        } catch (Exception e) {
            throw new BusinessException("上传失败", "100001", e);
        }
        return objName;
    }

    /**
     * 图片上传
     *
     * @param content 图片
     * @return 文件唯一id
     */
    public static String uploadImg(byte[] content) {
        String objName = "images/" + UUID.randomUUID().toString();
        try {
            upload(content, objName);
        } catch (Exception e) {
            throw new BusinessException("上传失败", "100001", e);
        }
        return objName;
    }
}
