package com.pfh.crm.dashboard.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 二维码内容
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrmQrResult {

    /**
     * 二维码地址
     */
    private String qrUrl;
    /**
     * 二维码过期时间
     */
    private Integer timeOutSeconded;
}
