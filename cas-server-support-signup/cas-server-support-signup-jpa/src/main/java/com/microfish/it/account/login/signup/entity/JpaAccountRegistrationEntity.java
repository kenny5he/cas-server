/**
 * Copyright 2026 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.login.signup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Persistent CAS account created by the account-registration flow.
 *
 * @author kenny.he
 * @since 7.3.0
 */
@Table(name = "cas_account_t")
@Entity(name = JpaAccountRegistrationEntity.ENTITY_NAME)
@Getter
@Setter
@Slf4j
@Accessors(chain = true)
@NoArgsConstructor
public class JpaAccountRegistrationEntity implements Serializable {

    /** JPA entity name. */
    public static final String ENTITY_NAME = "JpaRegistrationAccountEntity";

    @Serial
    private static final long serialVersionUID = 3324361716165027650L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cas_account_id_s")
    @SequenceGenerator(name = "cas_account_id_s", sequenceName = "cas_account_id_s", allocationSize = 100)
    private Long id;

    @Column(name = "code", length = 60, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 60, nullable = false)
    private String name;

    @Column(name = "nike_name", length = 60)
    private String nikeName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "expired", length = 20)
    private Integer expired;

    @Column(name = "calling_code", length = 8)
    private String callingCode;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @Column(name = "email", length = 254, unique = true)
    private String email;

    @Column(name = "email_reverse", length = 254)
    private String emailReverse;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "enabled")
    private Integer enabled;

    @Column(name = "effective_time")
    private LocalDateTime effectiveTime;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "tenant_code")
    private String tenantCode;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "last_update_by")
    private Long lastUpdateBy;
}
