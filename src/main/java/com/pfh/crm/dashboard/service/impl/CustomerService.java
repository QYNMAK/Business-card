package com.pfh.crm.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pfh.crm.dashboard.common.config.AliyunOSSConfig;
import com.pfh.crm.dashboard.common.constant.CustomerConstant;
import com.pfh.crm.dashboard.common.exception.BusinessException;
import com.pfh.crm.dashboard.common.util.AliyunOSSUtils;
import com.pfh.crm.dashboard.common.util.ImgUtils;
import com.pfh.crm.dashboard.entity.dto.CustomerDTO;
import com.pfh.crm.dashboard.entity.dto.CustomerQuery;
import com.pfh.crm.dashboard.entity.po.*;
import com.pfh.crm.dashboard.entity.vo.CustomerEditDetailVO;
import com.pfh.crm.dashboard.entity.vo.CustomerList;
import com.pfh.crm.dashboard.entity.vo.CustomerRelationVO;
import com.pfh.crm.dashboard.mapper.CustomerMapper;
import com.pfh.crm.dashboard.mapper.CustomerPicMapper;
import com.pfh.crm.dashboard.mapper.CustomerRelationMapper;
import com.pfh.crm.dashboard.mapper.CustomerTelMapper;
import com.pfh.crm.dashboard.service.ICustomerService;
import com.pfh.crm.dashboard.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户管理实现
 *
 * @author pengfh
 * @version 1.0.0
 */
