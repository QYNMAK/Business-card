package com.pfh.crm.dashboard.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Date;

/**
 * 管理员操作审计日志
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName(value = "tb_audit_log", schema = "crm")
public class AuditLog {

    @TableId(type = IdType.UUID)
    private String id;

    /** 操作类型：USER_UPDATE / USER_DELETE / USER_CREATE */
    private String action;

    /** 被操作用户 id */
    private String targetUserId;

    /** 操作人（API key 前缀或 admin 标识） */
    private String operator;

    /** 操作前数据快照（JSON） */
    private String beforeJson;

    /** 操作后数据快照（JSON） */
    private String afterJson;

    /** 操作说明 */
    private String remark;

    private Date createdAt;
}
