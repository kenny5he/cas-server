/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import com.microfish.it.account.login.configuration.annotation.EnableConfigurationMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Applies configuration aliases before the application context is refreshed.
 *
 * <p>The annotation registrar also installs a refresh-time processor. That
 * second path covers mappings declared on configuration classes that are not
 * direct SpringApplication sources.</p>
 */
public final class ConfigurationMappingPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    @Override
    public int getOrder() {
        // ConfigData and command-line properties must already be available.
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var definitions = discoverApplicationMappings(application, environment);
        ConfigurationMappingEnvironment.apply(environment, definitions);
    }

    private static List<ConfigurationMappingDefinition> discoverApplicationMappings(
            SpringApplication application, ConfigurableEnvironment environment) {
        var importingClasses = new LinkedHashSet<Class<?>>();
        if (application != null) {
            for (Object source : application.getAllSources()) {
                if (source instanceof Class<?> sourceClass) {
                    importingClasses.add(sourceClass);
                }
            }
            var mainClass = application.getMainApplicationClass();
            if (mainClass != null) {
                importingClasses.add(mainClass);
            }
        }

        var resourceLoader = application == null || application.getResourceLoader() == null
                ? new DefaultResourceLoader()
                : application.getResourceLoader();
        var definitions = new java.util.ArrayList<ConfigurationMappingDefinition>();
        for (Class<?> importingClass : importingClasses) {
            var enable = importingClass.getAnnotation(EnableConfigurationMapping.class);
            if (enable != null) {
                definitions.addAll(ConfigurationMappingScanner.scan(enable, importingClass, resourceLoader, environment));
            }
        }
        return deduplicate(definitions);
    }

    private static List<ConfigurationMappingDefinition> deduplicate(
            List<ConfigurationMappingDefinition> definitions) {
        var unique = new java.util.LinkedHashMap<String, ConfigurationMappingDefinition>();
        for (ConfigurationMappingDefinition definition : definitions) {
            var key = definition.mappingClass() + "|" + definition.sourcePrefix()
                    + "|" + definition.targetPrefix();
            unique.putIfAbsent(key, definition);
        }
        return List.copyOf(unique.values());
    }
}
