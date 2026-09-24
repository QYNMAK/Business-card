Page({
  data: {
    card: {},
    hasCard: false,
    copyMsg: ""
  },

  onLoad() {
    var that = this;
    var eventChannel = this.getOpenerEventChannel();
    eventChannel.on("cardView", function (data) {
      var c = data.card || {};
      // 组装展示字段
      c.showBiz = c.bizName || c.bizType || "";
      c.title = c.bizType && c.bizName ? (c.bizType + " · " + c.bizName) : (c.bizType || c.bizName || "");
      c.contacts = [];
      if (c.phone) c.contacts.push({ label: "电话", value: c.phone, type: "phone" });
      if (c.email) c.contacts.push({ label: "邮箱", value: c.email, type: "email" });
      if (c.wechatId) c.contacts.push({ label: "微信", value: c.wechatId, type: "copy" });
      if (c.msgLink) c.contacts.push({ label: "消息", value: c.msgLink, type: "link" });
      c.addressFull = [c.platform, c.address].filter(Boolean).join(" · ");
      that.setData({ card: c, hasCard: true });
    });
  },

  /** 复制 / 拨打 */
  copyField(e) {
    var v = e.currentTarget.dataset.value;
    var t = e.currentTarget.dataset.type;
    if (!v) return;
    if (t === "phone") {
      wx.makePhoneCall({ phoneNumber: v, fail: () => {} });
      return;
    }
    if (t === "link") {
      wx.setClipboardData({ data: v, success: () => wx.showToast({ title: "链接已复制", icon: "none" }) });
      return;
    }
    wx.setClipboardData({
      data: v,
      success: () => wx.showToast({ title: "已复制", icon: "success" })
    });
  },

  onShareAppMessage() {
    var c = this.data.card;
    return {
      title: (c.username || "名片") + (c.showBiz ? " · " + c.showBiz : ""),
      path: "/pages/index/detail/index"
    };
  }
});
