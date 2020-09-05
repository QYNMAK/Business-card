package com.pfh.crm.dashboard.entity.vo;

import com.alibaba.druid.sql.visitor.functions.If;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pfh.crm.dashboard.common.util.ArrayListUtil;
import com.pfh.crm.dashboard.entity.po.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerList {

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
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 县
     */
    private String county;
    /**
     * 地址
     */
    private String address;
    /**
     * 手机号码
     */
    private String phone;

    /**
     * 预览头像
     */
    private String previewImg;

    public static Page<CustomerList> build(IPage<Customer> originPage) {
        Page<CustomerList> pageRst = new Page<>(originPage.getCurrent(),originPage.getSize(),originPage.getTotal());
        if (!CollectionUtils.isEmpty(originPage.getRecords())) {
            pageRst.setRecords(ArrayListUtil.copy(originPage.getRecords(),CustomerList.class));
        }
        return pageRst;
    }
}
