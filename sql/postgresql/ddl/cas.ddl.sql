
/** 1.1.1 创建用户表 **/
CREATE TABLE IF NOT EXISTS cas_account_t(
    id int8 NOT NULL,
    "number" VARCHAR(20) NOT NULL,
    "name" VARCHAR(60) NOT NULL,
    nike_name VARCHAR(60)  NOT NULL,
    first_name varchar(60) NOT NULL,
    last_name varchar(60) NOT NULL,
    password varchar(50) NULL,
    calling_code VARCHAR(4) NULL,
    mobile_phone VARCHAR(20) NULL,
    email VARCHAR(50) NULL,
    email_reverse VARCHAR(50) NULL,
    "type" VARCHAR(5) NOT NULL,
    expired integer NOT NULL default 0,
    start_date timestamp NOT NULL,
    end_date timestamp NULL,
    tenant_id int8  NOT NULL,
    tenant_code VARCHAR(50) NOT NULL,
    creation_date timestamp NOT NULL,
    created_by VARCHAR(10),
    last_update_date timestamp NULL,
    last_update_by VARCHAR(10) NULL,
    PRIMARY KEY(id)
 );

/** 1.1.2 账号表字段添加注释 **/
COMMENT ON TABLE cas_account_t is '账号表';
COMMENT ON COLUMN cas_account_t.id is '主键，账号ID，无符号、非空。';
COMMENT ON COLUMN cas_account_t.number is '账号';
COMMENT ON COLUMN cas_account_t.name is '账号名称，非空';
COMMENT ON COLUMN cas_account_t.nike_name is '昵称，非空';
COMMENT ON COLUMN cas_account_t.mobile_phone is '手机号码';
COMMENT ON COLUMN cas_account_t.email is 'email 邮箱';
COMMENT ON COLUMN cas_account_t.type is '账号类型/是否为虚拟账号';
COMMENT ON COLUMN cas_account_t.expired is '账号是否过期 有效 0, 过期 1';
COMMENT ON COLUMN cas_account_t.start_date is '生效日期';
COMMENT ON COLUMN cas_account_t.end_date is '失效日期';
COMMENT ON COLUMN cas_account_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_account_t.created_by is '记录创建人';
COMMENT ON COLUMN cas_account_t.last_update_date is '最后一次更改日期';
COMMENT ON COLUMN cas_account_t.last_update_by is '最后一次更改人';

/** 1.1.3 创建序列 **/
CREATE SEQUENCE "cas_account_id_s" START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 1;
/** 1.1.4 创建索引 **/
ALTER TABLE cas_account_t ADD CONSTRAINT unique_account_number UNIQUE ("number");

/** 1.2.1 个人信息 **/
CREATE TABLE IF NOT EXISTS cas_profile_t(
    id int8 NOT NULL,
    account_id int8 NOT NULL,
    gender integer NULL,
    country_code VARCHAR(4) NULL,
    id_card VARCHAR(20) NULL,
    birth_date date NULL,
    school varchar(100) NULL,
    creation_date timestamp NOT NULL ,
    created_by VARCHAR(10),
    last_update_date timestamp NULL ,
    last_update_by VARCHAR(10) NULL ,
    PRIMARY KEY(id)
);
/** 1.2.2 个人信息表字段添加注释 **/
COMMENT ON TABLE cas_profile_t is '账号表';
COMMENT ON COLUMN cas_profile_t.id is '主键，账号ID，无符号、非空。';
COMMENT ON COLUMN cas_profile_t.account_id is '账号Id';
COMMENT ON COLUMN cas_account_t.gender is '性别';
COMMENT ON COLUMN cas_account_t.country_code is '国家';
COMMENT ON COLUMN cas_account_t.id_card is '身份证号码';
COMMENT ON COLUMN cas_account_t.birth_date is '生日';
COMMENT ON COLUMN cas_account_t.school is '学校';
COMMENT ON COLUMN cas_account_t.creation_date is '创建时间';
COMMENT ON COLUMN cas_account_t.created_by is '记录创建人';
COMMENT ON COLUMN cas_account_t.last_update_date is '最后一次更改日期';
COMMENT ON COLUMN cas_account_t.last_update_by is '最后一次更改人';