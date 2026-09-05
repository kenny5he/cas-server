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

package com.microfoolish.it.account.signup.configuration;

import com.microfish.it.account.login.configuration.annotation.EnableConfigurationMapping;
import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.config.CasAccountManagementWebflowAutoConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.CasWebflowExecutionPlanConfigurer;
import org.apereo.cas.web.flow.CasWebflowIdExtractor;
import org.apereo.cas.web.flow.actions.WebflowActionBeanSupplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;
import org.springframework.webflow.execution.Action;

import java.util.List;

/**
 * @author kenny.he
 * @since 2026/09/04
 */
@AutoConfiguration(before = CasAccountManagementWebflowAutoConfiguration.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
@EnableConfigurationMapping(classes = CasSignupCryptoProperties.class)
public class CasSignupAutoConfiguration {

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = "casSignupEndpointConfigurer")
    public CasWebSecurityConfigurer<Void> casSignupEndpointConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of("/" + CasSignupWebflowConfigurer.REGISTRATION_FLOW_PATH);
            }
        };
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = CasWebflowConstants.ACTION_ID_ACCOUNT_REGISTRATION_SUBMIT)
    public Action submitAccountRegistrationAction(
            @Qualifier(TenantExtractor.BEAN_NAME) final TenantExtractor tenantExtractor,
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties,
            @Qualifier(AccountRegistrationService.BEAN_NAME)
            final AccountRegistrationService accountRegistrationService,
            @Qualifier(TicketFactory.BEAN_NAME) final TicketFactory ticketFactory,
            @Qualifier(TicketRegistry.BEAN_NAME) final TicketRegistry ticketRegistry,
            final CommunicationsManager communicationsManager) {
        return WebflowActionBeanSupplier.builder()
                .withApplicationContext(applicationContext)
                .withProperties(casProperties)
                .withAction(() -> new CasSignupSubmitAccountRegistrationAction(
                        accountRegistrationService,
                        casProperties,
                        communicationsManager,
                        ticketFactory,
                        ticketRegistry,
                        tenantExtractor))
                .withId(CasWebflowConstants.ACTION_ID_ACCOUNT_REGISTRATION_SUBMIT)
                .build()
                .get();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(name = "registrationWebflowIdExtractor")
    public CasWebflowIdExtractor registrationWebflowIdExtractor() {
        return (request, flowId) -> {
            if (CasSignupWebflowConfigurer.REGISTRATION_FLOW_PATH.equals(flowId)) {
                request.setAttribute(CasSignupWebflowConfigurer.REGISTRATION_REQUEST_ATTRIBUTE, Boolean.TRUE);
                return CasWebflowConfigurer.FLOW_ID_LOGIN;
            }
            return flowId;
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "casSignupWebflowConfigurer")
    public CasWebflowConfigurer casSignupWebflowConfigurer(
            final CasConfigurationProperties casProperties,
            final ConfigurableApplicationContext applicationContext,
            @Qualifier(CasWebflowConstants.BEAN_NAME_FLOW_DEFINITION_REGISTRY)
            final FlowDefinitionRegistry flowDefinitionRegistry,
            @Qualifier(CasWebflowConstants.BEAN_NAME_FLOW_BUILDER_SERVICES)
            final FlowBuilderServices flowBuilderServices) {
        var configurer = new CasSignupWebflowConfigurer(
                flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
        configurer.setOrder(casProperties.getAccountRegistration().getWebflow().getOrder() + 1);
        return configurer;
    }

    @Bean
    @ConditionalOnMissingBean(name = "casSignupWebflowExecutionPlanConfigurer")
    public CasWebflowExecutionPlanConfigurer casSignupWebflowExecutionPlanConfigurer(
            @Qualifier("casSignupWebflowConfigurer") final CasWebflowConfigurer configurer) {
        return plan -> plan.registerWebflowConfigurer(configurer);
    }
}
