Page({
  data:{
    relationUsers:[]
  },
  relationCustomerItemCancel:function(e) {
    debugger
    var users = this.data.relationUsers;
    var index = e.currentTarget.dataset.index;
    console.info(index)
    users.splice(index, 1);
    users.forEach(user => {
      if (user.name.length > 2) {
        user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
      } else {
        user['showName'] = user.name;
      }
    });
    this.setData({
      relationUsers:users
    })
  },
  relationCustomer:function(){
    wx.navigateTo({
      url: '../../../pages/find/find-relation-customer/index',
    })
  },
  addRelation:function(e) {

    let users = this.data.relationUsers;
    users.forEach(user => {
      if (user.name.length > 2) {
        user['showName'] = user.name.substring(user.name.length - 2, user.name.length);
      } else {
        user['showName'] = user.name;
      }
    });
    users.push(e);
    this.setData({
      relationUsers:users
    })
  },
  onSaveExitState: function() {
    let pages = getCurrentPages();
    let currPage = null; 
    let prevPage = null; 

    if (pages.length >= 2) {
      currPage = pages[pages.length - 1]; 
      prevPage = pages[pages.length - 2]; 
      console.log(currPage + "，" + prevPage);
    }
  
    if (prevPage) {
      prevPage.addRelation(this.data.relationUsers);
    }
  },
  onLoad:function() {
    let pages = getCurrentPages();
    let currPage = null; //当前页面
    let prevPage = null; //上一个页面

    if (pages.length >= 2) {
      currPage = pages[pages.length - 1]; //最后一个元素为当前页面。
      prevPage = pages[pages.length - 2]; //上一个页面
      console.log(currPage + "，" + prevPage);
    }
  
    if (prevPage) {
      this.setData({
        relationUsers:prevPage.data.relationUsers
      })
  
    }
  }
})