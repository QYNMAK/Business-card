import {
  httpService
} from "../../providers/httpService";
import {
  card,
  cardClear,
  loginOut,
  token
} from "../../providers/constants";

Page({
  data: {
    card: {},
    hasCard: false,
    apiHost: "",
    saving: false
  },

  onShow() {
    this.loadCard();
  },

  loadCard() {
    var that = this;
    httpService.ensureLogin().then(() => {
      httpService.get(card).then(res => {
        if (res.success && res.data) {
          var c = res.data;
          c.showBiz = c.bizName || c.bizType || "";
          that.setData({ card: c, hasCard: c.saved });
        }
      });
    });
    // 显示当前 API 地址
    that.setData({ apiHost: httpService.getHost() });
  },

  /** 编辑名片 */
  toEdit() {
    wx.navigateTo({ url: "/pages/user/setting/setting" });
  },

  /** 跳转二维码页 */
  toQr() {
    wx.switchTab({ url: "/pages/find/qr/index" });
  },

  /** 修改 API 地址 */
  changeApiHost() {
    var that = this;
    wx.showModal({
      title: "修改接口地址",
      editable: true,
      placeholderText: "如 http://localhost:8080 或 https://你的域名",
      content: that.data.apiHost,
      success: res => {
        if (res.confirm && res.content && res.content.length) {
          var h = res.content.replace(/\/+$/, "");
          wx.setStorageSync("apiHost", h);
          wx.showToast({ title: "已保存", icon: "success" });
          that.setData({ apiHost: h });
        }
      }
    });
  },

  /** 清空名片 */
  clearCard() {
    wx.showModal({
      title: "清空名片",
      content: "确认清空已填写的名片信息？",
      success: res => {
        if (res.confirm) {
          var that = this;
          httpService.post(cardClear).then(() => {
            wx.showToast({ title: "已清空", icon: "success" });
            that.loadCard();
          });
        }
      }
    });
  },

  /** 退出登录：清 token 并重新走自动登录 */
  loginOut() {
    wx.removeStorageSync(token);
    wx.showToast({ title: "已退出，重新登录", icon: "none" });
    setTimeout(() => this.loadCard(), 400);
  },

  onShareAppMessage() {
    var c = this.data.card;
    return {
      title: (c.username || "我的名片") + (c.showBiz ? " · " + c.showBiz : ""),
      path: "/pages/find/qr/index"
    };
  }
});
