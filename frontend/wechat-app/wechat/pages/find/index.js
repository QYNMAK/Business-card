import {
  httpService
} from "../../providers/httpService";
import {
  qrUser,
  cacheHistory
} from "../../providers/constants";

const app = getApp();

Page({
  data: {
    recent: []
  },

  onShow() {
    this.loadRecent();
  },

  /** 读最近浏览的名片（与联系人共用名片夹） */
  loadRecent() {
    var cards = wx.getStorageSync(cacheHistory) || [];
    cards = cards.slice(-6).reverse();
    cards.forEach(u => {
      u.showName = u.username || "未知";
      u.biz = u.bizName || u.bizType || "";
      u.addressFull = [u.platform, u.address].filter(Boolean).join(" · ");
    });
    this.setData({ recent: cards });
  },

  /** 扫一扫查看他人名片 */
  qrScan() {
    var that = this;
    wx.scanCode({
      onlyFromCamera: true,
      success(res) {
        var jid = res.result;
        wx.showLoading({ title: "读取名片…" });
        httpService.get(qrUser + jid, {}, true).then(r => {
          wx.hideLoading();
          if (r.success && r.data) {
            // 存名片夹
            var cards = wx.getStorageSync(cacheHistory) || [];
            var idx = -1;
            cards.forEach((u, i) => { if (u.id === r.data.id) idx = i; });
            if (idx >= 0) cards[idx] = r.data; else cards.push(r.data);
            wx.setStorageSync(cacheHistory, cards);
            // 看详情
            wx.navigateTo({
              url: "/pages/index/detail/index",
              success: rr => rr.eventChannel.emit("cardView", { card: r.data })
            });
          } else {
            wx.showToast({ title: "未识别到名片", icon: "none" });
          }
        }).catch(() => {
          wx.hideLoading();
          wx.showToast({ title: "网络异常", icon: "none" });
        });
      },
      fail: () => {}
    });
  },

  /** 跳我的名片 */
  toMyCard() {
    wx.switchTab({ url: "/pages/find/qr/index" });
  },

  /** 查看最近名片 */
  viewCard(e) {
    var u = e.currentTarget.dataset.card;
    wx.navigateTo({
      url: "/pages/index/detail/index",
      success: res => res.eventChannel.emit("cardView", { card: u })
    });
  },

  onShareAppMessage() {
    return { title: "扫一扫查看名片", path: "/pages/find/qr/index" };
  }
});
