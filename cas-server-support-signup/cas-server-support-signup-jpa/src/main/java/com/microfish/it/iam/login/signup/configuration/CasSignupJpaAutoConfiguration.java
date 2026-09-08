/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfish.it.iam.login.signup.configuration;

import org.apereo.cas.acct.AccountRegistrationPropertyLoader;
import org.apereo.cas.config.CasAccountManagementWebflowAutoConfiguration;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.microfish.it.iam.login.signup.repository.JpaAccountRegistrationRepository;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyRepository;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyValueRepository;
import com.microfish.it.iam.login.signup.services.JpaRegistrationPropertyService;
import com.microfish.it.iam.login.signup.services.JpaSignupAccountStore;
import com.microfish.it.iam.login.jpa.configuration.CasJpaAutoConfiguration;
import com.microfoolish.it.account.signup.registration.SignupAccountStore;
import com.microfoolish.it.account.signup.registration.services.AccountRegistrationService;
import com.microfoolish.it.account.signup.verficationcode.services.VerificationCodeService;
import com.microfoolish.it.account.signup.registration.services.DefaultAccountRegistrationService;
import com.microfoolish.it.account.signup.registration.services.RegistrationPropertyService;
import com.microfoolish.it.account.signup.configuration.CasSignupAutoConfiguration;
import com.microfoolish.it.account.signup.registration.JpaAccountSignupPropertyLoader;

@AutoConfiguration
@AutoConfigureAfter({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class,
    CasJpaAutoConfiguration.class, CasSignupAutoConfiguration.class})
@AutoConfigureBefore(CasAccountManagementWebflowAutoConfiguration.class)
@ImportAutoConfiguration({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableConfigurationProperties(AccountSignupJpaProperties.class)
@EnableJpaRepositories(basePackageClasses = JpaRegistrationPropertyRepository.class)
public class CasSignupJpaAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(RegistrationPropertyService.class)
    public RegistrationPropertyService registrationPropertyService(
            final JpaRegistrationPropertyRepository repository,
            final JpaRegistrationPropertyValueRepository valueRepository) {
        return new JpaRegistrationPropertyService(repository, valueRepository);
    }

    @Bean(name = "accountMgmtRegistrationPropertyLoader")
    @ConditionalOnMissingBean(name = "accountMgmtRegistrationPropertyLoader")
    public AccountRegistrationPropertyLoader accountMgmtRegistrationPropertyLoader(
            final RegistrationPropertyService propertyService,
            final CasConfigurationProperties casProperties) {
        return new JpaAccountSignupPropertyLoader(propertyService,
            casProperties.getAccountRegistration().getCore().getRegistrationProperties().getLocation());
    }

    @Bean
    @ConditionalOnMissingBean(SignupAccountStore.class)
    public SignupAccountStore signupAccountStore(final JpaAccountRegistrationRepository repository) {
        return new JpaSignupAccountStore(repository);
    }

    @Bean
    @ConditionalOnMissingBean(AccountRegistrationService.class)
    public AccountRegistrationService signupRegistrationService(
            final RegistrationPropertyService propertyService,
            final VerificationCodeService verificationCodeService,
            final SignupAccountStore accountStore,
            @Qualifier(org.apereo.cas.acct.AccountRegistrationService.BEAN_NAME) final org.apereo.cas.acct.AccountRegistrationService accountRegistrationService) {
        return new DefaultAccountRegistrationService(propertyService, verificationCodeService,
            accountStore, accountRegistrationService);
    }
}
