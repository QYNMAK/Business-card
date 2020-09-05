package com.pfh.crm.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.pfh.crm.dashboard.common.config.CrmConfig;
import com.pfh.crm.dashboard.common.constant.WxLoginEnum;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.util.OkHttpUtil;
import com.pfh.crm.dashboard.entity.dto.UserRegister;
import com.pfh.crm.dashboard.entity.dto.UserSettingDTO;
import com.pfh.crm.dashboard.entity.dto.WxLogin;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.mapper.UserMapper;
import com.pfh.crm.dashboard.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现
 *
 * appid	string		是	小程序 appId
 * secret	string		是	小程序 appSecret
 * js_code	string		是	登录时获取的 code
 * grant_type	string		是	授权类型，此处只需填写 authorization_code
 *
 * errcode 的合法值
 *
 * 值	说明	最低版本
 * -1	系统繁忙，此时请开发者稍候再试
 * 0	请求成功
 * 40029	code 无效
 * 45011	频率限制，每个用户每分钟100次
 *
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 内部配置
     */
    private CrmConfig crmConfig;
    /**
     * 用户 mapper
     */
    private UserMapper userMapper;

    @Retryable(maxAttempts = 3
            , backoff = @Backoff(delay = 2000L, multiplier = 1.5),include = {Exception.class,RuntimeException.class})
    @Override
    public WxLogin login(String code) {

        Map<String,String> loginParams = new HashMap<>(4);
        loginParams.put("appid",crmConfig.getAppId());
        loginParams.put("secret",crmConfig.getAppSecret());
        loginParams.put("js_code",code);
        loginParams.put("grant_type","authorization_code");

        String rst = OkHttpUtil.get(crmConfig.getAppLoginUrl(), loginParams);

        log.debug("msg{}",rst);

        WxLogin loginRst = JSON.parseObject(rst, WxLogin.class);
        Integer errCode = loginRst.getErrcode();
        WxLoginEnum loginEnum = WxLoginEnum.getByCode(errCode);
        if (!WxLoginEnum.loginSuccess(loginEnum)) {
            throw new BusinessException(loginEnum.getMsg());
        }

        return loginRst;

    }

    @Override
    public User register(UserRegister userRegister) {

        LambdaQueryWrapper<User> userQueryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUnionId, userRegister.getUnionId());
        User oldUser = getOne(userQueryWrapper, false);

        if (oldUser != null) {
            throw new BusinessException("用户已存在，请勿重复注册");
        }

        User user = User.builder().headPic(userRegister.getAvatarUrl())
                .unionId(userRegister.getUnionId())
                .username(userRegister.getNickName())
                .sex(userRegister.getGender())
                .build();

        userMapper.insert(user);

        return user;
    }

    @Override
    public void update(UserSettingDTO userSettingDTO) {
        User old = getById(userSettingDTO.getId());
        if (old == null) {
            throw new BusinessException("用户不存在");
        }

        old.setHeadPic(userSettingDTO.getHeadPic());

        if (CollectionUtils.isNotEmpty(userSettingDTO.getPhoneNums())) {
            old.setPhone(Joiner.on(",").join(userSettingDTO.getPhoneNums()));
        }

        old.setUsername(userSettingDTO.getUsername());
        if (CollectionUtils.isNotEmpty(userSettingDTO.getRegion()) && userSettingDTO.getRegion().size() >= 3) {
            old.setProvince(userSettingDTO.getRegion().get(0));
            old.setCity(userSettingDTO.getRegion().get(1));
            old.setCounty(userSettingDTO.getRegion().get(2));
        }
        old.setAddress(userSettingDTO.getAddress());
        old.setSex(userSettingDTO.getSex());

        updateById(old);
    }
}
