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

import jakarta.persistence.EntityManagerFactory;

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
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ScopedProxyMode;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalFactoryUtils;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.services.ServicesManager;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;

import com.microfish.it.account.login.authentication.utils.JpaAuthenticationUtils;

/**
 * Configures stored-procedure authentication through JPA.
 */
@EnableConfigurationProperties(CasConfigurationProperties.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.Authentication, module = "jpa")
@Configuration(value = "CasJpaStoredProcedureAuthenticationConfiguration", proxyBeanMethods = false)
@Import(CasSignInJpaDataConfiguration.class)
class CasJpaStoredProcedureAuthenticationConfiguration {

    @ConditionalOnMissingBean(name = "storedProcedureAuthenticationHandlers")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public Collection<AuthenticationHandler> storedProcedureAuthenticationHandlers(
        @Qualifier("storedProcedurePasswordPolicyConfiguration")
        final PasswordPolicyContext passwordPolicyConfiguration,
        final ConfigurableApplicationContext applicationContext,
        @Qualifier(ServicesManager.BEAN_NAME)
        final ServicesManager servicesManager,
        @Qualifier("storedProcedurePrincipalFactory")
        final PrincipalFactory principalFactory,
        final EntityManagerFactory entityManagerFactory,
        final CasConfigurationProperties casProperties) {
        val handlers = new HashSet<AuthenticationHandler>();
        casProperties.getAuthn().getJdbc().getProcedure().forEach(properties -> {
            val handler = JpaAuthenticationUtils.newAuthenticationHandler(properties, applicationContext,
                principalFactory, passwordPolicyConfiguration, entityManagerFactory);
            handlers.add(handler);
        });
        return handlers;
    }

    @ConditionalOnMissingBean(name = "storedProcedurePrincipalFactory")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PrincipalFactory storedProcedurePrincipalFactory() {
        return PrincipalFactoryUtils.newPrincipalFactory();
    }

    @ConditionalOnMissingBean(name = "storedProcedurePasswordPolicyConfiguration")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public PasswordPolicyContext storedProcedurePasswordPolicyConfiguration() {
        return new PasswordPolicyContext();
    }

    @ConditionalOnMissingBean(name = "storedProcedureAuthenticationEventExecutionPlanConfigurer")
    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    public AuthenticationEventExecutionPlanConfigurer storedProcedureAuthenticationEventExecutionPlanConfigurer(
        @Qualifier("storedProcedureAuthenticationHandlers")
        final Collection<AuthenticationHandler> authenticationHandlers,
        @Qualifier(PrincipalResolver.BEAN_NAME_PRINCIPAL_RESOLVER)
        final PrincipalResolver defaultPrincipalResolver) {
        return plan -> authenticationHandlers.forEach(handler ->
            plan.registerAuthenticationHandlerWithPrincipalResolver(handler, defaultPrincipalResolver));
    }
}
