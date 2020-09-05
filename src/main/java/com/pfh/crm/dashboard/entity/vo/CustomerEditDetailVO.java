package com.pfh.crm.dashboard.entity.vo;

import com.google.common.collect.Collections2;
import com.pfh.crm.dashboard.entity.po.Customer;
import com.pfh.crm.dashboard.entity.po.CustomerPic;
import com.pfh.crm.dashboard.entity.po.CustomerTel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerEditDetailVO {
    private String id;
    /**
     * 客户姓名
     */
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
    private List<String> imgIds = new ArrayList<>();
    /**
     * 关联用户
     */
    private List<CustomerRelationVO> relationUsers = new ArrayList<>();
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

    public static CustomerEditDetailVO build(Customer customer, List<CustomerPic> customerPics, List<CustomerRelationVO> relationUsers,List<CustomerTel> tels,  String previewUrl) {

        List<String> imgIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(customerPics)){
            customerPics.forEach(customerPic -> {
                imgIds.add(customerPic.getPicId());

            });
        }



        CustomerEditDetailVO customerDetail = new CustomerEditDetailVO();
        BeanUtils.copyProperties(customer,customerDetail);
        customerDetail.setImgIds(imgIds);
        if (relationUsers != null){
            customerDetail.setRelationUsers(relationUsers);
        }

        List<String> region = new ArrayList<>(3);
        region.add(customer.getProvince());
        region.add(customer.getCity());
        region.add(customer.getCounty());
        customerDetail.setRegion(region);

        if (!CollectionUtils.isEmpty(tels)){
            List<String> telList = new ArrayList<>(tels.size());
            tels.forEach(customerTel -> {
                telList.add(customerTel.getPhoneNum());
            });
            customerDetail.setTels(telList);
        }

        if (customerDetail.getRemark() == null) {
            customerDetail.setRemark("");
        }

        return customerDetail;
    }
}
