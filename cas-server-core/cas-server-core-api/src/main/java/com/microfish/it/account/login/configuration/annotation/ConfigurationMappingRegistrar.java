/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.annotation;

import com.microfish.it.account.login.configuration.processer.ConfigurationMappingBeanFactoryPostProcessor;
import com.microfish.it.account.login.configuration.processer.ConfigurationMappingDefinition;
import com.microfish.it.account.login.configuration.processer.ConfigurationMappingMetadataRegistry;
import com.microfish.it.account.login.configuration.processer.ConfigurationMappingScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Discovers mappings declared by {@link EnableConfigurationMapping} and
 * registers their metadata and a refresh-time fallback processor.
 */
public class ConfigurationMappingRegistrar
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationMappingRegistrar.class);

    private static final String REGISTRY_BEAN_PREFIX = "configurationMappingMetadataRegistry.";

    private static final String POST_PROCESSOR_BEAN_PREFIX = "configurationMappingBeanFactoryPostProcessor.";

    private Environment environment;

    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingMetadata, BeanDefinitionRegistry registry) {
        var definitions = ConfigurationMappingScanner.scan(importingMetadata, resourceLoader, environment);
        if (definitions.isEmpty()) {
            LOGGER.debug("No configuration mapping classes discovered for {}", importingMetadata.getClassName());
            return;
        }

        var suffix = Integer.toHexString(importingMetadata.getClassName().hashCode());
        registerInfrastructureBean(
                REGISTRY_BEAN_PREFIX + suffix,
                ConfigurationMappingMetadataRegistry.class,
                definitions,
                registry);
        registerInfrastructureBean(
                POST_PROCESSOR_BEAN_PREFIX + suffix,
                ConfigurationMappingBeanFactoryPostProcessor.class,
                definitions,
                registry);
        LOGGER.debug("Discovered {} configuration mapping classes for {}", definitions.size(),
                importingMetadata.getClassName());
    }

    private void registerInfrastructureBean(String beanName, Class<?> beanClass,
                                            List<ConfigurationMappingDefinition> definitions,
                                            BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        var definition = BeanDefinitionBuilder.genericBeanDefinition(beanClass)
                .addConstructorArgValue(new ArrayList<>(definitions))
                .getBeanDefinition();
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
    }
}
