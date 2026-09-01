/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.microfish.it.account.login.configuration.processer;

import com.microfish.it.account.login.configuration.annotation.ConfigurationPropertiesMapping;
import com.microfish.it.account.login.configuration.annotation.EnableConfigurationMapping;
import com.microfish.it.account.login.configuration.annotation.PropertyMapping;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Discovers mapping declarations without instantiating mapping classes.
 */
public final class ConfigurationMappingScanner {

    private ConfigurationMappingScanner() {
    }

    public static List<ConfigurationMappingDefinition> scan(
            AnnotationMetadata importingMetadata, ResourceLoader resourceLoader, Environment environment) {
        var attributes = importingMetadata.getAnnotationAttributes(EnableConfigurationMapping.class.getName());
        if (attributes == null) {
            throw new IllegalStateException("Missing @" + EnableConfigurationMapping.class.getSimpleName());
        }

        var explicitClasses = new LinkedHashSet<Class<?>>();
        addClasses(explicitClasses, attributes.get("classes"));
        var basePackages = new LinkedHashSet<String>();
        addPackages(basePackages, attributes.get("value"));
        addPackages(basePackages, attributes.get("basePackages"));
        addPackages(basePackages, attributes.get("basePackage"));
        if (basePackages.isEmpty() && explicitClasses.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingMetadata.getClassName()));
        }
        return scan(explicitClasses, basePackages, resourceLoader, environment);
    }

    public static List<ConfigurationMappingDefinition> scan(
            EnableConfigurationMapping enable, Class<?> importingClass, ResourceLoader resourceLoader,
            Environment environment) {
        var explicitClasses = new LinkedHashSet<Class<?>>(Arrays.asList(enable.classes()));
        var basePackages = new LinkedHashSet<String>();
        addPackages(basePackages, enable.value());
        addPackages(basePackages, enable.basePackages());
        addPackages(basePackages, enable.basePackage());
        if (basePackages.isEmpty() && explicitClasses.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClass));
        }
        return scan(explicitClasses, basePackages, resourceLoader, environment);
    }

    public static List<ConfigurationMappingDefinition> scan(
            Collection<Class<?>> explicitClasses, Collection<String> basePackages,
            ResourceLoader resourceLoader, Environment environment) {
        var mappingClasses = new LinkedHashMap<String, Class<?>>();
        if (explicitClasses != null && !explicitClasses.isEmpty()) {
            for (Class<?> mappingClass : explicitClasses) {
                if (mappingClass == null) {
                    continue;
                }
                requireMappingAnnotation(mappingClass);
                mappingClasses.put(mappingClass.getName(), mappingClass);
            }
        } else {
            var loader = resourceLoader == null ? new DefaultResourceLoader() : resourceLoader;
            var scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    return beanDefinition.getMetadata().isIndependent()
                            && !beanDefinition.getMetadata().isAnnotation();
                }
            };
            scanner.setResourceLoader(loader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationPropertiesMapping.class));
            var packages = basePackages == null ? Set.<String>of() : basePackages;
            for (String basePackage : packages) {
                if (!StringUtils.hasText(basePackage)) {
                    continue;
                }
                for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                    var className = candidate.getBeanClassName();
                    if (className == null) {
                        continue;
                    }
                    try {
                        var mappingClass = ClassUtils.forName(className, loader.getClassLoader());
                        mappingClasses.put(className, mappingClass);
                    } catch (ClassNotFoundException | LinkageError ex) {
                        throw new IllegalStateException("Cannot load configuration mapping class " + className, ex);
                    }
                }
            }
        }

        return mappingClasses.values().stream()
                .sorted(Comparator.comparing(Class::getName))
                .map(ConfigurationMappingScanner::toDefinition)
                .toList();
    }

    private static ConfigurationMappingDefinition toDefinition(Class<?> mappingClass) {
        var mapping = mappingClass.getAnnotation(ConfigurationPropertiesMapping.class);
        requireMappingAnnotation(mappingClass);
        var propertyPaths = new LinkedHashSet<String>();
        for (Class<?> current = mappingClass; current != null && current != Object.class;
             current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && !field.isSynthetic()) {
                    var propertyMapping = field.getAnnotation(PropertyMapping.class);
                    addPropertyPath(propertyPaths, propertyMapping == null
                            ? kebabCase(field.getName()) : propertyMapping.property());
                }
            }
            for (Method method : current.getDeclaredMethods()) {
                if (!method.isBridge() && !method.isSynthetic()) {
                    var methodMapping = method.getAnnotation(PropertyMapping.class);
                    if (methodMapping != null) {
                        addPropertyPath(propertyPaths, methodMapping.property());
                    }
                }
            }
            if (current.isRecord()) {
                for (RecordComponent component : current.getRecordComponents()) {
                    var componentMapping = component.getAnnotation(PropertyMapping.class);
                    if (componentMapping != null) {
                        addPropertyPath(propertyPaths, componentMapping.property());
                    }
                }
            }
        }
        return new ConfigurationMappingDefinition(
                mapping.prefix(), mapping.casPrefix(), propertyPaths,
                mapping.overrideExisting(), mappingClass.getName());
    }

    private static void addPropertyPath(Set<String> propertyPaths, String propertyPath) {
        if (!StringUtils.hasText(propertyPath)) {
            throw new IllegalStateException("Mapped configuration property must not be blank");
        }
        propertyPaths.add(propertyPath);
    }

    private static String kebabCase(String propertyName) {
        var result = new StringBuilder(propertyName.length() + 8);
        for (int i = 0; i < propertyName.length(); i++) {
            var character = propertyName.charAt(i);
            if (character == '_') {
                result.append('-');
                continue;
            }
            if (Character.isUpperCase(character) && i > 0
                    && (Character.isLowerCase(propertyName.charAt(i - 1))
                    || Character.isDigit(propertyName.charAt(i - 1)))) {
                result.append('-');
            }
            result.append(Character.toLowerCase(character));
        }
        return result.toString();
    }

    private static void requireMappingAnnotation(Class<?> mappingClass) {
        if (mappingClass.getAnnotation(ConfigurationPropertiesMapping.class) == null) {
            throw new IllegalStateException("Explicit mapping class " + mappingClass.getName()
                    + " must be annotated with @" + ConfigurationPropertiesMapping.class.getSimpleName());
        }
    }

    private static void addPackages(Set<String> target, Object value) {
        if (value instanceof String[] packages) {
            for (String pkg : packages) {
                if (StringUtils.hasText(pkg)) {
                    target.add(pkg.trim());
                }
            }
        }
    }

    private static void addClasses(Set<Class<?>> target, Object value) {
        if (value instanceof Class<?>[] classes) {
            target.addAll(Arrays.asList(classes));
        }
    }
}
