package com.pfh.crm.dashboard.entity.dto;

import com.pfh.crm.dashboard.entity.po.Customer;
import lombok.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 客户DTO
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CustomerDTO {
    private String id;
    /**
     * 客户姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;
    /**
     * 性别
     */
    private int sex;
    /**
     * 省市县
     */
    private List<String> region;
    /**
     * 图片id
     */
    private List<String> imgIds;
    /**
     * 关联用户
     */
    private List<String> relationUserIds;
    /**
     * 手机号码
     */
    private List<String> tels;
    /**
     * 地址
     */
    private String address;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 备注
     */
    private String remark;

    /**
     * the owner
     */
    private String userId;
    public Customer getCustomer() {

        Customer customer =  new Customer();
        BeanUtils.copyProperties(this,customer);
        if (!CollectionUtils.isEmpty(this.getRegion()) && this.getRegion().size() >=3){
            customer.setProvince(this.getRegion().get(0));
            customer.setCity(this.getRegion().get(1));
            customer.setCounty(this.getRegion().get(2));
        }

        return customer;
    }

    public boolean isModify(){
        return Strings.isNotBlank(this.getId());
    }
}
