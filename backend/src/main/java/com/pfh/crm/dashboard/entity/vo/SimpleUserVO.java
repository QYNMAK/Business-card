package com.pfh.crm.dashboard.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SimpleUserVO {
    private String id;
    private String phone;
    private String username;
    private String headPic;
    private String email;
    private String wechatId;
    private String description;
    private String greeting;
    private String bizType;
    private String bizName;
    private String msgLink;
    private String qr;
    private String platform;
    private String province;
    private String city;
    private String county;
    private String address;
    private int sex;
}
