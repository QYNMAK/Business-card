package com.pfh.crm.dashboard.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SimpleUserVO {
    private String id;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 用户名
     */
    private String username;
    /**
     * 头像url
     */
    private String headPic;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县
     */
    private String county;
    /**
     * 地址
     */
    private String address;
    /**
     * 0	未知
     * 1	男性
     * 2	女性
     */
    private int sex;
}
