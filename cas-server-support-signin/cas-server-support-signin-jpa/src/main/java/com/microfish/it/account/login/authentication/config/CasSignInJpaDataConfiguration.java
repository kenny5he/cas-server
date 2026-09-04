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

package com.microfish.it.account.login.authentication.config;

import com.microfish.it.account.login.authentication.entity.AccountEntity;
import com.microfish.it.account.login.authentication.repository.AccountRepository;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Enables the Spring Data JPA infrastructure used by the sign-in account
 * repository. CAS's standard web application intentionally excludes Boot's
 * datasource and Hibernate auto-configurations, so they must be imported for
 * this application-owned repository explicitly.
 */
@AutoConfiguration
@AutoConfigureAfter({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
})
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "jpa")
@ImportAutoConfiguration({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
})
@EntityScan(basePackageClasses = AccountEntity.class)
@EnableJpaRepositories(basePackageClasses = AccountRepository.class)
public class CasSignInJpaDataConfiguration {
}
