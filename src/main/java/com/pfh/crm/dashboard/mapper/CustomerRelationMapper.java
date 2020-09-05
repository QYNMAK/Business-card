package com.pfh.crm.dashboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pfh.crm.dashboard.entity.po.CustomerRelation;
import com.pfh.crm.dashboard.entity.vo.CustomerRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CustomerRelationMapper extends BaseMapper<CustomerRelation> {

    /**
     * 查询关联信息
     *
     * @param masterCustomerId 客户id
     * @return 关联用户信息
     */
    @Select("SELECT tb_customer_relation.customer_id as id ,tb_customer.`name`  FROM tb_customer_relation,tb_customer WHERE tb_customer_relation.customer_id = tb_customer.id AND tb_customer_relation.customer_master_id = #{id} ")
    List<CustomerRelationVO> selectRelationList(@Param("id") String masterCustomerId);
}
