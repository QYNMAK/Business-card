/*
 Navicat Premium Data Transfer
 Source Schema : crm
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `phone` varchar(120) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号',
  `username` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名/名片姓名',
  `password` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码（保留）',
  `status` int(1) DEFAULT '1' COMMENT '1正常 0非正常',
  `head_pic` varchar(150) DEFAULT NULL COMMENT '头像 OSS 路径',
  `created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `union_id` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信 unionId',
  `sex` int(1) DEFAULT '0' COMMENT '0未知 1男 2女',
  `is_delete` int(1) unsigned DEFAULT '0' COMMENT '软删除标志 1删除 0未删除',
  `county` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `city` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `province` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL,
  `address` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详细地址',
  `email` varchar(120) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `wechat_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信号',
  `description` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名片描述/副标题',
  `greeting` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '问候语/备注',
  `biz_type` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型',
  `biz_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务名称',
  `msg_link` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息链接(平台私信URL)',
  `qr` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信二维码图片(OSS相对路径)',
  `platform` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所在平台(微信/小红书/抖音等)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_union_id` (`union_id`) USING BTREE,
  KEY `idx_platform` (`platform`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';

-- ----------------------------
-- Table structure for tb_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_audit_log`;
CREATE TABLE `tb_audit_log` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `action` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT 'USER_UPDATE/USER_DELETE/USER_CREATE',
  `target_user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '被操作用户id',
  `operator` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作人(API key前缀)',
  `before_json` text COLLATE utf8mb4_bin COMMENT '操作前数据快照',
  `after_json` text COLLATE utf8mb4_bin COMMENT '操作后数据快照',
  `remark` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '操作说明',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_target_user` (`target_user_id`) USING BTREE,
  KEY `idx_created_at` (`created_at`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='管理员操作审计日志';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- 已有数据库升级用 ALTER（新库直接建表无需执行）
-- ----------------------------
-- ALTER TABLE `tb_user` ADD COLUMN `biz_type`  varchar(50)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型';
-- ALTER TABLE `tb_user` ADD COLUMN `biz_name`  varchar(50)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务名称';
-- ALTER TABLE `tb_user` ADD COLUMN `msg_link`  varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '消息链接';
-- ALTER TABLE `tb_user` ADD COLUMN `qr`        varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信二维码';
-- ALTER TABLE `tb_user` ADD COLUMN `platform`  varchar(50)  COLLATE utf8mb4_bin DEFAULT NULL COMMENT '所在平台';
