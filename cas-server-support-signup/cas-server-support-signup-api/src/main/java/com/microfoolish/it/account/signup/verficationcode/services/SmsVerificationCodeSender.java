/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode.services;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.notifications.sms.SmsBodyBuilder;
import org.apereo.cas.notifications.sms.SmsRequest;

import org.springframework.util.StringUtils;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeSender;

@RequiredArgsConstructor
public class SmsVerificationCodeSender implements VerificationCodeSender {
    private final CasConfigurationProperties casProperties;
    private final CommunicationsManager communicationsManager;
    private final TenantExtractor tenantExtractor;

    @Override
    public RegistrationType type() {
        return RegistrationType.PHONE;
    }

    @Override
    public void send(final String target, final String code) {
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException("Phone number is required");
        }
        var properties = casProperties.getAccountRegistration().getSms();
        var text = properties.getText() != null && properties.getText().contains("${code}")
            ? SmsBodyBuilder.builder().properties(properties).parameters(Map.of("code", code)).build().get()
            : "Your verification code is " + code;
        var tenant = tenantExtractor == null ? "" : tenantExtractor.extract((String) null)
            .map(tenantDefinition -> tenantDefinition.getId()).orElse("");
        var sent = communicationsManager.sms(SmsRequest.builder()
            .from(properties.getFrom())
            .to(List.of(target))
            .tenant(tenant)
            .text(text)
            .build());
        if (!sent) {
            throw new IllegalStateException("Unable to send the verification SMS");
        }
    }
}
