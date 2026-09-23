package com.pfh.crm.dashboard.entity.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 用户修改对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserSettingDTO {

    /**
     * 省市县
     */
    @Size(message = "请输入正确的省市县")
    private List<String> region;

    /**
     * 手机号码
     */
    @Size(message = "手机号最多支持10个")
    private List<String> phoneNums;

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
