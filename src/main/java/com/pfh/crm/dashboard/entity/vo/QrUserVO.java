package com.pfh.crm.dashboard.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QrUserVO {
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
     * 是否好友
     */
    private boolean addAble;
}
