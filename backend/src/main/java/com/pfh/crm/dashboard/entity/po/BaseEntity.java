package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.pfh.crm.dashboard.common.annotaition.CreateTime;
import com.pfh.crm.dashboard.common.annotaition.Delete;
import com.pfh.crm.dashboard.common.annotaition.UpdateTime;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseEntity implements Serializable {
    /**
     * 创建时间
     */
    @CreateTime
    @TableField(fill = FieldFill.INSERT)
    private Date created;
    /**
     * 修改时间
     */
    @UpdateTime
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;

    @Delete
    @TableField(fill = FieldFill.INSERT)
    private Integer isDelete;
}
