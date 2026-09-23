package com.pfh.crm.dashboard.entity.vo;

import com.pfh.crm.dashboard.entity.po.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 名片 VO（用户端）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardVO {

    private String id;
    private String username;
    private String headPic;
    private String phone;
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
    private boolean saved;

    public static CardVO build(User user) {
        if (user == null) return null;
        boolean saved = notEmpty(user.getPhone()) || notEmpty(user.getEmail())
                || notEmpty(user.getWechatId()) || notEmpty(user.getDescription())
                || notEmpty(user.getGreeting()) || notEmpty(user.getBizType())
                || notEmpty(user.getBizName()) || notEmpty(user.getMsgLink())
                || notEmpty(user.getQr()) || notEmpty(user.getPlatform());
        return CardVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .headPic(user.getHeadPic())
                .phone(user.getPhone())
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
                .saved(saved)
                .build();
    }

    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
