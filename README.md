# 商家名片小程序后台（crm-dashboard）

一个基于 **微信小程序** 的商家电子名片服务：用户在小程序里填写名片信息（姓名、业务、联系方式、所在平台、微信二维码等），管理端可按平台统计、查看并管理所有填写过名片的用户。

> 项目前身是一个"明信片小程序"，后转型为 CRM 客户管理，现进一步收敛为**商家名片 + 管理后台**。

## 功能

### 用户端（小程序 / H5）
- 微信登录（`wx.login` code 换 session）或开发模式登录
- 名片的查看 / 编辑 / 清空
- 头像、微信二维码图片上传（阿里云 OSS）
- 生成带 logo 的名片二维码，扫码查看他人名片
- 电话 / 邮箱 / 微信号一键复制

### 管理端（`web/admin.html`）
- 用户管理：分页查询、关键字/平台筛选、编辑资料、软删除
- 平台统计：按"所在平台"（微信/小红书/抖音…）统计用户数
- 审计日志：记录管理员每次改/删操作，可对比改前改后数据

## 技术栈

| 层 | 技术 |
|---|---|
| 框架 | Spring Boot 2.1.4 |
| ORM | MyBatis-Plus 3.1.1 |
| 数据库 | MySQL 5.7+（或 H2 内存库做本地快速验证） |
| 认证 | 微信小程序登录 + JWT |
| 限流 | 自研 AOP `@SelfLimiter` + Guava RateLimiter |
| 对象存储 | 阿里云 OSS（图片上传） |
| 二维码 | ZXing |
| 前端 | 原生 HTML/CSS/JS（`web/card.html` 用户端，`web/admin.html` 管理端） |

## 目录结构

```
com.pfh.crm.dashboard
├── controller/   User（登录/注册/扫码）、Card（名片 CRUD）、Qr（二维码）、File（图片上传）、Admin（用户管理/统计/审计）
├── service/      IUserService（登录/名片）、IAdminService（用户管理/审计/平台统计）
├── mapper/       UserMapper、AuditLogMapper
├── entity/       po（User/AuditLog）+ dto（入参）+ vo（出参）
└── common/       config（JWT 拦截/限流/OSS/自动填充）+ util（JWT/二维码/OSS/HTTP）+ exception + model
```

## 快速开始

### 1. 环境要求
- JDK 8（推荐）
- Maven 3.6+
- MySQL（本机或远程）

### 2. 建库建表
导入 `src/main/resources/crm.sql`，会自动创建 `tb_user` 和 `tb_audit_log`。

### 3. 配置
`src/main/resources/application.yml` 已用**环境变量占位符**脱敏，可用默认值直接跑：

| 变量 | 默认值 | 说明 |
|---|---|---|
| `DB_HOST` | `localhost` | 数据库地址 |
| `DB_NAME` | `crm` | 库名 |
| `DB_USER` / `DB_PASS` | `root` / `root` | 账号密码 |
| `WX_APPID` / `WX_APPSECRET` | `your-wechat-appid` | 微信小程序凭证（登录用，不填则真实微信登录不可用） |
| `OSS_AK` / `OSS_SK` / `OSS_ENDPOINT` / `OSS_IMG_PREVIEW` | `xx` | 阿里云 OSS（图片上传用，不填则图片功能不可用） |
| `ADMIN_KEY` | `admin-secret-key` | 管理端 `/admin/**` 接口鉴权 key |

> **重要**：仓库里不含任何真实密钥。请通过环境变量或本地 `application-xxx.yml` 注入自己的值，切勿把真实 AppSecret / OSS AK 提交到代码库。

### 4. 启动
```bash
mvn spring-boot:run
```
服务默认监听 `http://localhost:8080`。

### 5. 打开前端
- 用户端：`web/card.html`（浏览器打开，顶部连接条填接口地址 + 用户标识 → 点"连接"登录）
- 管理端：`web/admin.html`（接口地址 + 管理员 Key）

## 主要接口

### 用户端（JWT 鉴权，`Authorization` 头）
```
GET  /user/dev-login?unionId=xxx      开发模式登录，换 JWT（绕过微信，仅联调用）
POST /user/login?code=xxx             微信小程序登录
GET  /card                            当前用户名片
POST /card/update                     编辑名片
POST /card/clear                      清空名片
GET  /card/user/{jid}                扫码查看他人名片
POST /file/img/upload                 图片上传 OSS
GET  /qr/create                       生成带 logo 名片二维码
```

### 管理端（`X-Admin-Key` 鉴权）
```
GET    /admin/users?page=1&size=20&keyword=&platform=   用户列表
GET    /admin/users/{id}                                  用户详情
PUT    /admin/users/{id}                                  修改用户（写审计）
DELETE /admin/users/{id}                                  软删除（写审计）
GET    /admin/stats/platform                               各平台用户统计
GET    /admin/audit-logs?page=1&size=50                    审计日志
```

## 本地快速验证（无 MySQL）
项目自带 H2 profile，无需外部数据库即可启动：
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
H2 控制台：`http://localhost:8080/h2-console`（JDBC URL `jdbc:h2:mem:crm`，用户 `sa` 密码 `sa`）。

## 说明
- 软删除标志：`is_delete` 1=已删除，0=未删除；查询默认排除已删除记录。
- 时间统一按东八区（`serverTimezone=Asia/Shanghai`）存储。
