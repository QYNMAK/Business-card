package com.pfh.crm.dashboard.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegister {
    /**
     * 用户名
     */
    private String nickName;
    /**
     * 手机
     */
    private String phone;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 所在省
     */
    private String province;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 0	未知
     * 1	男性
     * 2	女性
     */
    private int gender;
    /**
     * 唯一id
     */
    private String unionId;
}
