package com.pfh.crm.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pfh.crm.dashboard.entity.po.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {
}
