package com.pfh.crm.dashboard.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 管理员修改用户 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserUpdateDTO {

    @Size(max = 50)
    private String username;
    @Size(max = 20)
    private String phone;
    @Email
    private String email;
    @Size(max = 64)
    private String wechatId;
    @Size(max = 200)
    private String description;
    @Size(max = 200)
    private String greeting;
    @Size(max = 50)
    private String bizType;
    @Size(max = 50)
    private String bizName;
    @Size(max = 500)
    private String msgLink;
    @Size(max = 100)
    private String address;
    @Size(max = 50)
    private String platform;
    private String headPic;
    private String qr;
    /** 1正常 0非正常 */
    private Integer status;
}
