/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import com.microfish.it.account.login.configuration.annotation.ConfigurationPropertiesMapping;
import com.microfish.it.account.login.configuration.annotation.EnableConfigurationMapping;
import com.microfish.it.account.login.configuration.annotation.PropertyMapping;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurationMappingTest {

    private static final String SOURCE_PREFIX = "account.registration";

    private static final String TARGET_PREFIX = "cas.account-registration.core";

    @Test
    void mapsExplicitAndFieldDerivedPropertiesAndOverridesCasValue() {
        var environment = environment(Map.of(
                "account.registration.crypto.encryption.key", "custom-key",
                "account.registration.expiration", "PT10M"),
                Map.of(
                        "cas.account-registration.core.crypto.encryption.key", "old-key",
                        "cas.account-registration.core.expiration", "PT5M"));

        var definitions = ConfigurationMappingScanner.scan(
                List.of(AccountRegistrationMapping.class), List.of(),
                new DefaultResourceLoader(), environment);
        ConfigurationMappingEnvironment.apply(environment, definitions);

        assertEquals("custom-key", environment.getProperty(
                "cas.account-registration.core.crypto.encryption.key"));
        assertEquals("PT10M", environment.getProperty(
                "cas.account-registration.core.expiration"));
        assertEquals(List.of("crypto.encryption.key", "expiration"),
                definitions.getFirst().propertyPaths().stream().sorted().toList());
        assertEquals(ConfigurationMappingEnvironment.PROPERTY_SOURCE_NAME,
                environment.getPropertySources().iterator().next().getName());
    }

    @Test
    void mapsWholePrefixWhenMappingClassHasNoProperties() {
        var environment = environment(Map.of(
                "account.registration.crypto.encryption.key", "custom-key",
                "account.registration.mail.subject", "Subject"),
                Map.of("cas.account-registration.core.mail.subject", "Old subject"));

        var definitions = ConfigurationMappingScanner.scan(
                List.of(WholePrefixMapping.class), List.of(),
                new DefaultResourceLoader(), environment);
        assertTrue(definitions.getFirst().mapsWholePrefix());

        ConfigurationMappingEnvironment.apply(environment, definitions);

        assertEquals("custom-key", environment.getProperty(
                "cas.account-registration.core.crypto.encryption.key"));
        assertEquals("Subject", environment.getProperty(
                "cas.account-registration.core.mail.subject"));
    }

    @Test
    void scansMappingTypesFromConfiguredBasePackage() {
        var definitions = ConfigurationMappingScanner.scan(
                List.of(),
                List.of("com.microfish.it.account.login.configuration.processer"),
                new DefaultResourceLoader(), new StandardEnvironment());

        assertTrue(definitions.stream().anyMatch(definition ->
                definition.mappingClass().equals(AccountRegistrationMapping.class.getName())));
    }

    @Test
    void doesNotMapAPropertyOutsideTheDeclaredPrefix() {
        var definition = new ConfigurationMappingDefinition(
                SOURCE_PREFIX, TARGET_PREFIX, Set.of("expiration"), true, "test");

        assertEquals(TARGET_PREFIX + ".expiration", definition.map(
                SOURCE_PREFIX + ".expiration"));
        assertNull(definition.map("account.registration-extra.expiration"));
        assertNull(definition.map(SOURCE_PREFIX + ".mail.subject"));
    }

    @Test
    void canPreserveExistingCasPropertyWhenOverrideIsDisabled() {
        var environment = environment(Map.of(
                "account.registration.expiration", "PT10M"),
                Map.of("cas.account-registration.core.expiration", "PT5M"));
        var definition = new ConfigurationMappingDefinition(
                SOURCE_PREFIX, TARGET_PREFIX, Set.of("expiration"), false, "test");

        ConfigurationMappingEnvironment.apply(environment, List.of(definition));

        assertEquals("PT5M", environment.getProperty(
                "cas.account-registration.core.expiration"));
    }

    @Test
    void rejectsConflictingMappingsForTheSameTarget() {
        var environment = environment(Map.of(
                "account.registration.expiration", "PT10M",
                "profile.registration.expiration", "PT20M"), Map.of());
        var first = new ConfigurationMappingDefinition(
                SOURCE_PREFIX, TARGET_PREFIX, Set.of("expiration"), true, "first");
        var second = new ConfigurationMappingDefinition(
                "profile.registration", TARGET_PREFIX, Set.of("expiration"), true, "second");

        assertThrows(IllegalStateException.class,
                () -> ConfigurationMappingEnvironment.apply(environment, List.of(first, second)));
    }

    @Test
    void registrarRegistersMetadataAndAppliesMappingBeforeBeanCreation() {
        var environment = environment(Map.of(
                "account.registration.crypto.encryption.key", "custom-key"),
                Map.of("cas.account-registration.core.crypto.encryption.key", "old-key"));
        var context = new AnnotationConfigApplicationContext();
        context.setEnvironment(environment);
        context.register(ImportingConfiguration.class);

        try {
            context.refresh();

            assertEquals("custom-key", context.getEnvironment().getProperty(
                    "cas.account-registration.core.crypto.encryption.key"));
            assertFalse(context.getBeansOfType(ConfigurationMappingMetadataRegistry.class).isEmpty());
        } finally {
            context.close();
        }
    }

    @Test
    void environmentPostProcessorDiscoversMappingsFromApplicationSources() {
        var environment = environment(Map.of(
                "account.registration.crypto.encryption.key", "custom-key"),
                Map.of("cas.account-registration.core.crypto.encryption.key", "old-key"));
        var application = new SpringApplication(ImportingConfiguration.class);

        new ConfigurationMappingPostProcessor().postProcessEnvironment(environment, application);

        assertEquals("custom-key", environment.getProperty(
                "cas.account-registration.core.crypto.encryption.key"));
    }

    private static StandardEnvironment environment(Map<String, Object> custom, Map<String, Object> old) {
        var environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("custom", custom));
        if (!old.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource("old", old));
        }
        return environment;
    }

    @ConfigurationPropertiesMapping(prefix = SOURCE_PREFIX, casPrefix = TARGET_PREFIX)
    static class AccountRegistrationMapping {
        @PropertyMapping(property = "crypto.encryption.key")
        private String encryptionKey;

        private String expiration;
    }

    @ConfigurationPropertiesMapping(prefix = SOURCE_PREFIX, casPrefix = TARGET_PREFIX)
    static class WholePrefixMapping {
    }

    @Configuration
    @EnableConfigurationMapping(classes = AccountRegistrationMapping.class)
    static class ImportingConfiguration {
    }
}
