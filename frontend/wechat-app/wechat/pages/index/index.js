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
    listMain: [],
    allCards: [],
    keywords: "",
    inputShowed: false
  },

  onShow() {
    this.loadLocal();
  },

  /** 从本地名片夹读取浏览过的人 */
  loadLocal() {
    var cards = wx.getStorageSync(cacheHistory) || [];
    // 补 showName / 展示字段
    cards.forEach(u => {
      u.showName = u.username || "未知";
      u.biz = u.bizName || u.bizType || "";
      u.addressFull = [u.platform, u.address].filter(Boolean).join(" · ");
    });
    this.setData({ allCards: cards, listMain: cards });
  },

  /** 搜索过滤（确认提交） */
  searchSubmit(e) {
    var kw = (e.detail.value || "").trim();
    this.setData({ keywords: kw });
    this.filter(kw);
  },
  /** 搜索过滤（边输入边过滤） */
  filterLive(e) {
    var kw = (e.detail.value || "").trim();
    this.setData({ keywords: kw });
    this.filter(kw);
  },
  hideInput() {
    this.setData({ inputShowed: false });
  },
  showInput() {
    this.setData({ inputShowed: true });
  },
  filter(kw) {
    if (!kw) { this.setData({ listMain: this.data.allCards }); return; }
    var r = this.data.allCards.filter(u => {
      var hay = (u.username + (u.biz || "") + (u.phone || "") + (u.platform || "") + (u.wechatId || "")).toLowerCase();
      return hay.indexOf(kw.toLowerCase()) >= 0;
    });
    this.setData({ listMain: r });
  },

  /** 扫一扫看名片 */
  scan() {
    var that = this;
    wx.scanCode({
      success(res) {
        var jid = res.result;
        wx.showLoading({ title: "读取名片…" });
        httpService.get(qrUser + jid, {}, true).then(r => {
          wx.hideLoading();
          if (r.success && r.data) {
            // 存进本地名片夹
            var cards = wx.getStorageSync(cacheHistory) || [];
            var idx = -1;
            cards.forEach((u, i) => { if (u.id === r.data.id) idx = i; });
            if (idx >= 0) cards[idx] = r.data; else cards.push(r.data);
            wx.setStorageSync(cacheHistory, cards);
            that.loadLocal();
            // 直接展示该名片
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
      }
    });
  },

  /** 查看名片详情 */
  userDetail(e) {
    var u = e.currentTarget.dataset.card;
    wx.navigateTo({
      url: "/pages/index/detail/index",
      success: res => res.eventChannel.emit("cardView", { card: u })
    });
  },

  onShareAppMessage() {
    return { title: "商家名片", path: "/pages/find/qr/index" };
  }
});
