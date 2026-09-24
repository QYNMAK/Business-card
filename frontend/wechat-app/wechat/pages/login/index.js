import {
  httpService
} from "../../providers/httpService";
import {
  login,
  register,
  token
} from "../../providers/constants";

Page({
  data: {
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  onLoad: function () {
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: function (res) {
              console.log(res.userInfo)
            }
          })
        }
      }
    })
  },

  /**
   * 发起登陆并注册
   * 
   * @param {userinfo} e 
   */
  bindGetUserInfo(e) {
    console.log(e.detail.userInfo)

    wx.login({
      success(res) {
        if (res.code) {
          //发起网络请求
          httpService.get(
            login, {
              code: res.code
            },
            true
          ).then(res => {
            if (res.success) {
              wx.setStorage({
                key: token,
                data: res.data.jwt
              })
              var exists = res.data.register;
              if (!exists) {
                wx.getUserInfo({
                  success: function (userRes) {
                    var userInfo = e.detail.userInfo;
                    httpService.post(register, userInfo, true).then(rres => {
                      if (rres.success) {
                        wx.setStorage({
                          key: token,
                          data: rres.data.jwt
                        })
                      }
                      wx.setStorage({
                        key: "user",
                        data: userInfo
                      })
                      wx.reLaunch({
                        url: '../../pages/index/index'
                      })
                    })
                  }
                })
              }
              wx.reLaunch({
                url: '../../pages/index/index'
              })
            }
          })
        } else {
          console.log('登录失败！' + res.errMsg)
        }
      }
    })
  }
})