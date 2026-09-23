package com.pfh.crm.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.config.GlobalUserCache;
import com.pfh.crm.dashboard.common.config.QrHolder;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.model.LoginResult;
import com.pfh.crm.dashboard.common.model.LoginUserCache;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.common.util.AliyunOSSUtils;
import com.pfh.crm.dashboard.common.util.ImgUtils;
import com.pfh.crm.dashboard.common.util.JwtUtil;
import com.pfh.crm.dashboard.entity.dto.UserRegister;
import com.pfh.crm.dashboard.entity.dto.UserSettingDTO;
import com.pfh.crm.dashboard.entity.dto.WxLogin;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.entity.vo.QrUserVO;
import com.pfh.crm.dashboard.entity.vo.SimpleUserVO;
import com.pfh.crm.dashboard.service.IUserService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.UUID;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {

    /**
     * 用户服务
     */
    private IUserService userService;

    private AliyunOSSConfig aliyunOSSConfig;

    /**
     * 小程序登录：用 code 换 openid/session_key，若用户不存在则标记需要注册
     */
    @SelfLimiter
    @RequestMapping("login")
    public Result<LoginResult> login(String code, HttpServletResponse response) {

        WxLogin wxLogin = userService.login(code);
        String unionId = wxLogin.getOpenid();

        LambdaQueryWrapper<User> userQueryWrapper =
                Wrappers.<User>lambdaQuery().eq(User::getUnionId, unionId);
        User user = userService.getOne(userQueryWrapper, false);

        LoginUserCache userCache = LoginUserCache.builder()
                .openid(wxLogin.getOpenid())
                .sessionKey(wxLogin.getSession_key())
                .unionId(unionId)
                .build();

        String jid = UUID.randomUUID().toString();
        LoginResult loginResult = LoginResult.builder().register(true).build();

        if (user == null) {
            loginResult.setRegister(false);
        } else {
            userCache.setUserId(user.getId());
            userCache.setUserName(user.getUsername());
            userCache.setAvatarUrl(user.getHeadPic());
            userCache.setJid(jid);
        }

        String jwt = JwtUtil.createJWT(jid, JSON.toJSONString(userCache), null);
        loginResult.setJwt(jwt);

        return Result.ok(loginResult);
    }

    /**
     * 开发登录：浏览器 / H5（web/card.html）模拟一个用户换取 JWT，绕过微信 wx.login。
     * 传 unionId 作为身份标识；用户不存在则自动注册。
     * 仅用于本地开发联调，生产环境应删除该接口或用 IP 白名单限制。
     *
     * @param unionId  模拟的用户唯一标识
     * @param username 首次自动注册时的昵称（可选）
     */
    @SelfLimiter
    @GetMapping("dev-login")
    public Result<LoginResult> devLogin(@RequestParam("unionId") String unionId,
                                        @RequestParam(value = "username", required = false) String username) {
        User user = userService.getOne(
                Wrappers.<User>lambdaQuery().eq(User::getUnionId, unionId), false);
        boolean registered = user != null;
        if (user == null) {
            String name = (username != null && !username.trim().isEmpty()) ? username.trim() : unionId;
            user = User.builder()
                    .unionId(unionId)
                    .username(name)
                    .status(1)
                    .build();
            userService.save(user);
        }
        String jid = UUID.randomUUID().toString();
        LoginUserCache userCache = LoginUserCache.builder()
                .unionId(unionId)
                .userId(user.getId())
                .userName(user.getUsername())
                .avatarUrl(user.getHeadPic())
                .jid(jid)
                .build();
        String jwt = JwtUtil.createJWT(jid, JSON.toJSONString(userCache), null);
        LoginResult loginResult = LoginResult.builder().jwt(jwt).register(registered).build();
        return Result.ok(loginResult);
    }

    /**
     * 注册
     */
    @SelfLimiter
    @PostMapping("register")
    public Result<LoginResult> register(@RequestBody UserRegister userRegister, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            throw HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "un auth", null, null, Charset.defaultCharset());
        }

        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            throw HttpClientErrorException.create(HttpStatus.UNAUTHORIZED, "un auth", null, null, Charset.defaultCharset());
        }

        LoginUserCache userCache = JSON.parseObject(claims.getSubject(), LoginUserCache.class);
        userRegister.setUnionId(userCache.getUnionId());

        String jid = UUID.randomUUID().toString();
        User user = userService.register(userRegister);

        userCache.setUserName(user.getUsername());
        userCache.setUserId(user.getId());
        userCache.setAvatarUrl(user.getHeadPic());
        userCache.setJid(jid);

        String jwt = JwtUtil.createJWT(jid, JSON.toJSONString(userCache), null);
        LoginResult loginResult = LoginResult.builder().jwt(jwt).register(true).build();

        return Result.ok(loginResult);
    }

    /**
     * 扫码查看他人名片
     *
     * @param jid 二维码中携带的 uuid
     */
    @SelfLimiter
    @GetMapping("qr/user/{jid}")
    public Result<QrUserVO> getQrUser(@PathVariable("jid") String jid) {
        String userId;
        try {
            userId = QrHolder.get(jid);
        } catch (BusinessException e) {
            return Result.ok(null); // 链接已过期
        }
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok(null);
        }
        return Result.ok(QrUserVO.build(user));
    }

    /**
     * 退出登录
     */
    @SelfLimiter
    @GetMapping("login/out")
    public Result loginOut(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            return Result.ok();
        }

        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            return Result.ok();
        }
        JwtUtil.delJwt(claims.getId());
        return Result.ok();
    }

    /**
     * 获取当前用户信息（"我的"页面）
     */
    @SelfLimiter
    @GetMapping("current")
    public Result<SimpleUserVO> getCurrent() {
        LoginUserCache loginUserCache = GlobalUserCache.get();
        return getCurrent(loginUserCache.getUserId());
    }

    /**
     * 获取指定用户信息
     */
    @SelfLimiter
    @GetMapping("/{id}")
    public Result<SimpleUserVO> getCurrent(@PathVariable("id") String id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 微信头像迁移到 OSS（避免 wx.qlogo.cn 链接过期）
        String headPic = user.getHeadPic();
        if (headPic != null && headPic.startsWith(GlobalUserCache.WX_URL)) {
            try {
                ByteArrayOutputStream outputStream = ImgUtils.image2Stream(ImageIO.read(new URL(headPic)));
                String uploadImg = AliyunOSSUtils.uploadImg(outputStream.toByteArray());
                user.setHeadPic(aliyunOSSConfig.getImgPreview() + uploadImg);
                userService.updateById(user);
            } catch (IOException | RuntimeException ignored) {
                // 头像迁移失败不阻塞主流程
            }
        }

        SimpleUserVO simpleUserVO = new SimpleUserVO();
        BeanUtils.copyProperties(user, simpleUserVO);
        return Result.ok(simpleUserVO);
    }

    /**
     * 更新用户基础资料
     */
    @SelfLimiter
    @PostMapping("/update")
    public Result setting(@RequestBody UserSettingDTO userSettingDTO) {
        userService.update(userSettingDTO);
        return Result.ok();
    }
}
