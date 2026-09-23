package com.pfh.crm.dashboard.entity.vo;

import com.pfh.crm.dashboard.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 扫码识别到的用户名片信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class QrUserVO {
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
    private String address;
    private String platform;
    private int sex;

    public static QrUserVO build(User user) {
        if (user == null) return null;
        return QrUserVO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .username(user.getUsername())
                .headPic(user.getHeadPic())
                .email(user.getEmail())
                .wechatId(user.getWechatId())
                .description(user.getDescription())
                .greeting(user.getGreeting())
                .bizType(user.getBizType())
                .bizName(user.getBizName())
                .msgLink(user.getMsgLink())
                .qr(user.getQr())
                .address(user.getAddress())
                .platform(user.getPlatform())
                .sex(user.getSex())
                .build();
    }
}
