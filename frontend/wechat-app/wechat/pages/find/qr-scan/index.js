import {
  userService
} from "../../common/userService";
import {
  qrUser,
  customerAddWithUser
} from "../../../providers/constants";
import {
  httpService
} from "../../../providers/httpService";
Page({
  data:{
    user:{}
  },
  onLoad:function(){
    const eventChannel = this.getOpenerEventChannel();
    var that = this;
    eventChannel.on('scan-add', function (data) {
      console.log(qrUser + data.uid)
      httpService.get(qrUser + data.uid).then(res => {
        if (res.success) {
          that.setData({
            user:res.data
          })
        }
      })
    });
  },
  /**
   * 添加客户
   */
  addCustomer:function(){
    httpService.post(customerAddWithUser+this.data.user.id).then(res => {
      if (res.success) {
       wx.reLaunch({
         url: '../../index/index',
       })
      }
    })
  }
})