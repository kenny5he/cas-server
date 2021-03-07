/** create database **/
CREATE DATABASE cas;

/** create user table **/
CREATE TABLE `tbl_users` (
    `user_id` decimal(30,0) NOT NULL COMMENT '用户ID，非空',
    `user_name` varchar(60) NOT NULL COMMENT '用户名称，非空',
    `nike_name` varchar(60) NOT NULL COMMENT '昵称，非空',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
    `email` varchar(20) DEFAULT NULL COMMENT 'email 邮箱',
    `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
    `password` varchar(20) NOT NULL COMMENT '用户密码,使用MD5加密',
    `expired` int(11) NOT NULL COMMENT '密码过期字段 0 未过期 1 已过期',
    `type` varchar(20) NOT NULL COMMENT '用户类型 ',
    `status` int(11) NOT NULL COMMENT '用户状态 启用 0 锁定 1',
    `creation_date` datetime NOT NULL COMMENT '用户创建时间',
    `favicon_id` varchar(50) DEFAULT NULL COMMENT '用户头像',
    PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/** 用户环境信息 **/
CREATE TABLE `tbl_user_env` (
    `env_id` decimal(30,0) NOT NULL COMMENT '环境Id',
    `user_id` decimal(30,0) NOT NULL COMMENT '用户ID，非空',
    `ipv4` varchar(20) DEFAULT NULL COMMENT '用户IP4信息',
    `ipv6` varchar(20) DEFAULT NULL COMMENT '用户IP6信息',
    `mac` varchar(20) DEFAULT NULL COMMENT '用户MAC地址',
    `device` varchar(20) DEFAULT NULL COMMENT '用户使用设备',
    `app_type` varchar(5) DEFAULT NULL COMMENT '登录程序类别(APP,WEB,WECHAT)',
    `accredit` varchar(5) DEFAULT NULL COMMENT '环境认证',
    PRIMARY KEY (`env_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='用户使用环境表';

/** 用户登陆信息 **/
CREATE TABLE `tbl_user_login_history_log` (
    `login_id` decimal(30,0) NOT NULL COMMENT '登录信息Id,可做主键',
    `userid` decimal(30,0) NOT NULL COMMENT '外键 用户Id',
    `env_id` decimal(30,0) NOT NULL COMMENT '外键 环境Id',
    `login_time` datetime NOT NULL COMMENT '用户登录时间'
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='登录历史记录表';

