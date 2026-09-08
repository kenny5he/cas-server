/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfish.it.iam.login.signup.services;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Objects;

import org.apereo.cas.acct.AccountRegistrationRequest;

import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.registration.SignupAccountStore;
import com.microfish.it.iam.login.signup.entity.JpaAccountRegistrationEntity;
import com.microfish.it.iam.login.signup.repository.JpaAccountRegistrationRepository;


@Slf4j
public class JpaSignupAccountStore implements SignupAccountStore {

    private final JpaAccountRegistrationRepository repository;

    public JpaSignupAccountStore(final JpaAccountRegistrationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public boolean existsByEmail(final String email) {
        return StringUtils.hasText(email) && repository.existsByEmailIgnoreCase(email.trim());
    }

    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public boolean existsByPhone(final String callingCode, final String phoneNumber) {
        return StringUtils.hasText(phoneNumber)
            && repository.existsByCallingCodeAndPhoneNumber(normalizeCallingCode(callingCode), normalizePhone(phoneNumber));
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public String save(final AccountRegistrationRequest request, final RegistrationType type) {
        var target = type == RegistrationType.EMAIL ? text(request, "email") : text(request, "phone", "phoneNumber", "phone_number");
        var callingCode = text(request, "callingCode", "calling_code");
        var firstName = text(request, "firstName", "first_name");
        var lastName = text(request, "lastName", "last_name");
        var name = text(request, "name");
        if (!StringUtils.hasText(name)) {
            name = (firstName + " " + lastName).trim();
        }
        if (!StringUtils.hasText(name)) {
            name = target;
        }
        var code = text(request, "username");
        if (!StringUtils.hasText(code)) {
            code = target;
        }
        var now = LocalDateTime.now();
        var entity = new JpaAccountRegistrationEntity()
            .setCode(code).setName(name).setNikeName(text(request, "nikeName", "nickname", "nike_name"))
            .setFirstName(firstName).setLastName(lastName).setGender(integer(request, "gender"))
            .setPassword(text(request, "password"))
            .setExpired(0).setType(text(request, "type"))
            .setEnabled(1).setEffectiveTime(now).setCreationDate(now).setLastUpdateDate(now)
            .setCallingCode(type == RegistrationType.PHONE ? normalizeCallingCode(callingCode) : null)
            .setPhoneNumber(type == RegistrationType.PHONE ? normalizePhone(target) : null)
            .setEmail(type == RegistrationType.EMAIL ? target.toLowerCase(java.util.Locale.ROOT) : null)
            .setEmailReverse(type == RegistrationType.EMAIL ? new StringBuilder(target).reverse().toString() : null);
        repository.save(entity);
        return code;
    }

    private static String text(final AccountRegistrationRequest request, final String... names) {
        for (var name : names) {
            var value = request.getProperties().get(name);
            if (value != null && StringUtils.hasText(value.toString())) {
                return value.toString().trim();
            }
        }
        return "";
    }

    private static Integer integer(final AccountRegistrationRequest request, final String name) {
        var value = text(request, name);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    private static String normalizeCallingCode(final String value) {
        return StringUtils.hasText(value) ? value.trim() : "";
    }

    private static String normalizePhone(final String value) {
        return value == null ? "" : value.trim().replaceAll("[\\s-]", "");
    }
}
