import {
  httpService
} from "../../../providers/httpService";
import {
  card,
  qrCreate,
  cardClear
} from "../../../providers/constants";
const app = getApp();
Page({
  data: {
    card: {},
    hasCard: false,
    qrUrl: "",
    saving: false,
    loadError: ""
  },
  onLoad() {
    this.refresh();
  },
  onShow() {
    // 从编辑页返回时刷新
    if (this._loaded) this.refresh();
  },
  refresh() {
    this._loaded = true;
    var that = this;
    httpService.ensureLogin().then(() => {
      that.loadCard();
      that.createQr();
    });
  },
  // 拉取名片信息（CardVO）
  loadCard() {
    var that = this;
    httpService.get(card).then(res => {
      if (res.success && res.data) {
        var c = res.data;
        // 组装展示用字段
        c.showBiz = c.bizName || c.bizType || "";
        c.title = c.bizType && c.bizName ? (c.bizType + " · " + c.bizName) : (c.bizType || c.bizName || "");
        c.contacts = [];
        if (c.phone) c.contacts.push({ label: "电话", value: c.phone, type: "phone" });
        if (c.email) c.contacts.push({ label: "邮箱", value: c.email, type: "email" });
        if (c.wechatId) c.contacts.push({ label: "微信", value: c.wechatId, type: "copy" });
        if (c.msgLink) c.contacts.push({ label: "消息", value: c.msgLink, type: "link" });
        c.addressFull = [c.platform, c.address].filter(Boolean).join(" · ");
        that.setData({ card: c, hasCard: c.saved, loadError: "" });
      } else {
        that.setData({ loadError: res.message || "加载失败" });
      }
    }).catch(() => {
      that.setData({ loadError: "网络异常，请检查后端是否运行" });
    });
  },
  // 生成带 logo 的名片二维码
  createQr() {
    var that = this;
    httpService.get(qrCreate).then(res => {
      if (res.success && res.data && res.data.qrUrl) {
        that.setData({ qrUrl: res.data.qrUrl });
      }
    }).catch(() => {});
  },
  // 复制联系方式
  copyField(e) {
    var v = e.currentTarget.dataset.value;
    var t = e.currentTarget.dataset.type;
    if (!v) return;
    if (t === "phone") {
      wx.makePhoneCall({ phoneNumber: v, fail: () => {} });
      return;
    }
    if (t === "email") {
      // 邮箱也走复制
    }
    wx.setClipboardData({
      data: v,
      success: () => wx.showToast({ title: "已复制", icon: "success" })
    });
  },
  // 打开消息链接（webview 兜底：复制链接）
  openLink(e) {
    var url = e.currentTarget.dataset.value;
    wx.setClipboardData({ data: url, success: () => wx.showToast({ title: "链接已复制", icon: "none" }) });
  },
  // 保存二维码到相册
  saveQr() {
    if (!this.data.qrUrl) {
      wx.showToast({ title: "二维码未生成", icon: "none" });
      return;
    }
    var that = this;
    that.setData({ saving: true });
    wx.downloadFile({
      url: this.data.qrUrl,
      success: res => {
        wx.saveImageToPhotosAlbum({
          filePath: res.tempFilePath,
          success: () => wx.showToast({ title: "已保存到相册", icon: "success" }),
          fail: () => wx.showToast({ title: "保存失败，请在设置中允许相册权限", icon: "none" })
        });
      },
      fail: () => wx.showToast({ title: "下载失败", icon: "none" })
    }).finally(() => that.setData({ saving: false }));
  },
  // 查看大图
  previewQr() {
    if (this.data.qrUrl) wx.previewImage({ urls: [this.data.qrUrl] });
  },
  // 去编辑
  toEdit() {
    wx.navigateTo({ url: "/pages/user/setting/setting" });
  },
  // 清空名片
  clearCard() {
    wx.showModal({
      title: "清空名片",
      content: "确认清空已填写的名片信息？",
      success: res => {
        if (res.confirm) {
          var that = this;
          httpService.post(cardClear).then(() => {
            wx.showToast({ title: "已清空", icon: "success" });
            that.refresh();
          });
        }
      }
    });
  },
  // 转发名片给好友
  onShareAppMessage() {
    var c = this.data.card;
    return {
      title: (c.username || "我的名片") + (c.showBiz ? " · " + c.showBiz : ""),
      path: "/pages/find/qr/index"
    };
  }
});
