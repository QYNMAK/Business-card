package com.pfh.crm.dashboard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.entity.dto.AdminUserUpdateDTO;
import com.pfh.crm.dashboard.entity.po.AuditLog;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.entity.vo.AdminUserVO;
import com.pfh.crm.dashboard.service.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员端接口
 *
 * <p>鉴权：请求头 {@code X-Admin-Key} 与 application.yml 中
 * {@code crm.internal.admin-api-key} 匹配。</p>
 */
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    private final AliyunOSSConfig aliyunOSSConfig;

    /** 通过字段注入，避免 @AllArgsConstructor 将 String 参数当作构造函数 bean 注入 */
    @Value("${crm.internal.admin-api-key:}")
    private String adminApiKey;

    private void checkKey(String key) {
        if (adminApiKey == null || adminApiKey.isEmpty()
                || !adminApiKey.equals(key)) {
            throw new BusinessException("管理员认证失败");
        }
    }

    /** OSS 相对路径 → 完整可访问 URL（已是 http/https 或空则原样返回） */
    private String toFullUrl(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return aliyunOSSConfig.getImgPreview() + path;
    }

    // ========== 用户管理 ==========

    @GetMapping("users")
    public Result<IPage<AdminUserVO>> listUsers(
            @RequestHeader("X-Admin-Key") String adminKey,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String platform) {
        checkKey(adminKey);
        Page<User> pageParam = new Page<>(page, size);
        IPage<AdminUserVO> result = adminService.listUsers(pageParam, keyword, platform);
        // 把 headPic / qr 的 OSS 相对路径补全为可访问的完整 URL
        if (result.getRecords() != null) {
            for (AdminUserVO vo : result.getRecords()) {
                vo.setHeadPic(toFullUrl(vo.getHeadPic()));
                vo.setQr(toFullUrl(vo.getQr()));
            }
        }
        return Result.ok(result);
    }

    @GetMapping("users/{id}")
    public Result<User> getUserDetail(
            @RequestHeader("X-Admin-Key") String adminKey,
            @PathVariable("id") String id) {
        checkKey(adminKey);
        return Result.ok(adminService.getUserDetail(id));
    }

    @PutMapping("users/{id}")
    public Result<User> updateUser(
            @RequestHeader("X-Admin-Key") String adminKey,
            @PathVariable("id") String id,
            @RequestBody AdminUserUpdateDTO dto) {
        checkKey(adminKey);
        String operator = adminKey.length() >= 4 ? adminKey.substring(0, 4) : adminKey;
        adminService.updateUser(id, dto, operator);
        return Result.ok(adminService.getUserDetail(id));
    }

    @DeleteMapping("users/{id}")
    public Result deleteUser(
            @RequestHeader("X-Admin-Key") String adminKey,
            @PathVariable("id") String id) {
        checkKey(adminKey);
        String operator = adminKey.length() >= 4 ? adminKey.substring(0, 4) : adminKey;
        adminService.deleteUser(id, operator);
        return Result.ok();
    }

    // ========== 统计 ==========

    /**
     * 各平台用户数量统计：{ "微信": 12, "小红书": 5, "未填写": 3 }
     */
    @GetMapping("stats/platform")
    public Result<Map<String, Integer>> platformStats(
            @RequestHeader("X-Admin-Key") String adminKey) {
        checkKey(adminKey);
        return Result.ok(adminService.platformStats());
    }

    // ========== 审计日志 ==========

    @GetMapping("audit-logs")
    public Result<IPage<AuditLog>> listAuditLogs(
            @RequestHeader("X-Admin-Key") String adminKey,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String targetUserId) {
        checkKey(adminKey);
        Page<AuditLog> pageParam = new Page<>(page, size);
        return Result.ok(adminService.listAuditLogs(pageParam, targetUserId));
    }
}
