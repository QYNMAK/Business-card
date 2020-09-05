package com.pfh.crm.dashboard.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pfh.crm.dashboard.common.annotaition.SelfLimiter;
import com.pfh.crm.dashboard.common.config.GlobalUserCache;
import com.pfh.crm.dashboard.common.config.QrHolder;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.model.LoginUserCache;
import com.pfh.crm.dashboard.common.model.Result;
import com.pfh.crm.dashboard.entity.dto.CustomerDTO;
import com.pfh.crm.dashboard.entity.dto.CustomerQuery;
import com.pfh.crm.dashboard.entity.po.Customer;
import com.pfh.crm.dashboard.entity.vo.CustomerEditDetailVO;
import com.pfh.crm.dashboard.entity.vo.CustomerList;
import com.pfh.crm.dashboard.service.ICustomerService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequestMapping("customer")
@RestController
@AllArgsConstructor
public class CustomerController {

    private ICustomerService customerService;

    @SelfLimiter
    @PostMapping("add")
    public Result add(@Validated @RequestBody CustomerDTO customer){

        LoginUserCache loginUserCache = GlobalUserCache.get();
        customer.setUserId(loginUserCache.getUserId());
       if (customer.isModify()) {
           customerService.update(customer);
       } else {
           customerService.add(customer);
       }


        return Result.ok();
    }


    /**
     * 通过二维码识别添加客户
     *
     * @param userId 用户id
     */
    @SelfLimiter
    @PostMapping("/qr/add/{userId}")
    public Result add(@PathVariable("userId") String userId){
        LoginUserCache loginUserCache = GlobalUserCache.get();
        if (loginUserCache.getUserId().equals(userId)) {
            throw new BusinessException("不能添加自己为好友");
        }

        customerService.addUserAsCustomer(userId,loginUserCache.getUserId());

        return Result.ok();
    }

    @SelfLimiter
    @GetMapping
    public Result<Page<CustomerList>> getCustomer(Page<Customer> page, CustomerQuery queryParams){
        LoginUserCache loginUserCache = GlobalUserCache.get();
        queryParams.setUserId(loginUserCache.getUserId());
        Page<CustomerList> customerListPage = customerService.pageCustomer(page, queryParams);
        return Result.ok(customerListPage);
    }

    @SelfLimiter
    @GetMapping("/{id}")
    public Result<CustomerEditDetailVO> getDetail(@PathVariable("id") String uid){
        CustomerEditDetailVO userDetail = customerService.getDetail(uid);
        return Result.ok(userDetail);
    }
}
