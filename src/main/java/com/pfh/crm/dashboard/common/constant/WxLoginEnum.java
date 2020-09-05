package com.pfh.crm.dashboard.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 值	说明	最低版本
 * -1	系统繁忙，此时请开发者稍候再试
 * 0	请求成功
 * 40029	code 无效
 * 45011	频率限制，每个用户每分钟100次
 */
@Getter
@AllArgsConstructor
public enum WxLoginEnum {

    LOGIN_SUCCESS(0, "登陆成功"), WX_BUSY(-1, "系统繁忙，此时请稍候再试")
    , INVALID_CODE(40029, "code无效"), PASS_LIMIT_100(45011, "频率限制，每个用户每分钟100次");


    private Integer code;
    private String msg;

    public static WxLoginEnum getByCode(Integer code) {
        WxLoginEnum[] wxLoginEnums = WxLoginEnum.values();
        for (WxLoginEnum loginEnum : wxLoginEnums) {
            if (loginEnum.getCode().equals(code))
                return loginEnum;
        }
        return null;
    }

    public static boolean loginSuccess(Integer code) {
        WxLoginEnum loginEnum = getByCode(code);
        if (loginEnum == null) {
            throw new IllegalArgumentException("code无效");
        }

        return loginEnum.compareTo(LOGIN_SUCCESS) == 0;

    }

    public static boolean loginSuccess(WxLoginEnum loginEnum) {
        if (loginEnum == null) {
            throw new IllegalArgumentException("code无效");
        }

        return loginEnum.compareTo(LOGIN_SUCCESS) == 0;

    }

}
