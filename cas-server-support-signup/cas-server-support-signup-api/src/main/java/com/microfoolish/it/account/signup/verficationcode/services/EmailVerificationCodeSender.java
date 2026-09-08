/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfoolish.it.account.signup.verficationcode.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.mail.EmailMessageBodyBuilder;
import org.apereo.cas.notifications.mail.EmailMessageRequest;

import org.springframework.util.StringUtils;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeSender;

public class EmailVerificationCodeSender implements VerificationCodeSender {
    private final CasConfigurationProperties casProperties;
    private final CommunicationsManager communicationsManager;
    private final TenantExtractor tenantExtractor;

    public EmailVerificationCodeSender(final CasConfigurationProperties casProperties,
                                       final CommunicationsManager communicationsManager) {
        this(casProperties, communicationsManager, null);
    }

    public EmailVerificationCodeSender(final CasConfigurationProperties casProperties,
                                       final CommunicationsManager communicationsManager,
                                       final TenantExtractor tenantExtractor) {
        this.casProperties = casProperties;
        this.communicationsManager = communicationsManager;
        this.tenantExtractor = tenantExtractor;
    }

    @Override
    public RegistrationType type() {
        return RegistrationType.EMAIL;
    }

    @Override
    public void send(final String target, final String code) {
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException("Email address is required");
        }
        var properties = casProperties.getAccountRegistration().getMail();
        var body = properties.getText() != null && properties.getText().contains("${code}")
            ? EmailMessageBodyBuilder.builder().properties(properties).parameters(Map.of("code", code))
                .locale(java.util.Optional.of(Locale.getDefault())).build().get()
            : "Your verification code is " + code;
        var builder = EmailMessageRequest.builder()
            .emailProperties(properties)
            .locale(Locale.getDefault())
            .to(List.of(target))
            .body(body);
        if (tenantExtractor != null) {
            builder.tenant(tenantExtractor.extract((String) null)
                .map(tenantDefinition -> tenantDefinition.getId()).orElse(""));
        }
        var result = communicationsManager.email(builder.build());
        if (!result.isSuccess()) {
            throw new IllegalStateException("Unable to send the verification email");
        }
    }
}
