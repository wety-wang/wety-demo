-- 组织架构与数据权限增量迁移脚本
-- 适用于已初始化的存量数据库升级，执行前请先备份数据库。

USE `wety_demo`;

-- --------------------------------------1. 新增组织架构与数据权限相关表--------------------------------------

CREATE TABLE IF NOT EXISTS `dept`
(
    `id`                  bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '数据库主键',
    `code`                varchar(40)  NOT NULL COMMENT '部门唯一业务编码',
    `parent_code`         varchar(40)  NOT NULL DEFAULT '0' COMMENT '父部门业务编码，根部门为0',
    `ancestors`           varchar(500) NOT NULL DEFAULT '0,' COMMENT '祖级物化路径，逗号分隔，如0,或0,dept-root,',
    `name`                varchar(100) NOT NULL COMMENT '部门名称',
    `sort_order`          int(11)      NOT NULL DEFAULT 0 COMMENT '同级排序号，升序排列',
    `leader_account_code` varchar(40)           DEFAULT NULL COMMENT '部门负责人账号编码',
    `phone`               varchar(32)           DEFAULT NULL COMMENT '联系电话',
    `email`               varchar(100)          DEFAULT NULL COMMENT '联系邮箱',
    `status`              tinyint(4)   NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `create_user`         varchar(40)           DEFAULT NULL COMMENT '创建人业务编码',
    `update_user`         varchar(40)           DEFAULT NULL COMMENT '更新人业务编码',
    `create_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`             tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`code`) USING BTREE,
    KEY `idx_dept_parent_code` (`parent_code`) USING BTREE,
    KEY `idx_dept_ancestors` (`ancestors`(191)) USING BTREE,
    KEY `idx_dept_deleted_status` (`deleted`, `status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='部门信息表';

CREATE TABLE IF NOT EXISTS `position`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '数据库主键',
    `code`        varchar(40)  NOT NULL COMMENT '岗位唯一业务编码',
    `post_code`   varchar(64)  NOT NULL COMMENT '岗位标识编码（如DEV、PM）',
    `name`        varchar(100) NOT NULL COMMENT '岗位名称',
    `sort_order`  int(11)      NOT NULL DEFAULT 0 COMMENT '排序号，升序排列',
    `status`      tinyint(4)   NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '岗位备注',
    `create_user` varchar(40)           DEFAULT NULL COMMENT '创建人业务编码',
    `update_user` varchar(40)           DEFAULT NULL COMMENT '更新人业务编码',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint(1)   NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_position_code` (`code`) USING BTREE,
    KEY `idx_position_post_code` (`post_code`) USING BTREE,
    KEY `idx_position_deleted_status` (`deleted`, `status`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='岗位信息表';

CREATE TABLE IF NOT EXISTS `account_dept_link`
(
    `id`           bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '数据库主键',
    `code`         varchar(40) NOT NULL COMMENT '关联记录唯一编码',
    `account_code` varchar(40) NOT NULL COMMENT '账号业务编码',
    `dept_code`    varchar(40) NOT NULL COMMENT '部门业务编码',
    `primary_flag` tinyint(1)  NOT NULL DEFAULT 1 COMMENT '是否主部门：1-是，0-否',
    `leader_flag`  tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否部门负责人：1-是，0-否',
    `create_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_dept_link_code` (`code`) USING BTREE,
    UNIQUE KEY `uk_account_dept_link_pair` (`account_code`, `dept_code`) USING BTREE,
    KEY `idx_account_dept_link_dept` (`dept_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账号部门关联表';

CREATE TABLE IF NOT EXISTS `account_position_link`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '数据库主键',
    `code`          varchar(40) NOT NULL COMMENT '关联记录唯一编码',
    `account_code`  varchar(40) NOT NULL COMMENT '账号业务编码',
    `position_code` varchar(40) NOT NULL COMMENT '岗位业务编码',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_position_link_code` (`code`) USING BTREE,
    UNIQUE KEY `uk_account_position_link_pair` (`account_code`, `position_code`) USING BTREE,
    KEY `idx_account_position_link_pos` (`position_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账号岗位关联表';

CREATE TABLE IF NOT EXISTS `role_dept_link`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '数据库主键',
    `code`        varchar(40) NOT NULL COMMENT '关联记录唯一编码',
    `role_code`   varchar(40) NOT NULL COMMENT '角色业务编码',
    `dept_code`   varchar(40) NOT NULL COMMENT '部门业务编码',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_dept_link_code` (`code`) USING BTREE,
    UNIQUE KEY `uk_role_dept_link_pair` (`role_code`, `dept_code`) USING BTREE,
    KEY `idx_role_dept_link_dept` (`dept_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色自定义数据权限部门关联表';

-- --------------------------------------2. 改造存量业务表--------------------------------------

ALTER TABLE `account`
    ADD COLUMN `primary_dept_code` varchar(40) DEFAULT NULL COMMENT '主部门业务编码（冗余字段，便于列表展示与数据权限过滤）' AFTER `type`,
    ADD COLUMN `super_admin`       tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否内置超级管理员：0-否，1-是' AFTER `primary_dept_code`,
    ADD COLUMN `create_user`       varchar(40) DEFAULT NULL COMMENT '创建人业务编码' AFTER `super_admin`,
    ADD COLUMN `deleted`           tinyint(1)  NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是' AFTER `create_user`,
    ADD INDEX `idx_account_primary_dept` (`primary_dept_code`),
    ADD INDEX `idx_account_deleted` (`deleted`);

ALTER TABLE `role`
    ADD COLUMN `data_scope` tinyint(4) NOT NULL DEFAULT 5 COMMENT '数据权限范围：1-全部，2-自定义部门，3-本部门，4-本部门及以下，5-仅本人' AFTER `name`;

ALTER TABLE `login_audit_log`
    ADD COLUMN `dept_code` varchar(40) DEFAULT NULL COMMENT '登录账号所属主部门编码' AFTER `account_name`,
    ADD INDEX `idx_login_audit_log_dept` (`dept_code`);

ALTER TABLE `operation_audit_log`
    CHANGE COLUMN `userAgent` `user_agent` varchar(255) DEFAULT NULL COMMENT 'User-Agent',
    CHANGE COLUMN `operationType` `operation_type` varchar(255) DEFAULT NULL COMMENT '操作类型',
    ADD COLUMN `dept_code` varchar(40) DEFAULT NULL COMMENT '操作人所属主部门编码' AFTER `account_name`,
    ADD INDEX `idx_operation_audit_log_dept` (`dept_code`);

-- --------------------------------------3. 初始化根部门与存量账号归属--------------------------------------

INSERT IGNORE INTO `dept` (`code`, `parent_code`, `ancestors`, `name`, `sort_order`, `leader_account_code`, `status`, `deleted`)
VALUES ('dept-root', '0', '0,', '总部', 0, '02ffc1b76b3f408583fcbdf802b68303', 1, 0);

-- 标记内置超级管理员
UPDATE `account`
SET `super_admin`       = 1,
    `deleted`           = 0,
    `primary_dept_code` = COALESCE(`primary_dept_code`, 'dept-root')
WHERE `login_name` = 'admin' OR `type` = 'admin';

-- 存量未归属部门的账号默认挂载至根部门
UPDATE `account`
SET `primary_dept_code` = 'dept-root',
    `deleted`           = COALESCE(`deleted`, 0)
WHERE `primary_dept_code` IS NULL;

-- 补齐账号与根部门的关联记录
INSERT IGNORE INTO `account_dept_link` (`code`, `account_code`, `dept_code`, `primary_flag`, `leader_flag`, `deleted`)
SELECT CONCAT('adl-', LEFT(MD5(CONCAT(`code`, '-dept-root')), 32)),
       `code`,
       `primary_dept_code`,
       1,
       CASE WHEN `super_admin` = 1 THEN 1 ELSE 0 END,
       0
FROM `account`
WHERE `primary_dept_code` IS NOT NULL;
