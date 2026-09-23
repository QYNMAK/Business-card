package com.pfh.crm.dashboard.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * 名片信息更新 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardUpdateDTO {

    /** 用户 id（拦截器注入，前端可不传） */
    private String id;

    /** 姓名 / 昵称 */
    @Size(max = 50, message = "姓名不能超过50字")
    private String username;

    /** 业务类型 */
    @Size(max = 50, message = "业务类型不能超过50字")
    private String bizType;

    /** 业务名称 */
    @Size(max = 50, message = "业务名称不能超过50字")
    private String bizName;

    /** 一句话介绍（副标题） */
    @Size(max = 200, message = "介绍不能超过200字")
    private String description;

    /** 电话号码 */
    @Size(max = 20, message = "电话长度不能超过20位")
    private String phone;

    /** 微信号 */
    @Size(max = 64, message = "微信号长度不能超过64位")
    private String wechatId;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 消息链接（平台私信 URL） */
    @Size(max = 500, message = "链接长度不能超过500字")
    private String msgLink;

    /** 地址 / 坐标 */
    @Size(max = 100, message = "地址不能超过100字")
    private String address;

    /** 问候语 / 备注 */
    @Size(max = 200, message = "问候语不能超过200字")
    private String greeting;

    /** 所在平台（微信 / 小红书 / 抖音 等） */
    @Size(max = 50, message = "平台名称不能超过50字")
    private String platform;

    /** 头像 url（OSS 相对路径，传则更新） */
    private String headPic;

    /** 微信二维码图片（OSS 相对路径，必填） */
    private String qr;
}
