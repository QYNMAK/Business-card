package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_customer", schema = "crm")
public class Customer extends BaseEntity {


    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 客户姓名
     */
    private String name;
    /**
     * 性别
     */
    private int sex;
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
     * 手机号码
     */
    private String phone;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 备注
     */
    private String remark;

    /**
     * 用户id
     */
    private String userId;

}