@Service
@Transactional(rollbackFor = {Exception.class,RuntimeException.class})
@AllArgsConstructor
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> implements ICustomerService {

    private AliyunOSSConfig aliyunOSSConfig;

    /**
     * 客户
     */
    private CustomerMapper customerMapper;
    /**
     * 客户图片
     */
    private CustomerPicMapper customerPicMapper;
    /**
     * 客户关联
     */
    private CustomerRelationMapper customerRelationMapper;
    /**
     * 手机号
     */
    private CustomerTelMapper customerTelMapper;
    /**
     * 用户服务
     */
    private IUserService userService;

    @Override
    public Customer add(CustomerDTO customerDTO) {

        Customer customer = customerDTO.getCustomer();
        List<String> relationUserIds = customerDTO.getRelationUserIds();
        List<String> imgIds = customerDTO.getImgIds();
        customer.setUserId(customerDTO.getUserId());
//        uniqueNameCheck(customer);

        customerMapper.insert(customer);
        insertRelationUser(customer.getId(), relationUserIds);
        insertCustomerImg(customer.getId(), imgIds);
        insertCustomerTel(customer.getId(), customerDTO.getTels());
        return customer;
    }

    @Override
    public void insertCustomerTel(String cid, List<String> tels) {
        if (CollectionUtils.isEmpty(tels)) {
            return;
        }

        LambdaQueryWrapper<CustomerTel> deleteWrapper = Wrappers.<CustomerTel>lambdaQuery().eq(CustomerTel::getCustomerId, cid);
        customerTelMapper.delete(deleteWrapper);
        tels.forEach(t -> {
            CustomerTel customerTel = CustomerTel.builder().customerId(cid).phoneNum(t).build();
            customerTelMapper.insert(customerTel);
        });

    }


    @Override
    public void insertCustomerImg(String cid, List<String> imgIds) {
        if (CollectionUtils.isEmpty(imgIds)) {
            return;
        }

        LambdaQueryWrapper<CustomerPic> deleteWrapper = Wrappers.<CustomerPic>lambdaQuery().eq(CustomerPic::getCustomerId, cid);
        customerPicMapper.delete(deleteWrapper);

        // 上传图片数量检查
        passLimitCheck(cid, imgIds);

        imgIds.forEach(imgId -> {
            if (!StringUtils.isEmpty(imgId)) {
                System.out.println(imgId.length());
                CustomerPic customerPic = new CustomerPic();
                customerPic.setCustomerId(cid);
                customerPic.setPicId(imgId);
                customerPicMapper.insert(customerPic);
            }
        });

    }


    @Override
    public void insertRelationUser(String cid, List<String> relationUserIds) {

        LambdaQueryWrapper<CustomerRelation> deleteWrapper = Wrappers.<CustomerRelation>lambdaQuery().eq(CustomerRelation::getCustomerMasterId, cid);
        customerRelationMapper.delete(deleteWrapper);

        if (CollectionUtils.isEmpty(relationUserIds)) {
            return;
        }

        relationUserIds.forEach(relationUserId -> {
            CustomerRelation relation = new CustomerRelation();
            relation.setCustomerId(relationUserId);
            relation.setCustomerMasterId(cid);
            customerRelationMapper.insert(relation);
        });
    }

    /**
     * 图片上限验证，每用户最多10张图片
     *
     * @param cid    用户id
     * @param imgIds 图片id
     */
    private void passLimitCheck(String cid, List<String> imgIds) {
        if (imgIds.size() > CustomerConstant.UPLOAD_LIMIT) {
            throw new BusinessException("用户最多只能上传10张图片");
        }

        LambdaQueryWrapper<CustomerPic> queryWrapper = Wrappers.<CustomerPic>lambdaQuery().eq(CustomerPic::getCustomerId, cid);
        Integer uploadedPicNum = customerPicMapper.selectCount(queryWrapper);

        Integer securityLimit = CustomerConstant.UPLOAD_LIMIT - uploadedPicNum;

        if (securityLimit < imgIds.size()) {
            throw new BusinessException(String.format("用户最多只能上传%s张图片", securityLimit));
        }
    }

    @Override
    public void uniqueNameCheck(Customer customer) {

        LambdaQueryWrapper<Customer> queryWrapper = Wrappers.<Customer>lambdaQuery()
                .ne(customer.getId() != null, Customer::getId, customer.getId())
                .eq(Customer::getName, customer.getName());
        boolean nameExists = customerMapper.selectCount(queryWrapper) > 0;

        if (nameExists) {
            throw new BusinessException("客户名已存在");
        }
    }

    @Override
    public Page<CustomerList> pageCustomer(Page<Customer> page, CustomerQuery queryParams) {


        LambdaQueryWrapper<Customer> queryWrapper = Wrappers.<Customer>lambdaQuery()
                .eq(Customer::getUserId, queryParams.getUserId())
                .ge(queryParams.getMinAge() != null, Customer::getAge, queryParams.getMinAge())
                .le(queryParams.getMaxAge() != null, Customer::getAge, queryParams.getMaxAge())
                .like(!StringUtils.isEmpty(queryParams.getKeywords()), Customer::getName, queryParams.getKeywords())
                .or()
                .like(!StringUtils.isEmpty(queryParams.getKeywords()), Customer::getPhone, queryParams.getKeywords())
                .or()
                .like(!StringUtils.isEmpty(queryParams.getKeywords()), Customer::getAddress, queryParams.getKeywords());


        String[] desc = page.descs();
        String[] newDesc = null;
        if (desc != null) {
            newDesc = new String[desc.length + 1];
            System.arraycopy(desc, 0, newDesc, 0, desc.length);
            newDesc[desc.length] = "created";
        } else {
            newDesc = new String[1];
            newDesc[0] = "created";
        }
        page.setDesc(newDesc);
        page.setSize(500L);
        IPage<Customer> pageRst = page(page, queryWrapper);

        Page<CustomerList> rstPage = CustomerList.build(pageRst);

        rstPage.getRecords().forEach(customer -> {
            LambdaQueryWrapper<CustomerPic> picQW = Wrappers.<CustomerPic>lambdaQuery().eq(CustomerPic::getCustomerId, customer.getId());
            List<CustomerPic> customerPics = customerPicMapper.selectList(picQW);
            if (!CollectionUtils.isEmpty(customerPics)) {
                customer.setPreviewImg(aliyunOSSConfig.getImgPreview() + customerPics.get(0).getPicId());
            }
        });

        return rstPage;

    }

    @Override
    public CustomerEditDetailVO getDetail(String uid) {

        Customer customer = customerMapper.selectById(uid);
        if (customer == null) {
            return null;
        }

        LambdaQueryWrapper<CustomerPic> picQW = Wrappers.<CustomerPic>lambdaQuery().eq(CustomerPic::getCustomerId, customer.getId());
        List<CustomerPic> customerPics = customerPicMapper.selectList(picQW);


        List<CustomerRelationVO> customerRelations = customerRelationMapper.selectRelationList(customer.getId());

        LambdaQueryWrapper<CustomerTel> telQW = Wrappers.<CustomerTel>lambdaQuery().eq(CustomerTel::getCustomerId, customer.getId());
        List<CustomerTel> tels = customerTelMapper.selectList(telQW);


        return CustomerEditDetailVO.build(customer, customerPics, customerRelations, tels, aliyunOSSConfig.getImgPreview());

    }

    @Override
    public void update(CustomerDTO customerDTO) {
        Customer customer = customerDTO.getCustomer();

        Customer oldCustomer = customerMapper.selectById(customer.getId());
        if (oldCustomer == null) {
            throw new BusinessException("用户不存在");
        }

        BeanUtils.copyProperties(customer, oldCustomer, "userId", "created", "isDelete");

        List<String> relationUserIds = customerDTO.getRelationUserIds();
        List<String> imgIds = customerDTO.getImgIds();

//        uniqueNameCheck(oldCustomer);
        customerMapper.updateById(oldCustomer);
        insertRelationUser(oldCustomer.getId(), relationUserIds);
        insertCustomerImg(oldCustomer.getId(), imgIds);
        insertCustomerTel(customer.getId(), customerDTO.getTels());
    }

    @Override
    public boolean exists(String cid, String userId) {
        LambdaQueryWrapper<Customer> queryWrapper = Wrappers.<Customer>lambdaQuery().eq(Customer::getId, cid).eq(Customer::getUserId, userId);
        return count(queryWrapper) > 0;
    }

    @Override
    public void addUserAsCustomer(String userId, String owner) {
        User user = userService.getById(userId);
        List<String> imgList = new ArrayList<>(1);

        //读取指定的二维码文件
        try {
            BufferedImage bufferedImage = ImageIO.read(new URL(user.getHeadPic()));
            ByteArrayOutputStream byteArrayOutputStream = ImgUtils.image2Stream(bufferedImage);
            String imgUrl = AliyunOSSUtils.uploadImg(byteArrayOutputStream.toByteArray());
            imgList.add(imgUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomerDTO customerDTO = CustomerDTO.builder().name(user.getUsername()).imgIds(imgList).userId(owner).build();
        this.add(customerDTO);

    }
}
