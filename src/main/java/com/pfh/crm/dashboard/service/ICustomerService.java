package com.pfh.crm.dashboard.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pfh.crm.dashboard.entity.dto.CustomerDTO;
import com.pfh.crm.dashboard.entity.dto.CustomerQuery;
import com.pfh.crm.dashboard.entity.po.Customer;
import com.pfh.crm.dashboard.entity.vo.CustomerEditDetailVO;
import com.pfh.crm.dashboard.entity.vo.CustomerList;

import java.util.List;

/**
 * 客户管理服务
 *
 */
public interface ICustomerService extends IService<Customer> {
    /**
     * 新增用户
     *
     * <br/>用户在用户组下唯一
     *
     * @param customer 用户dto
     * @return {@link Customer}
     */
    Customer add(CustomerDTO customer);

    /**
     * 新增客户手机号码
     *
     * @param id 客户id
     * @param tels 电话号码
     */
    void insertCustomerTel(String id, List<String> tels);

    /**
     * 新增用户图片，最多10张
     *
     * @param cid 客户id
     * @param imgIds 图片id集合
     */
    void insertCustomerImg(String cid, List<String> imgIds);

    /**
     * 关联用户
     *
     * @param cid 用户id
     * @param relationUserIds 关联客户id
     */
    void insertRelationUser(String cid, List<String> relationUserIds);

    /**
     * 客户名称合法验证，用户名唯一
     *
     * @param customer 客户信息
     */
    void uniqueNameCheck(Customer customer);

    /**
     * 客户分页查询
     *  @param page 分页信息
     * @param queryParams 查询信息
     */
    Page<CustomerList> pageCustomer(Page<Customer> page, CustomerQuery queryParams);

    /**
     * 获取用户详情
     *
     * @param uid 用户id
     * @return {@link CustomerEditDetailVO}
     */
    CustomerEditDetailVO getDetail(String uid);

    /**
     * 用户修改
     *
     * <br/>用户在用户组下唯一
     *
     * @param customer 用户dto
     * @return {@link Customer}
     */
    void update(CustomerDTO customer);

    /**
     * 是否存在
     *
     * @param cid 客户id
     * @param userId 用户id
     * @return true 已存在 false 不存在
     */
    boolean exists(String cid, String userId);

    /**
     * 通过二维码添加客户
     *
     * @param userId 用户id
     */
    void addUserAsCustomer(String userId,String owner);
}
