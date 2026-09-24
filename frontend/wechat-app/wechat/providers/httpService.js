import {
  Host,
  login,
  devLogin,
  register,
  imgUpload,
  token
} from "./constants.js";

// 解析 Host：优先 storage.apiHost（“我的”页可改），否则用默认 Host
function getHost() {
  try {
    var h = wx.getStorageSync('apiHost');
    if (h && h.length) { h = h.replace(/\/+$/, ''); return h; }
  } catch (e) {}
  return Host;
}

function handleError(error) {
  var errorCode = error.statusCode;
  if (errorCode == 401) {
    loginTo();
  } else if (errorCode == 500) {
    if (wx.getStorageSync(token) == "") {
      loginTo();
    } else {
      wx.showToast({ title: error.data && error.data.message + "", icon: "none", duration: 3000 });
    }
  } else if (errorCode == 400) {
    // 管理端 Key 错误等业务错误：静默，避免刷屏
  } else {
    wx.showLoading({ title: error.data && error.data.message });
    setTimeout(function () { wx.hideLoading(); }, 2000);
  }
}

function header() {
  return { Authorization: wx.getStorageSync(token) };
}

function wxPromisify(fn) {
  return function (obj = {}) {
    return new Promise((resolve, reject) => {
      obj.success = function (result) {
        // 后端统一 Result<T>：{ success, message, data }
        if (result.data && result.data.success) {
          resolve(result.data);
        } else {
          // 业务失败也 resolve，交给页面判断，避免误弹登录
          resolve(result.data || {});
        }
      };
      obj.fail = function (error) {
        handleError(error);
        reject(error);
      };
      fn(obj);
    });
  };
}

Promise.prototype.finally = function (callback) {
  let P = this.constructor;
  return this.then(
    value => P.resolve(callback()).then(() => value),
    reason => P.resolve(callback()).then(() => { throw reason; })
  );
};

// 自动登录兜底：没有 token 时，本地走 dev-login（绕过微信），生产走 wx.login
let _loginPromise = null;
function ensureLogin() {
  if (wx.getStorageSync(token)) return Promise.resolve();
  if (_loginPromise) return _loginPromise;
  _loginPromise = new Promise((resolve) => {
    var isLocal = /^http:\/\/(localhost|127\.0\.0\.1|10\.|192\.168\.)/.test(getHost());
    if (isLocal) {
      // 开发模式：dev-login 直接换 JWT，不弹微信授权
      wx.request({
        url: getHost() + devLogin + "?unionId=wx-local-dev&username=开发者",
        method: "GET",
        success: res => {
          _loginPromise = null;
          if (res.data && res.data.success && res.data.data && res.data.data.jwt) {
            wx.setStorageSync(token, res.data.data.jwt);
            resolve(res.data.data);
          } else resolve();
        },
        fail: () => { _loginPromise = null; resolve(); }
      });
    } else {
      // 生产模式：wx.login 换 code -> /user/login
      wx.login({
        success: lr => {
          if (lr.code) {
            wx.request({
              url: getHost() + login + "?code=" + encodeURIComponent(lr.code),
              method: "GET",
              success: res => {
                _loginPromise = null;
                if (res.data && res.data.success && res.data.data && res.data.data.jwt) {
                  wx.setStorageSync(token, res.data.data.jwt);
                  resolve(res.data.data);
                } else resolve();
              },
              fail: () => { _loginPromise = null; resolve(); }
            });
          } else { _loginPromise = null; resolve(); }
        },
        fail: () => { _loginPromise = null; resolve(); }
      });
    }
  });
  return _loginPromise;
}

function loginTo() {
  wx.navigateTo({ url: '/pages/login/index' });
}

function Post(url, paramObj, ischeck = false) {
  var getRequest = wxPromisify(wx.request);
  if (!ischeck) { ensureLogin(); }
  return getRequest({ url: getHost() + url, data: paramObj, header: header(), method: "POST" });
}

function Get(url, paramObj, ischeck = false) {
  var getRequest = wxPromisify(wx.request);
  if (!ischeck) { ensureLogin(); }
  return getRequest({ url: getHost() + url, data: paramObj, header: header(), method: "GET" });
}

function Delete(url, ischeck = false) {
  var getRequest = wxPromisify(wx.request);
  if (!ischeck) { ensureLogin(); }
  return getRequest({ url: getHost() + url, header: header(), method: "DELETE" });
}

function Put(url, paramObj) {
  var getRequest = wxPromisify(wx.request);
  return getRequest({ url: getHost() + url, data: paramObj, header: header(), method: "PUT" });
}

function FileUploadInternal(ops) {
  wx.uploadFile({
    filePath: ops.file,
    name: 'file',
    header: header(),
    url: getHost() + imgUpload,
    success: function (params) {
      if (params.statusCode == 200) { ops.success(params); } else { handleError(params); }
    },
    fail: function (err) {
      // 本地无 OSS 时上传必失败，弹提示即可，不阻塞
      wx.showToast({ title: "图片上传失败（本地无 OSS）", icon: "none", duration: 1500 });
      if (ops.fail) ops.fail(err);
    }
  });
}

export const httpService = {
  get: Get,
  post: Post,
  delete: Delete,
  put: Put,
  fileUploadInternal: FileUploadInternal,
  ensureLogin: ensureLogin,
  getHost: getHost
};
