-- 备忘录与个人知识库问答系统 · 建表脚本（唯一建表来源）
-- 用法：mysql -uroot -p < docs/sql/schema.sql
-- 约定：库名 memo，字符集 utf8mb4；表名与字段名一律 snake_case。

CREATE DATABASE IF NOT EXISTS `memo` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;

USE `memo`;

-- 用户
CREATE TABLE IF NOT EXISTS `user` (
  `id`            BIGINT       NOT NULL AUTO_INCREMENT,
  `username`      VARCHAR(64)  NOT NULL COMMENT '登录名',
  `password_hash` VARCHAR(100) NOT NULL COMMENT 'BCrypt 哈希',
  `nickname`      VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
  `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';
