/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfish.it.iam.login.signup.services;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apereo.cas.acct.AccountRegistrationProperty;

import org.springframework.transaction.annotation.Transactional;

import com.microfish.it.iam.login.signup.entity.JpaRegistrationPropertyEntity;
import com.microfish.it.iam.login.signup.entity.JpaRegistrationPropertyValueEntity;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyRepository;
import com.microfish.it.iam.login.signup.repository.JpaRegistrationPropertyValueRepository;
import com.microfoolish.it.account.signup.registration.AccountSignupProperty;
import com.microfoolish.it.account.signup.registration.AccountSignupPropertyValue;
import com.microfoolish.it.account.signup.registration.services.RegistrationPropertyService;

@Slf4j
public class JpaRegistrationPropertyService implements RegistrationPropertyService {

    private final JpaRegistrationPropertyRepository repository;

    private final JpaRegistrationPropertyValueRepository valueRepository;

    public JpaRegistrationPropertyService(
            final JpaRegistrationPropertyRepository repository,
            final JpaRegistrationPropertyValueRepository valueRepository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
        this.valueRepository = Objects.requireNonNull(valueRepository, "valueRepository must not be null");
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    public void save(final Map<String, AccountRegistrationProperty> map) {
        if (map == null) {
            return;
        }
        map.values().forEach(source -> {
            var entity = JpaRegistrationPropertyEntity.builder()
                .order(source.getOrder())
                .name(source.getName())
                .category(source instanceof AccountSignupProperty signup && signup.getCategory() != null
                    ? signup.getCategory() : "")
                .type(source.getType() == null ? "text" : source.getType())
                .pattern(source.getPattern() == null ? ".+" : source.getPattern())
                .required(source.isRequired() ? 1 : 0)
                .label(source.getLabel() == null ? source.getName() : source.getLabel())
                .title(source.getTitle() == null ? source.getName() : source.getTitle())
                .validationMessage(source.getValidationMessage() == null ? "Invalid value" : source.getValidationMessage())
                .build();
            var saved = repository.save(entity);
            if (source instanceof AccountSignupProperty signup) {
                var propertyValues = signup.getPropertyValues() == null ? java.util.List.<AccountSignupPropertyValue>of()
                    : signup.getPropertyValues();
                var values = propertyValues.stream().map(value -> {
                    var valueEntity = new JpaRegistrationPropertyValueEntity();
                    valueEntity.setCode(value.getCode());
                    valueEntity.setValue(value.getValue());
                    valueEntity.setEnabled(1);
                    valueEntity.setPropertyId(saved.getId());
                    return valueEntity;
                }).toList();
                valueRepository.saveAll(values);
                saved.setValues(new ArrayList<>(values));
            }
        });
    }

    @Override
    @Transactional(transactionManager = "transactionManager", readOnly = true)
    public Map<String, AccountRegistrationProperty> find() {
        var result = new LinkedHashMap<String, AccountRegistrationProperty>();
        repository.findAll().forEach(entity -> result.put(entity.getName(), toProperty(entity)));
        return result;
    }

    private static AccountSignupProperty toProperty(final JpaRegistrationPropertyEntity entity) {
        var values = entity.getValues() == null ? new ArrayList<AccountSignupPropertyValue>()
            : entity.getValues().stream()
            .filter(value -> value.getEnabled() != 0)
            .map(value -> new AccountSignupPropertyValue(value.getCode(), value.getValue()))
            .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        return AccountSignupProperty.builder()
            .order(entity.getOrder()).name(entity.getName()).category(entity.getCategory())
            .type(entity.getType()).pattern(entity.getPattern()).required(entity.getRequired() != 0)
            .label(entity.getLabel()).title(entity.getTitle()).validationMessage(entity.getValidationMessage())
            .propertyValues(values)
            .values(values.stream().map(AccountSignupPropertyValue::getValue).toList())
            .build();
    }
}
