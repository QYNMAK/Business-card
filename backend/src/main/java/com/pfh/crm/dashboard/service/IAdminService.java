package com.pfh.crm.dashboard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pfh.crm.dashboard.entity.dto.AdminUserUpdateDTO;
import com.pfh.crm.dashboard.entity.po.AuditLog;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.entity.vo.AdminUserVO;

import java.util.Map;

/**
 * 管理员服务接口
 */
public interface IAdminService {

    /**
     * 分页查询用户列表
     *
     * @param page     分页参数
     * @param keyword  模糊搜索（姓名/手机号/微信号/邮箱）
     * @param platform 平台过滤（可选）
     */
    IPage<AdminUserVO> listUsers(Page<User> page, String keyword, String platform);

    /**
     * 查看指定用户详情
     */
    User getUserDetail(String userId);

    /**
     * 修改用户（写审计日志）
     */
    void updateUser(String userId, AdminUserUpdateDTO dto, String operator);

    /**
     * 删除用户（软删除，写审计日志）
     */
    void deleteUser(String userId, String operator);

    /**
     * 平台使用统计
     *
     * @return platform → count
     */
    Map<String, Integer> platformStats();

    /**
     * 审计日志分页查询
     */
    IPage<AuditLog> listAuditLogs(Page<AuditLog> page, String targetUserId);
}
