package com.pfh.crm.dashboard.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerQuery {

    /**
     * 搜索关键词
     */
    private String keywords;

    /**
     * userId
     */
    private String userId;

    /**
     * 最小年龄
     */
    private Integer minAge;

    /**
     * 最大年龄
     */
    private Integer maxAge;
}
