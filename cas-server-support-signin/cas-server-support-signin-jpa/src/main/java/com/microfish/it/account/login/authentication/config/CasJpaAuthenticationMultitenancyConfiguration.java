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

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ScopedProxyMode;

import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;

import com.microfish.it.account.login.authentication.handler.TenantJpaAuthenticationHandlerBuilder;

/**
 * This is {@link CasJpaAuthenticationMultitenancyConfiguration}.
 *
 * @author Misagh Moayyed
 * @since 7.3.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "jpa")
@Configuration(value = "CasJpaAuthenticationMultitenancyConfiguration", proxyBeanMethods = false)
class CasJpaAuthenticationMultitenancyConfiguration {

    @Configuration(value = "CasJpaMultitenancyAuthenticationHandlersConfiguration", proxyBeanMethods = false)
    @ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Multitenancy)
    @EnableConfigurationProperties(CasConfigurationProperties.class)
    static class CasJpaMultitenancyAuthenticationHandlersConfiguration {
        @ConditionalOnMissingBean(name = "jpaMultitenancyAuthenticationPlanConfigurer")
        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public AuthenticationEventExecutionPlanConfigurer jpaMultitenancyAuthenticationPlanConfigurer(
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties,
            @Qualifier(ServicesManager.BEAN_NAME)
            final ServicesManager servicesManager) {
            return plan -> {
                if (casProperties.getMultitenancy().getCore().isEnabled()) {
                    val passwordPolicyConfiguration = new PasswordPolicyContext();
                    val principalFactory = PrincipalFactoryUtils.newPrincipalFactory();
                    val builder = new TenantJpaAuthenticationHandlerBuilder(passwordPolicyConfiguration,
                        principalFactory, applicationContext);
                    plan.registerTenantAuthenticationHandlerBuilder(builder);
                }
            };
        }
    }
}
