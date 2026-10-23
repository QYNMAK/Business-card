# 商家名片系统（crm-dashboard）

一套 **Spring Boot 后端 + 纯静态 H5 前端** 的商家电子名片服务：

- **业务端** `frontend/card.html`：填写 / 管理自己的名片（姓名、业务、联系方式、所在平台、微信二维码等），数据持久化到服务器数据库，头像 / 二维码图片存阿里云 OSS。
- **管理端** `frontend/admin.html`：按平台统计、查看并管理所有填写过名片的用户，记录审计日志。

> 项目早期是"微信小程序"形态，现已收敛为**纯 H5 + 管理后台**，**不再包含小程序端**。业务完全由 `card.html` 承担。
>
> 📖 详细说明（目录 / 启动 / 生产部署 / 接口 / 安全 / FAQ）请看 [docs/项目说明.md](docs/项目说明.md)

## 目录结构

```
├── backend/    【后端】Spring Boot + Maven（pom.xml、src/、mvnw）
│   └── src/main/resources/
│       ├── application.yml / application-local.yml / application-prod.yml
│       ├── crm.sql（建表脚本）
│       └── static/（card.html / admin.html，由 frontend/ 自动同步，随 jar 同源发布）
├── frontend/   【前端】card.html（业务端）、admin.html（管理端）——页面唯一来源
├── docs/       【文档】项目说明.md、verify_*.ps1、sql/、logs/
├── scripts/    【本机脚本】run_boot.bat（本机启动）、deploy_prod.bat（生产部署）
├── dist/       【交接产物】clean 快照 + 交接说明（不入库）
└── README.md / LICENSE / .gitignore
```

## 快速开始

### 本地最省事（H2 内存库，无需任何数据库）
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
服务在 `http://localhost:8080`：
- 业务端 `http://localhost:8080/card.html`（连接条填 `http://localhost:8080` + 任意用户标识 → "连接"）
- 管理端 `http://localhost:8080/admin.html`（local 模式 Key = `local-admin-key`）

### 生产部署（腾讯云 TDSQL @ 146.56.210.13，Windows）
```bat
scripts\deploy_prod.bat
```
脚本自动：同步 `frontend/` → `static/` → 打包 jar → 以 `prod` profile 启动。
启动后访问 `http://146.56.210.13:8080/card.html`（业务端）与 `/admin.html`（管理端）。
数据库 / OSS / 管理 Key 在脚本"配置区"或环境变量中填写。

完整步骤、环境变量表、安全注意事项、FAQ 见 [docs/项目说明.md](docs/项目说明.md)。

## 功能

### 业务端（`card.html`）
- 开发模式登录（`/user/dev-login` 换 JWT，无需微信）
- 名片查看 / 编辑 / 清空（读写数据库 `tb_user`）
- 头像、微信二维码图片上传（阿里云 OSS）
- 生成名片二维码，扫码查看他人名片
- 电话 / 邮箱 / 微信号一键复制

### 管理端（`admin.html`）
- 用户管理：分页查询、关键字 / 平台筛选、编辑资料、软删除
- 平台统计：按"所在平台"（微信 / 小红书 / 抖音…）统计用户数
- 审计日志：记录管理员每次改 / 删操作，可对比改前改后数据

## 技术栈

| 层 | 技术 |
|---|---|
| 框架 | Spring Boot 2.1.4（JDK 8/11/17） |
| ORM | MyBatis-Plus 3.1.1 |
| 数据库 | 腾讯云 TDSQL / MySQL 5.7+ / H2（本地零配置） |
| 认证 | JWT（业务端 `dev-login`）；管理端 `X-Admin-Key` |
| 限流 | 自研 AOP `@SelfLimiter` + Guava |
| 对象存储 | 阿里云 OSS（头像 / 二维码图片） |
| 二维码 | ZXing |
| 前端 | 原生 HTML/CSS/JS（`card.html` 业务端、`admin.html` 管理端） |

## 主要接口

### 业务端（JWT 鉴权，`Authorization` 头）
```
GET  /user/dev-login?unionId=xxx   开发模式登录，换 JWT
GET  /user/current                 当前用户信息
GET  /card                         当前用户名片
POST /card/update                  编辑名片
POST /card/clear                   清空名片
GET  /card/user/{jid}             扫码查看他人名片
POST /file/img/upload              图片上传 OSS
```

### 管理端（`X-Admin-Key` 鉴权）
```
GET    /admin/users?page=1&size=20&keyword=&platform=
GET    /admin/users/{id}
PUT    /admin/users/{id}           （写审计日志）
DELETE /admin/users/{id}           （软删除，写审计）
GET    /admin/stats/platform
GET    /admin/audit-logs?page=1&size=50
```

## 安全约定
- 仓库不含任何真实密钥；数据库 / OSS / 管理端 Key 均通过环境变量注入（清单见 [docs/项目说明.md 第五节](docs/项目说明.md)）。
- 软删除标志：`is_delete` 1=已删除，0=未删除；查询默认排除已删除记录。
- 时间统一按东八区（`serverTimezone=Asia/Shanghai`）存储。
- ⚠ 上线前建议：给 `/user/dev-login` 加访问密钥、收紧 CORS 白名单、更换默认 `ADMIN_KEY`（见 [docs/项目说明.md 第六节](docs/项目说明.md)）。
