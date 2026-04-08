-- 图书管理系统数据库初始化脚本
-- Database: library_db

-- 设置编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library_db;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 图书表
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图书ID',
    `isbn` VARCHAR(20) NOT NULL COMMENT 'ISBN编号',
    `title` VARCHAR(200) NOT NULL COMMENT '书名',
    `author` VARCHAR(100) NOT NULL COMMENT '作者',
    `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `description` TEXT DEFAULT NULL COMMENT '简介',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
    `total_count` INT NOT NULL DEFAULT 1 COMMENT '总数量',
    `available_count` INT NOT NULL DEFAULT 1 COMMENT '可借数量',
    `is_new` TINYINT NOT NULL DEFAULT 0 COMMENT '是否新书: 0-否, 1-是',
    `publish_date` DATE DEFAULT NULL COMMENT '出版日期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_isbn` (`isbn`),
    KEY `idx_title` (`title`),
    KEY `idx_author` (`author`),
    KEY `idx_category` (`category`),
    KEY `idx_is_new` (`is_new`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='图书表';

-- 借阅记录表
DROP TABLE IF EXISTS `borrow_record`;
CREATE TABLE `borrow_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `book_id` BIGINT NOT NULL COMMENT '图书ID',
    `borrow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    `due_time` DATETIME NOT NULL COMMENT '应还时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-借阅中, 1-已归还, 2-已逾期',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_book_id` (`book_id`),
    KEY `idx_status` (`status`),
    KEY `idx_borrow_time` (`borrow_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '操作用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作描述',
    `method` VARCHAR(200) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-失败, 1-成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `cost_time` BIGINT DEFAULT NULL COMMENT '耗时(ms)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 注意：初始账号和图书数据由 DataInitializer.java 在应用启动时自动创建
-- 默认账号: admin/admin123 (管理员), user/user123 (普通用户)
