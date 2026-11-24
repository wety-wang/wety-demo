-- author: wety

/*
 * 数据库初始化脚本
 */
-- TODO 修改数据库名
USE `wety_demo`;

-- --------------------------------------账户信息表--------------------------------------
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
    `code`         varchar(255) NOT NULL COMMENT '账户唯一识别，修改操作时必须带着',
    `login_name`   varchar(255) COMMENT '登录名/用户名',
    `user_name`    varchar(255) COMMENT '用户姓名/真实姓名',
    `password`     varchar(255) COMMENT '密码',
    `disabled`     int(11) COMMENT '是否禁用：0未禁用 1已禁用',
    `email`        varchar(255) COMMENT '邮箱地址',
    `phone_number` varchar(255) COMMENT '手机号',
    `type`         varchar(255) COMMENT '账户类型:admin表示系统管理员；normal表示普通账户',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    UNIQUE KEY `uk_login_name` (`login_name`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账户信息表';

# 添加初始管理员账户（admin/123456）
INSERT INTO `account` (`code`, `login_name`, `user_name`, `password`, `disabled`, `email`, `phone_number`, `type`)
VALUES ('02ffc1b76b3f408583fcbdf802b68303', 'admin', '系统管理员',
        '$2a$10$YyspNwNPaOMpKBfVYQUfyeXw5fagDYpSBdVkUoFdIlJJ2zw.b6K6u', 0, NULL, NULL, 'admin');

-- --------------------------------------账户session表--------------------------------------
DROP TABLE IF EXISTS `account_session`;
CREATE TABLE `account_session`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `account_code` varchar(255) COMMENT '账户唯一识别',
    `token`        varchar(255) COMMENT 'token uuid',
    `create_time`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_code` (`account_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账户session表';

-- --------------------------------------角色信息表--------------------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `code`        varchar(255) COMMENT '角色唯一识别',
    `name`        varchar(255) COMMENT '角色名称',
    `deleted`     int(11) COMMENT '是否删除 1已删除，0未删除',
    `create_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色信息表';

-- --------------------------------------账户角色关联表--------------------------------------
DROP TABLE IF EXISTS `account_role_link`;
CREATE TABLE `account_role_link`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `account_code` varchar(255) COMMENT '账户code',
    `role_code`    varchar(255) COMMENT '角色code',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_role` (`account_code`, `role_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账户角色关联表';

-- --------------------------------------角色权限关联表--------------------------------------
DROP TABLE IF EXISTS `role_permission_link`;
CREATE TABLE `role_permission_link`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT,
    `role_code`       varchar(255) COMMENT '角色code',
    `permission_code` varchar(255) COMMENT '权限码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_code`, `permission_code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色权限关联表';

-- --------------------------------------登录审计日志记录--------------------------------------
DROP TABLE IF EXISTS `login_audit_log`;
CREATE TABLE `login_audit_log`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `code`                varchar(255) COMMENT '登录日志唯一编码',
    `account_code`        varchar(255) COMMENT '登录账号的code',
    `account_login_name`  varchar(255) COMMENT '登录账号登录名',
    `account_name`        varchar(255) COMMENT '登录账号用户名',
    `operation_time`      datetime COMMENT '登录/登出时间',
    `result`              varchar(255) COMMENT '操作结果',
    `description`         varchar(255) COMMENT '操作描述',
    `operation_data`      varchar(255) COMMENT '操作数据',
    `client_ip`           varchar(255) COMMENT '客户端ip',
    `last_operation_time` datetime COMMENT '最后一次操作时间',
    `logout_time`         datetime COMMENT '主动退出登陆时间',
    `user_agent`          varchar(2048) COMMENT 'User-Agent信息',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='登录审计日志记录';

-- --------------------------------------操作审计日志记录--------------------------------------
DROP TABLE IF EXISTS `operation_audit_log`;
CREATE TABLE `operation_audit_log`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT,
    `code`               varchar(255) COMMENT '操作日志唯一编码',
    `model_code`         varchar(255) COMMENT '模块编码',
    `model_name`         varchar(255) COMMENT '所属模块',
    `path`               varchar(255) COMMENT '请求路径',
    `http_method`        varchar(255) COMMENT 'HTTP方法',
    `query_string`       varchar(255) COMMENT 'Query字符串',
    `userAgent`          varchar(255) COMMENT 'User-Agent',
    `referer`            varchar(255) COMMENT 'Referer',
    `request_id`         varchar(255) COMMENT '请求ID',
    `account_code`       varchar(255) COMMENT '登录账号的code',
    `account_login_name` varchar(255) COMMENT '登录账号登录名',
    `account_name`       varchar(255) COMMENT '登录账号用户名',
    `client_ip`          varchar(255) COMMENT '客户端ip',
    `operation_time`     datetime COMMENT '操作时间',
    `operationType`      varchar(255) COMMENT '操作类型',
    `result`             varchar(255) COMMENT '操作结果',
    `description`        longtext COMMENT '操作描述',
    `duration_ms`        bigint(20) COMMENT '处理耗时(ms)',
    `login_log_code`     varchar(40) COMMENT '关联的登录日志编码',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='操作审计日志记录';

-- --------------------------------------文件资源表--------------------------------------
DROP TABLE IF EXISTS `general_resource`;
CREATE TABLE `general_resource`
(
    `id`            bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '数据主键',
    `code`          varchar(40) NOT NULL COMMENT '资源code',
    `name`          varchar(255) COMMENT '资源名称(eg:background.png)',
    `original`      varchar(255) COMMENT '资源原始文件名称(eg:background.png)',
    `uri`           varchar(1000) COMMENT '资源访问路径，在前面加上域名就能直接访问的，以/开头',
    `path`          varchar(1000) COMMENT '资源路径，该路径是相对于应用根目录的路径',
    `absolute_path` varchar(1000) COMMENT '绝对路径，该路径是文件相对于服务器的绝对路径',
    `owner_code`    varchar(40) COMMENT '资源拥有者编号，不同的业务自己设置和查询，一个业务记录对应多个资源时借助该编号',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_code` (`code`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='文件资源表，用于统一记录系统中的资源，例如上传的图片';
