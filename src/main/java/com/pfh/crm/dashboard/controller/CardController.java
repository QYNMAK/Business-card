package com.pfh.crm.dashboard.controller;

import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.config.GlobalUserCache;
import com.pfh.crm.dashboard.common.config.QrHolder;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.model.LoginUserCache;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.entity.dto.CardUpdateDTO;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.entity.vo.CardVO;
import com.pfh.crm.dashboard.entity.vo.QrUserVO;
import com.pfh.crm.dashboard.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 名片控制器
 */
@RestController
@RequestMapping("card")
@AllArgsConstructor
public class CardController {

    private IUserService userService;

    private AliyunOSSConfig aliyunOSSConfig;

    /**
     * 获取当前用户名片
     */
    @SelfLimiter
    @GetMapping
    public Result<CardVO> getCurrent() {
        LoginUserCache loginUserCache = GlobalUserCache.get();
        User user = userService.getCard(loginUserCache.getUserId());
        CardVO vo = CardVO.build(user);
        vo.setQr(toFullUrl(vo.getQr()));
        vo.setHeadPic(toFullUrl(vo.getHeadPic()));
        return Result.ok(vo);
    }

    /**
     * 扫码查看他人名片
     */
    @SelfLimiter
    @GetMapping("/user/{jid}")
    public Result<QrUserVO> getUserCard(@PathVariable("jid") String jid) {
        String userId;
        try {
            userId = QrHolder.get(jid);
        } catch (BusinessException e) {
            throw new BusinessException("链接已过期，请重新扫码");
        }
        User user = userService.getCard(userId);
        QrUserVO vo = QrUserVO.build(user);
        vo.setQr(toFullUrl(vo.getQr()));
        vo.setHeadPic(toFullUrl(vo.getHeadPic()));
        return Result.ok(vo);
    }

    /**
     * 保存 / 编辑名片
     *
     * <p>前端先调 /file/img/upload 上传图片拿 OSS 相对路径，再随本接口提交。</p>
     */
    @SelfLimiter
    @PostMapping("/update")
    public Result<CardVO> update(@Validated @RequestBody CardUpdateDTO dto) {
        LoginUserCache loginUserCache = GlobalUserCache.get();
        dto.setId(loginUserCache.getUserId());
        userService.updateCard(dto);
        User user = userService.getCard(dto.getId());
        CardVO vo = CardVO.build(user);
        vo.setQr(toFullUrl(vo.getQr()));
        vo.setHeadPic(toFullUrl(vo.getHeadPic()));
        return Result.ok(vo);
    }

    /**
     * 清空名片信息
     */
    @SelfLimiter
    @PostMapping("/clear")
    public Result clear() {
        LoginUserCache loginUserCache = GlobalUserCache.get();
        userService.clearCard(loginUserCache.getUserId());
        return Result.ok();
    }

    private String toFullUrl(String path) {
        if (StringUtils.isEmpty(path)) return path;
        if (path.startsWith("http://") || path.startsWith("https://")) return path;
        return aliyunOSSConfig.getImgPreview() + path;
    }
}
