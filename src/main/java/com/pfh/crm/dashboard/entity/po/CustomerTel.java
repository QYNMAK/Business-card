package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName(value = "tb_customer_tel", schema = "crm")
public class CustomerTel {
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 电话号码
     */
    private String customerId;

    /**
     * 手机号
     */
    private String phoneNum;
}
