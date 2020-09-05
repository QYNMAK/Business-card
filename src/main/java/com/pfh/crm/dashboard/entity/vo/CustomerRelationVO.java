package com.pfh.crm.dashboard.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerRelationVO {
    /**
     * id
     */
    private String id;
    /**
     * 客户名字
     */
    private String name;
    /**
     * 预览头像
     */
    private String preViewImg;
}
