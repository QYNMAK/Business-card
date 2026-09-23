package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_user", schema = "crm")
@Builder
public class User extends BaseEntity {

    @TableId(type = IdType.UUID)
    private String id;

    /** 手机号 */
    private String phone;
    /** 用户名（名片姓名） */
    private String username;
    /** 密码（保留字段） */
    private String password;
    /** 状态 1正常 0非正常 */
    private Integer status;
    /** 头像url */
    private String headPic;
    /** 微信用户唯一id */
    private String unionId;
    /** 0未知 1男 2女 */
    private int sex;

    /** 省 */
    private String province;
    /** 市 */
    private String city;
    /** 县 */
    private String county;
    /** 详细地址 */
    private String address;

    // ===== 名片字段 =====

    /** 邮箱 */
    private String email;
    /** 微信号 */
    private String wechatId;
    /** 名片描述（副标题） */
    private String description;
    /** 问候语 / 备注 */
    private String greeting;
    /** 业务类型 */
    private String bizType;
    /** 业务名称 */
    private String bizName;
    /** 消息链接 */
    private String msgLink;
    /** 微信二维码图片 OSS 路径 */
    private String qr;
    /** 所在平台（微信 / 小红书 / 抖音 等） */
    private String platform;
}
