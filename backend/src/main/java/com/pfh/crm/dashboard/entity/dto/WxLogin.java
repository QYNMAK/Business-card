package com.pfh.crm.dashboard.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * openid	string	用户唯一标识
 * session_key	string	会话密钥
 * unionid	string	用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回，详见 UnionID 机制说明。
 * errcode	number	错误码
 * errmsg	string	错误信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WxLogin {

    private String openid;
    @JSONField(name ="session_key")
    private String session_key;
    @JSONField(name ="unionid")
    private String unionid;
    @JSONField(name ="errcode")
    private Integer errcode = 0;
    @JSONField(name ="errmsg")
    private String errmsg;
}
