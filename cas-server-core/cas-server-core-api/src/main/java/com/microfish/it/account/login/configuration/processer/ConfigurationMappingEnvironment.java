/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Applies mapping definitions to an Environment without changing JVM-global
 * system properties.
 */
public final class ConfigurationMappingEnvironment {

    public static final String PROPERTY_SOURCE_NAME = "configurationMappingAliases";

    private ConfigurationMappingEnvironment() {
    }

    public static void apply(ConfigurableEnvironment environment,
                             Collection<ConfigurationMappingDefinition> definitions) {
        if (environment == null || definitions == null || definitions.isEmpty()) {
            return;
        }

        var propertySources = environment.getPropertySources();
        var aliases = new LinkedHashMap<String, Object>();
        var existing = propertySources.get(PROPERTY_SOURCE_NAME);
        if (existing instanceof MapPropertySource mapPropertySource) {
            aliases.putAll(mapPropertySource.getSource());
        }
        propertySources.remove(PROPERTY_SOURCE_NAME);

        var candidates = collectCanonicalPropertyNames(environment);
        for (ConfigurationMappingDefinition definition : definitions) {
            var sourceNames = new LinkedHashSet<String>();
            if (definition.mapsWholePrefix()) {
                sourceNames.addAll(candidates);
            } else {
                for (String path : definition.propertyPaths()) {
                    sourceNames.add(definition.sourceProperty(path));
                }
            }

            for (String sourceName : sourceNames) {
                var targetName = definition.map(sourceName);
                if (!StringUtils.hasText(targetName)) {
                    continue;
                }
                var value = getProperty(environment, sourceName);
                if (value == null) {
                    continue;
                }
                if (!definition.overrideExisting()
                        && !aliases.containsKey(targetName)
                        && getProperty(environment, targetName) != null) {
                    continue;
                }
                var previous = aliases.putIfAbsent(targetName, value);
                if (previous != null && !Objects.equals(previous, value)) {
                    throw new IllegalStateException("Conflicting configuration mappings for target property "
                            + targetName + ". Check mapping classes " + definition.mappingClass()
                            + " and the other mapping declaration.");
                }
            }
        }

        if (!aliases.isEmpty()) {
            propertySources.addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, aliases));
        }
    }

    private static Set<String> collectCanonicalPropertyNames(ConfigurableEnvironment environment) {
        var names = new LinkedHashSet<String>();
        for (ConfigurationPropertySource source : ConfigurationPropertySources.get(environment)) {
            if (source instanceof IterableConfigurationPropertySource iterable) {
                for (ConfigurationPropertyName name : iterable) {
                    names.add(name.toString());
                }
            }
        }
        return names;
    }

    private static Object getProperty(ConfigurableEnvironment environment, String propertyName) {
        var canonicalName = ConfigurationPropertyName.of(propertyName);
        for (ConfigurationPropertySource source : ConfigurationPropertySources.get(environment)) {
            ConfigurationProperty property = source.getConfigurationProperty(canonicalName);
            if (property != null) {
                return property.getValue();
            }
        }
        return null;
    }
}
