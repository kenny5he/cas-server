
/** 1.1.1 创建用户表 **/
CREATE TABLE IF NOT EXISTS cas_user_t(
    user_id int8 NOT NULL,
    user_account VARCHAR(20) NOT NULL,
    user_name VARCHAR(60) NOT NULL,
    nike_name VARCHAR(60)  NOT NULL,
    first_name varchar(60) NOT NULL,
    last_name varchar(60) NOT NULL,
    gender integer NULL,
    password varchar(50) NULL,
    expired integer NOT NULL,
    mobile_phone VARCHAR(20) NULL,
    email VARCHAR(50) NULL,
    "type" VARCHAR(5) NOT NULL,
    enabled integer NOT NULL default 1,
    start_date timestamp NOT NULL,
    end_date timestamp NULL,
    tenant_id int8  NOT NULL,
    tenant_code VARCHAR(50) NOT NULL,
    creation_date timestamp NOT NULL ,
    created_by VARCHAR(10),
    last_update_date timestamp NULL ,
    last_update_by VARCHAR(10) NULL ,
    PRIMARY KEY(user_id)
 );

/** 1.1.2 用户表字段添加注释 **/
COMMENT ON TABLE cas_user_t is '用户表';
COMMENT ON COLUMN cas_user_t.user_id is '主键，用户ID，无符号、非空。';
COMMENT ON COLUMN cas_user_t.user_account is '用户账号';
COMMENT ON COLUMN cas_user_t.user_name is '用户名称，非空';
COMMENT ON COLUMN cas_user_t.nike_name is '昵称，非空';
COMMENT ON COLUMN cas_user_t.mobile_phone is '手机号码';
COMMENT ON COLUMN cas_user_t.email is 'email 邮箱';
COMMENT ON COLUMN cas_user_t.type is '用户类型/是否为虚拟用户';
COMMENT ON COLUMN cas_user_t.enabled is '用户状态 失效 0, 有效 1';
COMMENT ON COLUMN cas_user_t.start_date is '生效日期';
COMMENT ON COLUMN cas_user_t.end_date is '失效日期';
COMMENT ON COLUMN cas_user_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_user_t.created_by is '记录创建人';
COMMENT ON COLUMN cas_user_t.last_update_date is '最后一次更改日期';
COMMENT ON COLUMN cas_user_t.last_update_by is '最后一次更改人';

/** 1.1.3 创建序列 **/
CREATE SEQUENCE "cas_user_id_s" START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 1;
/** 1.1.4 创建索引 **/
ALTER TABLE cas_user_t ADD CONSTRAINT unique_user_account UNIQUE (user_account);
