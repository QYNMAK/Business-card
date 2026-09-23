package com.pfh.crm.dashboard.controller;


import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.config.GlobalUserCache;
import com.pfh.crm.dashboard.common.config.QrHolder;
import com.pfh.crm.dashboard.common.constant.CommonConstant;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.model.CrmQrResult;
import com.pfh.crm.dashboard.common.model.LoginUserCache;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.common.util.AliyunOSSUtils;
import com.pfh.crm.dashboard.common.util.ImgUtils;
import com.pfh.crm.dashboard.common.util.QRUtil;
import com.pfh.crm.dashboard.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RequestMapping("qr")
@RestController
@AllArgsConstructor
public class QrController {

    private AliyunOSSConfig aliyunOSSConfig;

    private IUserService userService;

    @SelfLimiter(limit = 1, timeout = 2000)
    @GetMapping("create")
    public Result<CrmQrResult> create() {
        LoginUserCache loginUserCache = GlobalUserCache.get();
        URL logoUrl;
        try {
            logoUrl = new URL(userService.getById(loginUserCache.getUserId()).getHeadPic());
        } catch (MalformedURLException e) {
            throw new BusinessException("服务器忙请稍后再试试");
        }

        // 手动重试，避免 @Retryable 在 private 方法上不生效的问题
        byte[] bytes = null;
        int attempts = 0;
        int maxAttempts = CommonConstant.QR_CREATE_RETRIES;
        while (attempts < maxAttempts) {
            try {
                bytes = doGenerateQr(loginUserCache, logoUrl);
                break;
            } catch (Exception e) {
                log.warn("二维码生成失败，第 {} 次尝试", attempts + 1, e);
                attempts++;
                if (attempts >= maxAttempts) {
                    throw new BusinessException("服务器忙请稍后再试试");
                }
                try {
                    long delay = CommonConstant.QR_CREATE_DELAY * (long) Math.pow(CommonConstant.QR_CREATE_MULTIPLIER, attempts - 1);
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException("服务器忙请稍后再试试");
                }
            }
        }

        String qrUrl = aliyunOSSConfig.getImgPreview() + AliyunOSSUtils.uploadImg(bytes);

        CrmQrResult qrResult = CrmQrResult.builder()
                .qrUrl(qrUrl)
                .timeOutSeconded(CommonConstant.QR_TIMEOUT_SECONDED)
                .build();

        return Result.ok(qrResult);
    }

    private byte[] doGenerateQr(LoginUserCache loginUserCache, URL logoUrl) throws Exception {
        String unionId = QrHolder.set(loginUserCache.getUserId());
        BufferedImage bufferedImage = QRUtil.builder().width(400).height(400).build().qrEncode(unionId, logoUrl);
        return ImgUtils.image2Stream(bufferedImage).toByteArray();
    }

}
