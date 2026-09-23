package com.pfh.crm.dashboard.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserCache {

    /**
     * 小程序登陆id
     */
    private String openid;
    /**
     * jwt
     */
    private String sessionKey;
    /**
     * 微信用户唯一id
     */
    private String unionId;
    /**
     * crm 唯一id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * jid
     */
    private String jid;
}
