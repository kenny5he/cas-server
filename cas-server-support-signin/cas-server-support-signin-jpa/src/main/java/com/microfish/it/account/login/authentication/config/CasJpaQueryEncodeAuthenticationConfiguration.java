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

import java.util.Collection;
import java.util.HashSet;

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
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;

import com.microfish.it.account.login.authentication.utils.JdbcAuthenticationUtils;

/**
 * This is {@link CasJpaQueryEncodeAuthenticationConfiguration}.
 *
 * @author Misagh Moayyed
 * @author Dmitriy Kopylenko
 * @since 7.2.0
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "jpa")
@Configuration(value = "CasJdbcQueryEncodeAuthenticationConfiguration", proxyBeanMethods = false)
class CasJpaQueryEncodeAuthenticationConfiguration {

    @ConditionalOnMissingBean(name = "queryAndEncodeDatabaseAuthenticationHandlers")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public Collection<AuthenticationHandler> queryAndEncodeDatabaseAuthenticationHandlers(
        @Qualifier("queryAndEncodePasswordPolicyConfiguration")
        final PasswordPolicyContext queryAndEncodePasswordPolicyConfiguration,
        final ConfigurableApplicationContext applicationContext,
        @Qualifier(ServicesManager.BEAN_NAME)
        final ServicesManager servicesManager,
        @Qualifier("queryAndEncodePrincipalFactory")
        final PrincipalFactory jdbcPrincipalFactory,
        final CasConfigurationProperties casProperties) {
        val handlers = new HashSet<AuthenticationHandler>();
        val jdbc = casProperties.getAuthn().getJdbc();
        jdbc.getEncode().forEach(properties -> {
            val handler = JdbcAuthenticationUtils.newAuthenticationHandler(properties, applicationContext,
                jdbcPrincipalFactory, queryAndEncodePasswordPolicyConfiguration);
            handlers.add(handler);
        });
        return handlers;
    }

    @ConditionalOnMissingBean(name = "queryAndEncodePrincipalFactory")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PrincipalFactory queryAndEncodePrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @ConditionalOnMissingBean(name = "queryAndEncodePasswordPolicyConfiguration")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PasswordPolicyContext queryAndEncodePasswordPolicyConfiguration() {
        return new PasswordPolicyContext();
    }

    @ConditionalOnMissingBean(name = "queryAndEncodeAuthenticationEventExecutionPlanConfigurer")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AuthenticationEventExecutionPlanConfigurer queryAndEncodeAuthenticationEventExecutionPlanConfigurer(
        @Qualifier("queryAndEncodeDatabaseAuthenticationHandlers")
        final Collection<AuthenticationHandler> queryAndEncodeDatabaseAuthenticationHandlers,
        @Qualifier(PrincipalResolver.BEAN_NAME_PRINCIPAL_RESOLVER)
        final PrincipalResolver defaultPrincipalResolver) {
        return plan -> queryAndEncodeDatabaseAuthenticationHandlers.forEach(h ->
            plan.registerAuthenticationHandlerWithPrincipalResolver(h, defaultPrincipalResolver));
    }
}
