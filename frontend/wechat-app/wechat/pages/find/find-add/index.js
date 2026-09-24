import {
  httpService
} from "../../../providers/httpService";
import {
  customerAdd,
  imgUpload,
  Host,
  customer,
  imgPreview
} from "../../../providers/constants";
import WxValidate from "../../common/WxValidate";
import TelValidate from "../../common/WxValidate";
Page({
  data: {
    region: [],
    regionPickerActive: false,
    regionSelected: '请选择所在区域',
    imgs: [],
    uploadLimit: 10,
    relationUsers: [],
    user: {name:''},
    phoneNums:[""],
    previewImgUrl: imgPreview
  },
  /**
   * 所在区域选择
   * 
   * @param {区域对象} e 
   */
  bindRegionChange: function (e) {
    var selectedRegion = e.detail.value[0] + "-" + e.detail.value[1] + "-" + e.detail.value[2];
    this.setData({
      region: e.detail.value,
      regionPickerActive: true,
      regionSelected: selectedRegion
    })
  },
  /**
   * 图片移除
   * 
   * @param {图片对象} e 
   */
  uploadRemove: function (e) {
    console.log("xxx")
    var imgs = this.data.imgs;
    var index = e.currentTarget.dataset.index;
    console.log("xxx", index)
    imgs.splice(index, 1);
    this.setData({
      imgs: imgs
    });
  },
  /**
   * 关联客户
   */
  relationCustomer: function () {
    wx.navigateTo({
      url: '../../../pages/find/find-relation-customer/index',
    })
  },
  /**
   * 查看关联的所有客户
   */
  relationCustomerAll: function () {
    wx.navigateTo({
      url: '../../../pages/find/find-relationed-user/index',
    })
  },
  /**
   * 选择手机图片或拍照
   * 
   * @param {*} e 
   */
  imgUpload: function (e) {

    var canUpload = this.data.imgs.length < 10;
    if (!canUpload) {
      wx.showToast({
        title: '最多支持10张图片',
        icon: 'none',
        duration: 1500
      })
      return false;
    }
    var canUploadCount = this.data.uploadLimit - this.data.imgs.length;
    let that = this;
    wx.chooseImage({
      count: canUploadCount,
      sizeType: ['original', 'compressed'],
      sourceType: ['album', 'camera'],
      success(res) {
        const tempFilePaths = res.tempFilePaths
        let imgs = that.data.imgs;
        tempFilePaths.filter(file => {
          httpService.fileUploadInternal({
            file: file,
            success: function (params) {
              imgs.push(JSON.parse(params.data).data);
              that.setData({
                imgs: imgs
              })
            }
          });
        })

        // that.setData({
        //   imgs: imgs
        // })
      }
    })
  },

  /**
   * 添加关联
   * 
   * @param {客户} e 
   */
  addRelation: function (e) {

    let users = this.data.relationUsers;
    users.push(e);
      
    users.forEach(user => {
      if (user.name.length > 2) {
        user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
      } else {
        user['showName'] = user.name;
      }
    });      

    this.setData({
      relationUsers: users
    })
  },
deleteRelation: function(id){
  let users = this.data.relationUsers;
  users.forEach(function (item, index, arr) {
    if (item.id = id) {
      arr.splice(index, 1);
    } 
  });
  this.setData({
    relationUsers: users
  })
},
  /**
   * 删除关联用户
   * 
   * @param {关联用户} e 
   */
  relationCustomerItemCancel: function (e) {

    var users = this.data.relationUsers;
    var index = e.currentTarget.dataset.index;
    console.info(index)
    users.splice(index, 1);
    console.info(users)
    this.setData({
      relationUsers: users
    })
  },

  /**
   * 图片预览
   * 
   * @param {图片对象} e 
   */
  imgPreview: function (e) {
    var imgIndex = e.currentTarget.dataset.index;
    var imgs = this.data.imgs;
    var preImgs = new Array(4);
    for (let index = 0; index < imgs.length; index++) {
        preImgs[index] = this.data.previewImgUrl + imgs[index];
    }
   
    wx.previewImage({
      current: preImgs[imgIndex],
      urls: preImgs
    })
  },
  /**
   * 提交表单
   * 
   * @param {表单对象} e 
   * @param {客户信息} customer 
   */
  submitRel: function (e, customer) {
    httpService.post(customerAdd, customer, true).then(res => {
      if (res.success) {
        wx.hideToast();
        wx.reLaunch({
          url: '../../../pages/index/index',
        })
      }
      return false;
    });

  },
  /**
   * 表单提交
   * 
   * @param {表单对象} e 
   */
  formSubmit: function (e) {

    const params = e.detail.value
   debugger
    // 传入表单数据，调用验证方法
    if (!this.WxValidate.checkForm(params)) {

      const error = this.WxValidate.errorList[0]
      wx.showModal({
        content: error.msg,
        showCancel: false,
      })
      return false
    }

    wx.showToast({
      title: '保存中···',
      icon: 'loading',
      duration: 100000
    })

    var relationUserIds = [];
    this.data.relationUsers.filter(item => {
      relationUserIds.push(item.id)
    })

    var customer = e.detail.value;
    customer['relationUserIds'] = relationUserIds;
    customer['imgIds'] = this.data.imgs;
    customer['tels'] = this.data.phoneNums;
    this.submitRel(e, customer);
  },
  onLoad: function () {
    this.initValidate();
    this.telValidate();
    var loadUser = this.loadUser;
    const eventChannel = this.getOpenerEventChannel();
    eventChannel.on('editUser', function (data) {
      loadUser(data.uid);
    })
  },
  loadUser: function (id = "") {
    httpService.get(customer + "/" + id).then(res => {
      if (res.success) {
        console.log('user', res);
        var user = res.data || {};
        console.log('user', user.region.join("-"));
        var imgs = user.imgIds;
        user.relationUsers.forEach(user => {
          if (user.name.length > 2) {
            user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
          } else {
            user['showName'] = user.name;
          }
        });
        this.setData({
          user: user,
          imgs: imgs,
          phoneNums:!user.tels ? ['']:user.tels,
          relationUsers: user.relationUsers,
          region: user.region,
          regionSelected: user.region.length > 0 && user.region[0] ? user.region.join("-") : this.data.regionSelected,
          regionPickerActive: true
        })
      }
    })
  },
  /**
   * 新增手机号
   */
  addPhoneNum:function(e){
    var phoneNums = this.data.phoneNums;
    var telObj = e.currentTarget.dataset;
   var tested = this.TelValidate.checkForm(telObj);

   if (!tested) {

    const error = this.TelValidate.errorList[0]
    wx.showModal({
      content: error.msg,
      showCancel: false,
    })
    return false
  }
    
    phoneNums.push("");
    this.setData({
      phoneNums:phoneNums
    })
  },
  /**
   * 双向绑定电话号码
   * @param {*} e 
   */
  bindPhoneNum:function(e) {
    var index = e.currentTarget.dataset.index;
    var tel = e.detail.value;
    var phoneNums = this.data.phoneNums;
    phoneNums[index]= tel;
    this.setData({
      phoneNums:phoneNums
    })
  },
  /**
   * 删除手机号
   * 
   * @param {*} e 
   */
  removePhoneNum:function(e){
    var index = e.currentTarget.dataset.index;
    var phoneNums = this.data.phoneNums;
    phoneNums.splice(index,1)
    this.setData({
      phoneNums:phoneNums
    })
  },
  /**
   * 初始化验证
   */
  initValidate() {
    let rules = {
      name: {
        required: true,
        maxlength: 20
      }
    }

    let message = {
      name: {
        required: '请输入姓名',
        maxlength: '名字不能超过10个字'
      }
    }
    //实例化当前的验证规则和提示消息
    this.WxValidate = new WxValidate(rules, message);

  },
  /**
   * 初始化验证
   */
  telValidate() {
    let rules = {
      tel: {
        required: true,
        number: true,
        tel: true
      }
    }

    let message = {
     
      tel: {
        required: '请输入手机号',
        tel: '请输入正确的手机号'
      }
    }
    //实例化当前的验证规则和提示消息
    this.TelValidate = new WxValidate(rules, message);

  }

})