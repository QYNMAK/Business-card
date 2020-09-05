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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

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
        byte[] bytes = create(loginUserCache, logoUrl);

        String qrUrl = aliyunOSSConfig.getImgPreview() + AliyunOSSUtils.uploadImg(bytes);

        CrmQrResult qrResult = CrmQrResult.builder().qrUrl(qrUrl).timeOutSeconded(CommonConstant.QR_TIMEOUT_SECONDED).build();

        return Result.ok(qrResult);
    }

    @Retryable(value = BusinessException.class, maxAttempts = 3
            , backoff = @Backoff(delay = 2000L, multiplier = 1.5))
    private byte[] create(LoginUserCache loginUserCache, URL logoUrl) {
        try {
            String unionId = QrHolder.set(loginUserCache.getUserId());
            BufferedImage bufferedImage = QRUtil.builder().width(400).height(400).build().qrEncode(unionId, logoUrl);
            return ImgUtils.image2Stream(bufferedImage).toByteArray();
        } catch (Exception e) {
            throw new BusinessException("服务器忙请稍后再试试");
        }
    }

}
