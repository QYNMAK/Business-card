// API 根地址：默认本机后端；可在“我的”页修改（存 storage: apiHost，即时生效）
export const Host = 'http://localhost:8080';

// ===== 用户端（JWT 鉴权） =====
export const devLogin = "/user/dev-login";   // 开发登录（绕过微信，本地联调）
export const login = "/user/login";           // 微信小程序登录 ?code=
export const register = "/user/register";
export const loginOut = "/user/login/out";
export const currentUser = "/user/current";    // 当前用户信息 SimpleUserVO
export const userQuery = "/user";
export const user = "/user";
export const userUpdate = "/user/update";
export const qrUser = "/card/user/";          // 扫码看他人名片 ?jid=

// ===== 名片（商家名片核心） =====
export const card = "/card";                  // GET 当前用户名片 CardVO
export const cardUpdate = "/card/update";      // POST 编辑名片 CardUpdateDTO
export const cardClear = "/card/clear";        // POST 清空名片
export const qrCreate = "/qr/create";          // GET 生成带 logo 的名片二维码

// ===== 文件（阿里云 OSS） =====
export const imgUpload = "/file/img/upload";
export const imgPreview = "http://localhost:8080/oss-preview/"; // 本地预览前缀；真机换真实 OSS 域名

// ===== 管理端（X-Admin-Key；旧“联系人/发现”页仍引用，保留兼容） =====
export const customerPage = "/admin/users";
export const customer = "/admin/users";
export const customerAdd = "/admin/users";
export const customerAddWithUser = "/admin/users";

// ===== 缓存 key =====
export const currentUserCache = "currentUserCache";
export const cacheHistory = "users";
export const token = "token";
