package com.pfh.crm.dashboard.common.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pfh.crm.dashboard.common.constant.CommonConstant;
import com.pfh.crm.dashboard.common.exception.BusinessException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QrHolder {

    private static Cache<Object, Object> qrCache;



    static {
        qrCache = CacheBuilder.newBuilder().expireAfterWrite(CommonConstant.QR_TIMEOUT_SECONDED, TimeUnit.SECONDS).build();
    }

    /**
     * 缓存二维码链接
     *
     * @param content 二维码内容
     * @return 唯一id
     */
    public static String set(String content) {
        String unionId = UUID.randomUUID().toString();
        qrCache.put(unionId, content);
        return unionId;
    }

    /**
     * 获取缓存数据
     *
     * @param cacheId 缓存key
     * @return 缓存数据，如果存在，否则返回null
     */
    public static String get(String cacheId) {

        Object cacheObj = qrCache.getIfPresent(cacheId);

        if (cacheObj == null)
        {
            throw new BusinessException("二维码已过期");
        }

        return cacheObj.toString();
    }

}
