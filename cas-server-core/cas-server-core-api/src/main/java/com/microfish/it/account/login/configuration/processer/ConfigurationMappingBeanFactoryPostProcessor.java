/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.List;

/**
 * Applies mappings discovered by an ImportBeanDefinitionRegistrar. This is a
 * fallback for mappings declared on configuration classes that are not direct
 * SpringApplication sources; it runs before configuration property beans are
 * instantiated.
 */
public final class ConfigurationMappingBeanFactoryPostProcessor
        implements BeanFactoryPostProcessor, EnvironmentAware {

    private final List<ConfigurationMappingDefinition> definitions;

    private ConfigurableEnvironment environment;

    public ConfigurationMappingBeanFactoryPostProcessor(List<ConfigurationMappingDefinition> definitions) {
        this.definitions = List.copyOf(definitions);
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment configurableEnvironment) {
            this.environment = configurableEnvironment;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (environment == null) {
            throw new IllegalStateException("Configuration mapping requires a ConfigurableEnvironment");
        }
        ConfigurationMappingEnvironment.apply(environment, definitions);
    }
}
