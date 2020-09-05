package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_user", schema = "crm")
@Builder
public class User extends BaseEntity {

    @TableId(type = IdType.UUID)
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
     * 密码
     */
    private String password;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 头像url
     */
    private String headPic;
    /**
     * 微信用户唯一id
     */
    private String unionId;
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
