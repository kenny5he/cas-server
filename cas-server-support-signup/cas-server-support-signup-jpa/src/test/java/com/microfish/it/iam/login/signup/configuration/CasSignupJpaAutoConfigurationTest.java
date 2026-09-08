/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfish.it.iam.login.signup.configuration;

import java.lang.reflect.Proxy;
import java.util.Map;

import junit.framework.TestCase;

import org.apereo.cas.acct.AccountRegistrationPropertyLoader;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import com.microfish.it.iam.login.jpa.configuration.CasJpaAutoConfiguration;
import com.microfish.it.iam.login.signup.repository.JpaAccountRegistrationRepository;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyRepository;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyValueRepository;
import com.microfoolish.it.account.signup.registration.SignupAccountStore;
import com.microfoolish.it.account.signup.registration.services.AccountRegistrationService;
import com.microfoolish.it.account.signup.registration.services.RegistrationPropertyService;

public class CasSignupJpaAutoConfigurationTest extends TestCase {

    public void testJpaRepositoriesAndServicesAreRegistered() {
        try (var context = new AnnotationConfigApplicationContext()) {
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "spring.datasource.url", "jdbc:h2:mem:signup;MODE=MySQL;DB_CLOSE_DELAY=-1",
                "spring.datasource.driver-class-name", "org.h2.Driver",
                "spring.jpa.hibernate.ddl-auto", "none")));
            context.register(TestDependencies.class, CasJpaAutoConfiguration.class,
                CasSignupJpaAutoConfiguration.class);
            context.refresh();

            assertNotNull(context.getBean(JpaRegistrationPropertyRepository.class));
            assertNotNull(context.getBean(JpaRegistrationPropertyValueRepository.class));
            assertNotNull(context.getBean(JpaAccountRegistrationRepository.class));
            assertNotNull(context.getBean(RegistrationPropertyService.class));
            assertNotNull(context.getBean(SignupAccountStore.class));
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class TestDependencies {

        @Bean(name = "accountMgmtRegistrationPropertyLoader")
        AccountRegistrationPropertyLoader accountRegistrationPropertyLoader() {
            return proxy(AccountRegistrationPropertyLoader.class);
        }

        @Bean
        AccountRegistrationService signupRegistrationService() {
            return proxy(AccountRegistrationService.class);
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(final Class<T> type) {
            return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            case "toString" -> type.getSimpleName() + " test proxy";
                            default -> null;
                        };
                    }
                    return null;
                });
        }
    }
}
