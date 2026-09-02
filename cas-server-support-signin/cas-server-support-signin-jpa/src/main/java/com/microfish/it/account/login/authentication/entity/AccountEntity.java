/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.login.authentication.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Table(name = "cas_account_t")
@Entity(name = AccountEntity.ENTITY_NAME)
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
@Slf4j
@Accessors(chain = true)
public class AccountEntity implements Serializable {

    /**
     * Th JPA entity name.
     */
    public static final String ENTITY_NAME = "JpaAccountEntity";

    @Serial
    private static final long serialVersionUID = -8370863003944241465L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "service_sequence")
    @SequenceGenerator(name = "service_sequence", allocationSize = 100)
    private long id;

    /**
     * 账号编码
     *   举例:
     *      1. 微信号：wxid_y76hzs705evs22
     *      2. 华为工号: c00194830
     */
    @Column(nullable = false)
    private String code;

    /**
     * 姓名（全）
     */
    @Column(nullable = false)
    private String name;

    /**
     * 姓
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * 名
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * 昵称
     */
    @Column(nullable = false)
    private String nikeName;

    /**
     * 账号类型
     *   注意：
     *     1. 内部：
     *        1.1: EMP 员工
     *        1.2: WX 外部协助人员
     *     2. 外部：
     *        2.1 CHANNEL: 渠道商 （销售商）
     *        2.2 PARTNER: 合作伙伴（售后、运维）
     *        2.3 SUPPLIER 供应商
     *        2.4 CUSTOMER 客户
     */
    @Column(nullable = false)
    private String type;

    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;

    /**
     * 邮箱
     */
    @Column(nullable = false)
    private String email;

    /**
     * 邮箱 反转数据
     * 冗余字段，数据查询优化
     */
    @Column(nullable = false)
    private String email_reverse;

    /**
     * 国家电话区号
     */
    private String callingCode;

    /**
     * 手机号码
     */
    @Column(nullable = false)
    private String phoneNumber;

    /**
     * 数据是否失效
     */
    @Column(nullable = false)
    private int expired;

    /**
     * 生效时间
     */
    @Column(nullable = false)
    private LocalDateTime effectiveTime;

    /**
     * 失效时间
     */
    @Column(nullable = false)
    private LocalDateTime expirationTime;

}
