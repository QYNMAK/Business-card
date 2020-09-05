package com.pfh.crm.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pfh.crm.dashboard.entity.dto.UserRegister;
import com.pfh.crm.dashboard.entity.dto.UserSettingDTO;
import com.pfh.crm.dashboard.entity.dto.WxLogin;
import com.pfh.crm.dashboard.entity.po.User;

/**
 * 用户接口
 *
 */
public interface IUserService extends IService<User> {

    /**
     * 微信登陆
     *
     * @param code 用户登录凭证
     */
    WxLogin login(String code);

    /**
     * 用户注册
     *
     * @param userRegister 注册信息
     * @return User
     */
    User register(UserRegister userRegister);

    /**
     * 修改用户信息
     *
     * @param userSettingDTO 用户信息
     */
    void update(UserSettingDTO userSettingDTO);
}
