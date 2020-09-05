package com.pfh.crm.dashboard.common.config;

import com.pfh.crm.dashboard.common.model.LoginUserCache;

/**
 * 用户缓存
 */
public class GlobalUserCache {

    public static final String WX_URL = "https://wx.qlogo.cn/mmopen/vi_32";

    /**
     * 用户信息缓存
     */
    private static ThreadLocal<LoginUserCache> userCacheThreadLocal = new ThreadLocal<>();


    public static void set(LoginUserCache userCache){
        userCacheThreadLocal.set(userCache);
    }

    public static LoginUserCache get(){
        LoginUserCache loginUserCache = userCacheThreadLocal.get();
        userCacheThreadLocal.remove();
        return loginUserCache;
    }
}
