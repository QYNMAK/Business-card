import {
  httpService
} from "../../../providers/httpService";
import {
  customerPage,
  imgUpload
} from "../../../providers/constants";
const app = getApp()

Page({
  /** 
   * 页面的初始数据 
   */
  data: {
    isActive: null,
    listMain: [],
    listTitles: [],
    fixedTitle: null,
    toView: 'inToView0',
    oHeight: [],
    scroolHeight: 0,
    inputShowed: false, //初始文本框不显示内容
    searchActive: false
  },
  // 处理数据格式，及获取分组高度
  getUsers: function (params = {}) {
    var that = this;
    var users = [];
    httpService.get(customerPage, params,false).then(res => {
      
      if (res.success) {
        users = res.data.records;
        users.forEach(user => {
          if (user.name.length > 2) {
            user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
          } else {
            user['showName'] = user.name;
          }
          user['relation'] = 0;
        });
        that.setData({
          listMain: users
        })
      }
    })
  }
  ,
  onLoad: function (options) {
    var that = this;
    that.getUsers();
  },
  // 使文本框进入可编辑状态
  showInput: function () {
    this.setData({
      inputShowed: true //设置文本框可以输入内容
    });
  },
  // 取消搜索
  hideInput: function () {
    this.setData({
      inputShowed: false
    });
  },
  relationUser:function(e) {
    let id = e.currentTarget.dataset.id;
    let name = e.currentTarget.dataset.name;
    let index = e.currentTarget.dataset.index;
    var value = 'listMain['+index+'].relation';
    var changeRelation = this.data.listMain[index].relation == 1?0:1;
    this.setData({
      [value]:changeRelation
    })

    let pages = getCurrentPages();
    let currPage = null; 
    let prevPage = null; 

    if (pages.length >= 2) {
      currPage = pages[pages.length - 1]; 
      prevPage = pages[pages.length - 2]; 
      console.log(currPage + "，" + prevPage);
    }
    let user = {};
    user.id = id;
    user.name = this.data.listMain[index].showName;
    user.previewImg =  this.data.listMain[index].previewImg;
      if (user.name.length > 2) {
        user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
      } else {
        user['showName'] = user.name;
      }


    if (prevPage) {
      if(changeRelation == 1) {
        prevPage.addRelation(user);
      } else {
        prevPage.deleteRelation(user.id);
      }
     
    }
  }
})