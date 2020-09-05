package com.pfh.crm.dashboard.common.constant;

/**
 * 通用常量
 */
public class CommonConstant {

    /**
     * 二维码过期时间
     */
    public static final Integer QR_TIMEOUT_SECONDED = 30;

    /**
     * 二维码创建重试次数
     */
    public static final Integer QR_CREATE_RETRIES = 3;

    /**
     * 二维码创建重试的延迟时间
     */
    public static final Long QR_CREATE_DELAY = 2000L;

    /**
     * 二维码创建重试上一次延时时间是这一次的倍数
     */
    public static final Double QR_CREATE_MULTIPLIER = 1.5;


}
