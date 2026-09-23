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
import com.pfh.crm.dashboard.entity.dto.CardUpdateDTO;
import com.pfh.crm.dashboard.entity.dto.UserRegister;
import com.pfh.crm.dashboard.entity.dto.UserSettingDTO;
import com.pfh.crm.dashboard.entity.dto.WxLogin;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.mapper.UserMapper;
import com.pfh.crm.dashboard.service.IUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private CrmConfig crmConfig;
    private UserMapper userMapper;

    @Override
    public WxLogin login(String code) {
        Map<String, String> loginParams = new HashMap<>(4);
        loginParams.put("appid", crmConfig.getAppId());
        loginParams.put("secret", crmConfig.getAppSecret());
        loginParams.put("js_code", code);
        loginParams.put("grant_type", "authorization_code");

        String rst = OkHttpUtil.get(crmConfig.getAppLoginUrl(), loginParams);
        log.debug("wx login response: {}", rst);

        WxLogin loginRst = JSON.parseObject(rst, WxLogin.class);
        Integer errCode = loginRst.getErrcode();
        WxLoginEnum loginEnum = WxLoginEnum.getByCode(errCode);
        if (loginEnum == null) {
            throw new BusinessException("微信登录失败，未知错误码: " + errCode);
        }
        if (!WxLoginEnum.loginSuccess(loginEnum)) {
            throw new BusinessException(loginEnum.getMsg());
        }
        return loginRst;
    }

    @Override
    public User register(UserRegister userRegister) {
        LambdaQueryWrapper<User> userQueryWrapper = Wrappers.<User>lambdaQuery()
                .eq(User::getUnionId, userRegister.getUnionId());
        User oldUser = getOne(userQueryWrapper, false);

        if (oldUser != null) {
            throw new BusinessException("用户已存在，请勿重复注册");
        }

        User user = User.builder()
                .headPic(userRegister.getAvatarUrl())
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

    // ===== 名片相关 =====

    @Override
    public User getCard(String userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public void updateCard(CardUpdateDTO dto) {
        User user = getById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!StringUtils.isEmpty(dto.getUsername())) {
            user.setUsername(dto.getUsername());
        }
        if (!StringUtils.isEmpty(dto.getBizType())) {
            user.setBizType(dto.getBizType());
        }
        if (!StringUtils.isEmpty(dto.getBizName())) {
            user.setBizName(dto.getBizName());
        }
        if (!StringUtils.isEmpty(dto.getDescription())) {
            user.setDescription(dto.getDescription());
        }
        if (!StringUtils.isEmpty(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (!StringUtils.isEmpty(dto.getWechatId())) {
            user.setWechatId(dto.getWechatId());
        }
        if (!StringUtils.isEmpty(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (!StringUtils.isEmpty(dto.getMsgLink())) {
            user.setMsgLink(dto.getMsgLink());
        }
        if (!StringUtils.isEmpty(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (!StringUtils.isEmpty(dto.getGreeting())) {
            user.setGreeting(dto.getGreeting());
        }
        if (!StringUtils.isEmpty(dto.getPlatform())) {
            user.setPlatform(dto.getPlatform());
        }
        if (!StringUtils.isEmpty(dto.getHeadPic())) {
            user.setHeadPic(dto.getHeadPic());
        }
        if (!StringUtils.isEmpty(dto.getQr())) {
            user.setQr(dto.getQr());
        }

        updateById(user);
    }

    @Override
    public void clearCard(String userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setPhone(null);
        user.setEmail(null);
        user.setWechatId(null);
        user.setDescription(null);
        user.setGreeting(null);
        user.setBizType(null);
        user.setBizName(null);
        user.setMsgLink(null);
        user.setAddress(null);
        user.setQr(null);
        user.setPlatform(null);

        updateById(user);
    }
}
