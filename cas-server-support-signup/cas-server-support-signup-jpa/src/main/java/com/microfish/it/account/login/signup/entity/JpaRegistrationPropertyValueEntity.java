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

package com.microfish.it.account.login.signup.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Persistent option value for an account-registration property.
 *
 * @author kenny
 * @since 7.3.0
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cas_property_value_t")
@Entity(name = JpaRegistrationPropertyValueEntity.ENTITY_NAME)
public class JpaRegistrationPropertyValueEntity implements Serializable {
    /** JPA entity name. */
    public static final String ENTITY_NAME = "JpaRegistrationPropertyValueEntity";

    @Serial
    private static final long serialVersionUID = 1774317470921051870L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cas_property_value_id_s")
    @SequenceGenerator(name = "cas_property_value_id_s", allocationSize = 100)
    private long id;

    @Column(name = "property_id")
    private long propertyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", insertable = false, updatable = false)
    private JpaRegistrationPropertyEntity property;

    @Column
    private String code;

    @Column
    private String value;

    @Column
    private int enabled;
}
