package com.pfh.crm.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pfh.crm.dashboard.entity.po.CustomerPic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CustomerPicMapper extends BaseMapper<CustomerPic> {
}
