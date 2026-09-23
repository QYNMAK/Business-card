package com.pfh.crm.dashboard.entity.vo;

import com.pfh.crm.dashboard.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员端用户 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserVO {
    private String id;
    private String username;
    private String phone;
    private String email;
    private String wechatId;
    private String platform;
    private String bizType;
    private String bizName;
    private String description;
    private String greeting;
    private String msgLink;
    private String address;
    private String headPic;
    private String qr;
    private Integer status;
    private String unionId;
    private java.util.Date created;
    private java.util.Date updated;

    public static AdminUserVO from(User u) {
        return AdminUserVO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .phone(u.getPhone())
                .email(u.getEmail())
                .wechatId(u.getWechatId())
                .platform(u.getPlatform())
                .bizType(u.getBizType())
                .bizName(u.getBizName())
                .description(u.getDescription())
                .greeting(u.getGreeting())
                .msgLink(u.getMsgLink())
                .address(u.getAddress())
                .headPic(u.getHeadPic())
                .qr(u.getQr())
                .status(u.getStatus())
                .unionId(u.getUnionId())
                .created(u.getCreated())
                .updated(u.getUpdated())
                .build();
    }
}
