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

import java.util.List;

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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
import org.springframework.data.redis.core.StringRedisTemplate;

import com.microfish.it.iam.login.configuration.annotation.EnableConfigurationMapping;
import com.microfoolish.it.account.signup.registration.SignupAccountStore;
import com.microfoolish.it.account.signup.registration.services.AccountRegistrationService;
import com.microfoolish.it.account.signup.verficationcode.cache.VerificationCodeCache;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeSender;
import com.microfoolish.it.account.signup.verficationcode.services.VerificationCodeService;
import com.microfoolish.it.account.signup.verficationcode.services.CompositeVerificationCodeCache;
import com.microfoolish.it.account.signup.registration.services.DefaultAccountRegistrationService;
import com.microfoolish.it.account.signup.verficationcode.services.DefaultVerificationCodeService;
import com.microfoolish.it.account.signup.verficationcode.services.EmailVerificationCodeSender;
import com.microfoolish.it.account.signup.verficationcode.services.SmsVerificationCodeSender;
import com.microfoolish.it.account.signup.registration.services.RegistrationPropertyService;

/**
 *
 * @author kenny.he
 * @since 2026/09/04
 */
@AutoConfiguration(before = CasAccountManagementWebflowAutoConfiguration.class)
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
@EnableConfigurationMapping(classes = CasSignupCryptoProperties.class)
public class CasSignupAutoConfiguration {

    @Bean
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
    @ConditionalOnMissingBean(VerificationCodeCache.class)
    public VerificationCodeCache signupVerificationCodeCache(final ObjectProvider<StringRedisTemplate> redisTemplate) {
        return new CompositeVerificationCodeCache(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "emailVerificationCodeSender")
    public VerificationCodeSender emailVerificationCodeSender(
            final CasConfigurationProperties casProperties,
            final CommunicationsManager communicationsManager,
            @Qualifier(TenantExtractor.BEAN_NAME) final TenantExtractor tenantExtractor) {
        return new EmailVerificationCodeSender(casProperties, communicationsManager, tenantExtractor);
    }

    @Bean
    @ConditionalOnMissingBean(name = "smsVerificationCodeSender")
    public VerificationCodeSender smsVerificationCodeSender(
            final CasConfigurationProperties casProperties,
            final CommunicationsManager communicationsManager,
            @Qualifier(TenantExtractor.BEAN_NAME) final TenantExtractor tenantExtractor) {
        return new SmsVerificationCodeSender(casProperties, communicationsManager, tenantExtractor);
    }

    @Bean
    @ConditionalOnMissingBean(VerificationCodeService.class)
    public VerificationCodeService signupVerificationCodeService(
            final VerificationCodeCache cache,
            final List<VerificationCodeSender> senders) {
        return new DefaultVerificationCodeService(cache, senders);
    }

    @Bean
    @ConditionalOnBean({SignupAccountStore.class, RegistrationPropertyService.class})
    @ConditionalOnMissingBean(AccountRegistrationService.class)
    public AccountRegistrationService signupRegistrationService(
            final RegistrationPropertyService propertyService,
            final VerificationCodeService verificationCodeService,
            final SignupAccountStore accountStore,
            @Qualifier(org.apereo.cas.acct.AccountRegistrationService.BEAN_NAME) final org.apereo.cas.acct.AccountRegistrationService accountRegistrationService) {
        return new DefaultAccountRegistrationService(propertyService, verificationCodeService,
                accountStore, accountRegistrationService);
    }

    @Bean
    @ConditionalOnBean(AccountRegistrationService.class)
    @ConditionalOnMissingBean
    public SignupRegistrationController signupRegistrationController(
            final AccountRegistrationService registrationService) {
        return new SignupRegistrationController(registrationService);
    }

    @Bean
    @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
    @ConditionalOnMissingBean(name = CasWebflowConstants.ACTION_ID_ACCOUNT_REGISTRATION_SUBMIT)
    public Action submitAccountRegistrationAction(
            @Qualifier(TenantExtractor.BEAN_NAME) final TenantExtractor tenantExtractor,
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties,
            @Qualifier(org.apereo.cas.acct.AccountRegistrationService.BEAN_NAME)
            final org.apereo.cas.acct.AccountRegistrationService accountRegistrationService,
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
