import {
  httpService
} from "../../../providers/httpService";
import {
  card,
  cardUpdate,
  imgPreview
} from "../../../providers/constants";

Page({
  data: {
    card: {},
    // 预览用（本地临时路径或后端返回的完整 URL）
    headPreview: "",
    qrPreview: "",
    platformList: ["微信", "小红书", "抖音", "快手", "B站", "淘宝", "其他"],
    platformIndex: -1,
    saving: false
  },

  onLoad() {
    var that = this;
    httpService.ensureLogin().then(() => {
      httpService.get(card).then(res => {
        if (res.success && res.data) {
          var c = res.data;
          var idx = that.data.platformList.indexOf(c.platform);
          that.setData({
            card: c,
            headPreview: c.headPic || "",
            qrPreview: c.qr || "",
            platformIndex: idx >= 0 ? idx : -1
          });
        }
      });
    });
  },

  /** 文本字段双向绑定 */
  bindInput(e) {
    var name = e.currentTarget.dataset.name;
    this.setData({ ['card.' + name]: e.detail.value });
  },

  /** 选择平台 */
  bindPlatformChange(e) {
    var idx = e.detail.value;
    this.setData({
      platformIndex: idx,
      'card.platform': idx >= 0 ? this.data.platformList[idx] : ""
    });
  },

  /** 上传图片（头像 / 微信二维码）→ OSS 相对路径 */
  uploadImage(kind) {
    var that = this;
    var c = this.data.card;
    wx.chooseImage({
      count: 1,
      sizeType: ["compressed"],
      sourceType: ["album", "camera"],
      success(res) {
        var filePath = res.tempFilePaths[0];
        // 本地预览
        if (kind === "head") that.setData({ headPreview: filePath });
        else that.setData({ qrPreview: filePath });

        wx.showLoading({ title: "上传中…" });
        httpService.fileUploadInternal({
          file: filePath,
          success(params) {
            wx.hideLoading();
            var body = JSON.parse(params.data);
            if (body.success && body.data) {
              // 存 OSS 相对路径，提交时带上；预览用本地临时图
              if (kind === "head") that.setData({ 'card.headPic': body.data });
              else that.setData({ 'card.qr': body.data });
              wx.showToast({ title: "上传成功", icon: "success" });
            } else {
              wx.showToast({ title: "上传失败", icon: "none" });
            }
          },
          fail() {
            wx.hideLoading();
            wx.showToast({ title: "上传失败（本地无 OSS 属正常）", icon: "none", duration: 2000 });
          }
        });
      }
    });
  },

  /** 移除图片 */
  removeImage(kind) {
    if (kind === "head") this.setData({ headPreview: "", "card.headPic": "" });
    else this.setData({ qrPreview: "", "card.qr": "" });
  },

  /** 保存名片 */
  saveCard() {
    var c = this.data.card;
    if (c.email && c.email.trim() && !/^[\w.-]+@[\w.-]+\.\w+$/.test(c.email.trim())) {
      wx.showToast({ title: "邮箱格式不正确", icon: "none" });
      return;
    }
    var payload = {
      username: c.username,
      bizType: c.bizType,
      bizName: c.bizName,
      description: c.description,
      greeting: c.greeting,
      phone: c.phone,
      wechatId: c.wechatId,
      email: c.email,
      msgLink: c.msgLink,
      address: c.address,
      platform: c.platform,
      headPic: c.headPic || null,
      qr: c.qr || null
    };
    var that = this;
    that.setData({ saving: true });
    httpService.post(cardUpdate, payload, true).then(res => {
      that.setData({ saving: false });
      if (res.success) {
        wx.showToast({ title: "已保存", icon: "success" });
        // 用后端返回的完整 URL 更新预览
        if (res.data) {
          if (res.data.headPic) that.setData({ headPreview: res.data.headPic });
          if (res.data.qr) that.setData({ qrPreview: res.data.qr });
        }
        setTimeout(() => wx.navigateBack(), 800);
      } else {
        wx.showToast({ title: res.message || "保存失败", icon: "none" });
      }
    }).catch(() => {
      that.setData({ saving: false });
      wx.showToast({ title: "保存失败", icon: "none" });
    });
  }
});
