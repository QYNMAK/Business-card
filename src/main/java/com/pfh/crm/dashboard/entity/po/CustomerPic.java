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
@TableName(value = "tb_customer_pic", schema = "crm")
public class CustomerPic extends BaseEntity{

    @TableId(type = IdType.UUID)
    private String id;
    /**
     * 客户id
     */
    private String customerId;
    /**
     * 图片id
     */
    private String picId;
}
