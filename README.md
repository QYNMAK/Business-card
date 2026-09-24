# 商家名片小程序后台（crm-dashboard）

一个基于 **微信小程序** 的商家电子名片服务：用户在小程序里填写名片信息（姓名、业务、联系方式、所在平台、微信二维码等），管理端可按平台统计、查看并管理所有填写过名片的用户。

> 项目前身是一个"明信片小程序"，后转型为 CRM 客户管理，现进一步收敛为**商家名片 + 管理后台**。
>
> 📖 **目录已按前后端整理，详细说明（启动步骤 / 敏感信息 / 接口 / FAQ）请看 [docs/项目说明.md](docs/项目说明.md)**

## 目录结构（整理后）

```
├── backend/    【后端】Spring Boot 工程（pom.xml、src/、mvnw）
├── frontend/   【前端】card.html（用户端）、admin.html（管理端）、wechat-app/（小程序源码包）
├── docs/       【文档】项目说明.md、sql/（临时脚本）、logs/（日志）
├── scripts/    【本机脚本】run_boot.bat（含机器路径，不入库）
└── README.md / LICENSE / .gitignore
```

## 快速开始（最省事：H2 内存库，无需 MySQL）

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

服务在 `http://localhost:8080`。连 MySQL 的完整步骤、环境变量表、常见问题见 [docs/项目说明.md](docs/项目说明.md)。

- 用户端：浏览器打开 `frontend/card.html`（连接条填 `http://localhost:8080`）
- 管理端：浏览器打开 `frontend/admin.html`（local 模式 Key 为 `local-admin-key`）

## 功能

### 用户端（小程序 / H5）
- 微信登录（`wx.login` code 换 session）或开发模式登录
- 名片的查看 / 编辑 / 清空
- 头像、微信二维码图片上传（阿里云 OSS）
- 生成带 logo 的名片二维码，扫码查看他人名片
- 电话 / 邮箱 / 微信号一键复制

### 管理端（`frontend/admin.html`）
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
| 前端 | 原生 HTML/CSS/JS（`frontend/card.html` 用户端，`frontend/admin.html` 管理端） |

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

## 安全约定
- 仓库不含任何真实密钥；数据库/微信/OSS/管理端 Key 均通过环境变量注入（完整清单见 [docs/项目说明.md 第五节](docs/项目说明.md)）。
- 软删除标志：`is_delete` 1=已删除，0=未删除；查询默认排除已删除记录。
- 时间统一按东八区（`serverTimezone=Asia/Shanghai`）存储。
