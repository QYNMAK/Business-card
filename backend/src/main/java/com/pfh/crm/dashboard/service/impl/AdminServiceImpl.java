package com.pfh.crm.dashboard.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.entity.dto.AdminUserUpdateDTO;
import com.pfh.crm.dashboard.entity.po.AuditLog;
import com.pfh.crm.dashboard.entity.po.User;
import com.pfh.crm.dashboard.entity.vo.AdminUserVO;
import com.pfh.crm.dashboard.mapper.AuditLogMapper;
import com.pfh.crm.dashboard.mapper.UserMapper;
import com.pfh.crm.dashboard.service.IAdminService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理员服务实现
 */
@Service
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
@AllArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private UserMapper userMapper;
    private AuditLogMapper auditLogMapper;

    @Override
    public IPage<AdminUserVO> listUsers(Page<User> page, String keyword, String platform) {
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery()
                .ne(User::getIsDelete, 1);

        if (!StringUtils.isEmpty(keyword)) {
            String kw = keyword.trim();
            wrapper.and(w -> w
                    .like(User::getUsername, kw)
                    .or().like(User::getPhone, kw)
                    .or().like(User::getWechatId, kw)
                    .or().like(User::getEmail, kw));
        }
        if (!StringUtils.isEmpty(platform)) {
            wrapper.like(User::getPlatform, platform);
        }
        wrapper.orderByDesc(User::getCreated);

        IPage<User> userPage = userMapper.selectPage(page, wrapper);

        Page<AdminUserVO> result = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        result.setRecords(userPage.getRecords().stream()
                .map(AdminUserVO::from)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public User getUserDetail(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null || (user.getIsDelete() != null && user.getIsDelete() == 1)) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public void updateUser(String userId, AdminUserUpdateDTO dto, String operator) {
        User user = getUserDetail(userId);
        String beforeJson = JSON.toJSONString(user);

        if (dto.getUsername() != null)    user.setUsername(dto.getUsername());
        if (dto.getPhone() != null)       user.setPhone(dto.getPhone());
        if (dto.getEmail() != null)       user.setEmail(dto.getEmail());
        if (dto.getWechatId() != null)    user.setWechatId(dto.getWechatId());
        if (dto.getDescription() != null)  user.setDescription(dto.getDescription());
        if (dto.getGreeting() != null)     user.setGreeting(dto.getGreeting());
        if (dto.getBizType() != null)     user.setBizType(dto.getBizType());
        if (dto.getBizName() != null)     user.setBizName(dto.getBizName());
        if (dto.getMsgLink() != null)     user.setMsgLink(dto.getMsgLink());
        if (dto.getAddress() != null)     user.setAddress(dto.getAddress());
        if (dto.getPlatform() != null)    user.setPlatform(dto.getPlatform());
        if (dto.getHeadPic() != null)     user.setHeadPic(dto.getHeadPic());
        if (dto.getQr() != null)          user.setQr(dto.getQr());
        if (dto.getStatus() != null)      user.setStatus(dto.getStatus());

        user.setUpdated(new Date());
        userMapper.updateById(user);

        auditLogMapper.insert(AuditLog.builder()
                .action("USER_UPDATE")
                .targetUserId(userId)
                .operator(operator)
                .beforeJson(beforeJson)
                .afterJson(JSON.toJSONString(user))
                .remark("管理员修改用户资料")
                .createdAt(new Date())
                .build());
    }

    @Override
    public void deleteUser(String userId, String operator) {
        User user = getUserDetail(userId);
        String beforeJson = JSON.toJSONString(user);

        user.setIsDelete(1);
        user.setUpdated(new Date());
        userMapper.updateById(user);

        auditLogMapper.insert(AuditLog.builder()
                .action("USER_DELETE")
                .targetUserId(userId)
                .operator(operator)
                .beforeJson(beforeJson)
                .afterJson(null)
                .remark("管理员软删除用户")
                .createdAt(new Date())
                .build());
    }

    @Override
    public Map<String, Integer> platformStats() {
        List<User> users = userMapper.selectList(
                Wrappers.<User>lambdaQuery()
                        .ne(User::getIsDelete, 1));

        Map<String, Integer> stats = users.stream()
                .filter(u -> !StringUtils.isEmpty(u.getPlatform()))
                .collect(Collectors.groupingBy(User::getPlatform,
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)));

        int noPlatform = (int) users.stream()
                .filter(u -> StringUtils.isEmpty(u.getPlatform()))
                .count();
        if (noPlatform > 0) {
            stats.put("未填写", noPlatform);
        }
        return stats;
    }

    @Override
    public IPage<AuditLog> listAuditLogs(Page<AuditLog> page, String targetUserId) {
        LambdaQueryWrapper<AuditLog> wrapper = Wrappers.<AuditLog>lambdaQuery();
        if (!StringUtils.isEmpty(targetUserId)) {
            wrapper.eq(AuditLog::getTargetUserId, targetUserId);
        }
        wrapper.orderByDesc(AuditLog::getCreatedAt);
        return auditLogMapper.selectPage(page, wrapper);
    }
}
