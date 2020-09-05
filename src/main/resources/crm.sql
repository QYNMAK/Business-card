/*
 Navicat Premium Data Transfer

 Source Server         : crm
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : 47.114.129.194:3306
 Source Schema         : crm

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 05/09/2020 11:59:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_customer
-- ----------------------------
DROP TABLE IF EXISTS `tb_customer`;
CREATE TABLE `tb_customer` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  `sex` int(1) NOT NULL COMMENT '性别 1男 0女',
  `province` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '省份',
  `city` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '市',
  `county` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '县',
  `address` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详细地址',
  `phone` varchar(11) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '手机号',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `created` datetime NOT NULL COMMENT '创建时间',
  `updated` datetime NOT NULL COMMENT '修改时间',
  `is_delete` int(1) unsigned DEFAULT '0' COMMENT '删除标志 1删除 0未删除',
  `user_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
  `pre_view` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '预览头像',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='客户表';

-- ----------------------------
-- Table structure for tb_customer_pic
-- ----------------------------
DROP TABLE IF EXISTS `tb_customer_pic`;
CREATE TABLE `tb_customer_pic` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `customer_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户id',
  `pic_id` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图片id',
  `created` datetime NOT NULL COMMENT '创建时间',
  `updated` datetime NOT NULL COMMENT '修改时间',
  `is_delete` int(1) unsigned DEFAULT '0' COMMENT '删除标志 1删除 0未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='客户图片';

-- ----------------------------
-- Table structure for tb_customer_relation
-- ----------------------------
DROP TABLE IF EXISTS `tb_customer_relation`;
CREATE TABLE `tb_customer_relation` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `customer_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户id',
  `customer_master_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '客户id',
  `created` datetime NOT NULL COMMENT '创建时间',
  `updated` datetime NOT NULL COMMENT '修改时间',
  `is_delete` int(1) unsigned DEFAULT '0' COMMENT '删除标志 1删除 0未删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='客户关联';

-- ----------------------------
-- Table structure for tb_customer_tel
-- ----------------------------
DROP TABLE IF EXISTS `tb_customer_tel`;
CREATE TABLE `tb_customer_tel` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `customer_id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '客户id',
  `phone_num` varchar(11) COLLATE utf8mb4_bin NOT NULL COMMENT '手机号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='客户电话';

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `phone` varchar(120) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '账号',
  `username` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `status` int(1) DEFAULT NULL COMMENT '使用状态（1正常 0非正常）',
  `head_pic` varchar(150) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像地址',
  `created` datetime NOT NULL COMMENT '创建时间',
  `updated` datetime NOT NULL COMMENT '修改时间',
  `union_id` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信用户唯一id',
  `sex` int(1) DEFAULT NULL COMMENT '0未知;1男性;2女性',
  `is_delete` int(1) unsigned DEFAULT '0' COMMENT '删除标志 1删除 0未删除',
  `county` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '县',
  `city` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '市',
  `province` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '省份',
  `address` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户表';

SET FOREIGN_KEY_CHECKS = 1;
