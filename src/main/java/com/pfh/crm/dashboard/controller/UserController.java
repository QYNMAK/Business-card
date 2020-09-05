package com.pfh.crm.dashboard.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.config.GlobalUserCache;
import com.pfh.crm.dashboard.common.config.QrHolder;
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
import com.pfh.crm.dashboard.service.ICustomerService;
import com.pfh.crm.dashboard.service.IUserService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
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

    /**
     * 客户服务
     */
    private ICustomerService customerService;

    private AliyunOSSConfig aliyunOSSConfig;

    @SelfLimiter
    @RequestMapping("login")
    public Result<LoginResult> login(String code, HttpServletResponse response) {

        WxLogin wxLogin = userService.login(code);

        String unionId = wxLogin.getOpenid();

        LambdaQueryWrapper<User> userQueryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUnionId, unionId);
        User user = userService.getOne(userQueryWrapper, false);
        LoginResult loginResult = null;


        LoginUserCache userCache = LoginUserCache.builder()
                .openid(wxLogin.getOpenid())
                .sessionKey(wxLogin.getSession_key())
                .unionId(unionId)
                .build();

        String jid = UUID.randomUUID().toString();

        loginResult = LoginResult.builder().register(true).build();
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
            e.printStackTrace();
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

    @SelfLimiter
    @GetMapping("qr/user/{uid}")
    public Result<QrUserVO> getQrUser(@PathVariable("uid") String jid) {
        String userId = QrHolder.get(jid);
        User user = userService.getById(userId);
        QrUserVO userVO = new QrUserVO();
        BeanUtils.copyProperties(user, userVO);
        LoginUserCache loginUserCache = GlobalUserCache.get();
        boolean addAble = true;
        addAble = !loginUserCache.getUserId().equals(userId);
        if (addAble) {
            addAble = !customerService.exists(userId, loginUserCache.getUserId());
        }
        userVO.setAddAble(addAble);

        return Result.ok(userVO);
    }

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
            e.printStackTrace();
            return Result.ok();
        }
        JwtUtil.delJwt(claims.getId());

        return Result.ok();
    }

    @SelfLimiter
    @GetMapping("current")
    public Result<SimpleUserVO> getCurrent(){
        LoginUserCache loginUserCache = GlobalUserCache.get();
        return getCurrent(loginUserCache.getUserId());
    }

    @SelfLimiter
    @GetMapping("/{id}")
    public Result<SimpleUserVO> getCurrent(@PathVariable("id") String id){
        User user = userService.getById(id);
        if (user.getHeadPic().startsWith(GlobalUserCache.WX_URL)) {
            try{
                ByteArrayOutputStream outputStream = ImgUtils.image2Stream(ImageIO.read(new URL(user.getHeadPic())));
                String uploadImg = AliyunOSSUtils.uploadImg(outputStream.toByteArray());
                user.setHeadPic(aliyunOSSConfig.getImgPreview() + uploadImg);
                userService.updateById(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SimpleUserVO simpleUserVO = new SimpleUserVO();
        BeanUtils.copyProperties(user,simpleUserVO);
        return Result.ok(simpleUserVO);
    }

    @SelfLimiter
    @PostMapping("/update")
    public Result setting(@RequestBody UserSettingDTO userSettingDTO){
        userService.update(userSettingDTO);
        return Result.ok();
    }
}
