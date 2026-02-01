/** create database **/
CREATE DATABASE cas;

/** create user table **/
drop table if exists `cas_user`;
CREATE TABLE `cas_user` (
    `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，非空',
    `user_name` varchar(255) NOT NULL COMMENT '用户名称，非空',
    `nike_name` varchar(255) NULL COMMENT '昵称，非空',
    `first_name` varchar(60) NOT NULL COMMENT '名，非空',
    `last_name` varchar(60) NOT NULL COMMENT '姓，非空',
    `gender` int(11) NULL COMMENT '性别，1 Male 男/0 Female 女',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
    `email` varchar(20) DEFAULT NULL COMMENT 'email 邮箱',
    `password` varchar(20) NULL COMMENT '用户密码,使用MD5加密',
    `expired` int(11) NOT NULL COMMENT '密码过期字段 0 未过期 1 已过期',
    `type` varchar(20) NOT NULL COMMENT '用户类型 ',
    `status` int(11) NOT NULL COMMENT '用户状态 启用 0 锁定 1',
    `creation_date` datetime NOT NULL COMMENT '用户创建时间',
    `favicon_id` varchar(50) DEFAULT NULL COMMENT '用户头像',
    `uid` varchar(255) NULL COMMENT '用户 UID 标识',
    PRIMARY KEY (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/** 用户环境信息 **/
CREATE TABLE `cas_user_env` (
    `env_id` bigint NOT NULL AUTO_INCREMENT COMMENT '环境Id',
    `user_id` bigint NOT NULL COMMENT '用户ID，非空',
    `ipv4` varchar(20) DEFAULT NULL COMMENT '用户IP4信息',
    `ipv6` varchar(20) DEFAULT NULL COMMENT '用户IP6信息',
    `mac` varchar(20) DEFAULT NULL COMMENT '用户MAC地址',
    `device` varchar(20) DEFAULT NULL COMMENT '用户使用设备',
    `app_type` varchar(5) DEFAULT NULL COMMENT '登录程序类别(APP,WEB,WECHAT)',
    `accredit` varchar(5) DEFAULT NULL COMMENT '环境认证',
    PRIMARY KEY (`env_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='用户使用环境表';

/** 审计日志信息 **/
create table cas_audit_trail (
    `id` bigint NOT NULL AUTO_INCREMENT,
    AUD_ACTION varchar(255) NULL COMMENT 'Action',
    AUD_CLIENT_IP varchar(255) NULL COMMENT 'Client Ip',
    AUD_DATE timestamp not null NULL COMMENT 'Date',
    AUD_RESOURCE varchar(2048) NULL COMMENT 'Resource',
    AUD_SERVER_IP varchar(255) NULL COMMENT 'Server IP',
    AUD_USER varchar(255) NULL COMMENT 'User',
    AUD_USERAGENT varchar(255) NULL COMMENT 'User Agent',
    primary key (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='登录历史记录表';

drop table if exists `cas_questions`;
create table `cas_questions` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `question` varchar(255) NULL COMMENT '问题',
    `status` int(11) NOT NULL COMMENT '状态 启用 0 锁定 1',
    `order` int null,
    primary key (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='密保问题';

drop table if exists `cas_question_answers`;
create table `cas_question_answers` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL COMMENT '用户信息',
    `question` bigint NOT NULL COMMENT '问题详情',
    `answer` varchar(255) NULL,
    primary key (`id`)
)ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='密保答案';

drop table if exists `cas_password_history`;
create table `cas_password_history` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL COMMENT '用户名称，非空',
    `password` varchar(60) NOT NULL COMMENT ' 用户名称，非空 ',
    `record_date` datetime NOT NULL COMMENT '用户创建时间',
    primary key (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COMMENT='历史密码';

create table cas_tickets (
    `id` varchar(255) not null,
    body text,
    creation_Time datetime not null,
    parent_Id varchar(255),
    principal_Id varchar(255),
    type varchar(255) not null,
    primary key (id)
) ENGINE=MyISAM;


create table cas_saml_metadata_document (
    id bigint not null,
    name varchar(255) not null,
    signature longtext,
    value longtext,
    primary key (id)
) type=MyISAM;

create table cas_com_audit_trail (
    id bigint not null AUTO_INCREMENT,
    AUD_ACTION varchar(255),
    APPLIC_CD varchar(255),
    AUD_CLIENT_IP varchar(255),
    AUD_DATE timestamp not null,
    AUD_RESOURCE varchar(2048),
    AUD_SERVER_IP varchar(255),
    AUD_USER varchar(255),
    AUD_USERAGENT varchar(255),
    primary key (id)
)