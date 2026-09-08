/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.registration.services;

import com.microfoolish.it.account.signup.registration.AccountSignupProperty;
import com.microfoolish.it.account.signup.registration.RegistrationTokenResult;
import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.registration.RegistrationVerificationRequest;
import com.microfoolish.it.account.signup.registration.SignupAccountStore;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeResult;
import com.microfoolish.it.account.signup.verficationcode.services.VerificationCodeService;
import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author kenny.he
 * @since 2026/09/06
 */
public class DefaultAccountRegistrationService implements AccountRegistrationService {
    private final RegistrationPropertyService propertyService;
    private final VerificationCodeService verificationCodeService;
    private final SignupAccountStore accountStore;
    private final org.apereo.cas.acct.AccountRegistrationService accountRegistrationService;

    public DefaultAccountRegistrationService(
            final RegistrationPropertyService propertyService,
            final VerificationCodeService verificationCodeService,
            final SignupAccountStore accountStore,
            final org.apereo.cas.acct.AccountRegistrationService accountRegistrationService) {
        this.propertyService = propertyService;
        this.verificationCodeService = verificationCodeService;
        this.accountStore = accountStore;
        this.accountRegistrationService = accountRegistrationService;
    }

    @Override
    public List<AccountSignupProperty> loadProperties(final RegistrationType type) {
        var loaded = propertyService.find();
        if (loaded.isEmpty() && accountRegistrationService != null) {
            loaded = accountRegistrationService.getAccountRegistrationPropertyLoader().load();
        }
        return loaded.values().stream()
            .filter(property -> isCommon(property) || matches(property, type))
            .sorted(Comparator.comparing(AccountRegistrationProperty::getOrder))
            .map(property -> property instanceof AccountSignupProperty signupProperty
                ? signupProperty : copy(property))
            .toList();
    }

    @Override
    public VerificationCodeResult sendVerificationCode(final VerificationCodeRequest request) throws Exception {
        return verificationCodeService.send(request);
    }

    @Override
    public RegistrationTokenResult verifyAndRegister(final RegistrationVerificationRequest request) {
        if (request == null || request.properties().isEmpty()) {
            throw new IllegalArgumentException("Registration properties are required");
        }
        var type = request.type();
        var properties = request.properties();
        var target = target(type, properties);
        validateChannel(type, properties);
        validateConfiguredProperties(type, properties);
        if (type == RegistrationType.EMAIL && accountStore.existsByEmail(target)) {
            throw new IllegalArgumentException("Email address is already registered");
        }
        if (type == RegistrationType.PHONE) {
            var callingCode = text(properties, "callingCode", "calling_code");
            if (accountStore.existsByPhone(callingCode, target)) {
                throw new IllegalArgumentException("Phone number is already registered");
            }
        }
        var verificationTarget = type == RegistrationType.PHONE
            ? phoneTarget(callingCode(properties), target) : target;
        if (!verificationCodeService.verify(type, verificationTarget, request.verificationCode())) {
            throw new IllegalArgumentException("Verification code is invalid or expired");
        }
        var registrationRequest = new AccountRegistrationRequest(properties);
        registrationRequest.putProperty("registrationType", type.name().toLowerCase(Locale.ROOT));
        accountRegistrationService.getAccountRegistrationRequestValidator().validate(registrationRequest);
        accountStore.save(registrationRequest, type);
        var token = accountRegistrationService.createToken(registrationRequest);
        return new RegistrationTokenResult(token, Instant.now());
    }

    private void validateConfiguredProperties(final RegistrationType type, final Map<String, Object> properties) {
        loadProperties(type).forEach(property -> {
            var value = text(properties, property.getName());
            if (property.isRequired() && !StringUtils.hasText(value)) {
                throw new IllegalArgumentException("Registration property is required: " + property.getName());
            }
            if (StringUtils.hasText(value) && StringUtils.hasText(property.getPattern()) && !value.matches(property.getPattern())) {
                throw new IllegalArgumentException(property.getValidationMessage());
            }
            if (StringUtils.hasText(value) && "select".equalsIgnoreCase(property.getType())
                    && property.getValues() != null && !property.getValues().isEmpty()
                    && !property.getValues().contains(value)) {
                throw new IllegalArgumentException("Invalid value for registration property: " + property.getName());
            }
        });
    }

    private static void validateChannel(final RegistrationType type, final Map<String, Object> properties) {
        var email = text(properties, "email");
        var phone = text(properties, "phone", "phoneNumber", "phone_number");
        if (type == RegistrationType.EMAIL && (!StringUtils.hasText(email) || StringUtils.hasText(phone))) {
            throw new IllegalArgumentException("Only email registration data may be submitted");
        }
        if (type == RegistrationType.PHONE && (!StringUtils.hasText(phone) || StringUtils.hasText(email))) {
            throw new IllegalArgumentException("Only phone registration data may be submitted");
        }
    }

    private static String target(final RegistrationType type, final Map<String, Object> properties) {
        var target = type == RegistrationType.EMAIL ? text(properties, "email")
            : text(properties, "phone", "phoneNumber", "phone_number");
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException("Registration target is required");
        }
        return target.trim();
    }

    private static String callingCode(final Map<String, Object> properties) {
        return text(properties, "callingCode", "calling_code");
    }

    private static String phoneTarget(final String callingCode, final String phone) {
        var normalizedPhone = phone.trim().replaceAll("[\\s-]", "");
        if (normalizedPhone.startsWith("+") || !StringUtils.hasText(callingCode)) {
            return normalizedPhone;
        }
        var normalizedCode = callingCode.trim().replaceAll("[\\s-]", "");
        return (normalizedCode.startsWith("+") ? normalizedCode : "+" + normalizedCode) + normalizedPhone;
    }

    private static String text(final Map<String, Object> values, final String... names) {
        for (var name : names) {
            var value = values.get(name);
            if (value != null && StringUtils.hasText(value.toString())) {
                return value.toString().trim();
            }
        }
        return "";
    }

    private static boolean isCommon(final AccountRegistrationProperty property) {
        return !(property instanceof AccountSignupProperty signupProperty)
            || !StringUtils.hasText(signupProperty.getCategory());
    }

    private static boolean matches(final AccountRegistrationProperty property, final RegistrationType type) {
        var category = ((AccountSignupProperty) property).getCategory().toLowerCase(Locale.ROOT);
        return type == RegistrationType.EMAIL
            ? category.equals("email") || category.equals("mail")
            : category.equals("phone") || category.equals("mobile") || category.equals("mobilephone") || category.equals("sms");
    }

    private static AccountSignupProperty copy(final AccountRegistrationProperty source) {
        return AccountSignupProperty.builder()
            .order(source.getOrder()).name(source.getName()).type(source.getType()).pattern(source.getPattern())
            .required(source.isRequired()).label(source.getLabel()).cssClass(source.getCssClass())
            .title(source.getTitle()).validationMessage(source.getValidationMessage()).values(source.getValues())
            .category(null)
            .build();
    }
}
