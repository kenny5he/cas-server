/**
 * Copyright 2026 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.login.configuration.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author kenny.he
 * @since 2026/08/30
 */
public class ConfigurationMappingRegistrar
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationMappingRegistrar.class);

    private static final String METADATA_COLLECTOR_BEAN_NAME = "propertiesMappingMetadataCollector";

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
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        registerSecurityResources(annotationMetadata, registry);
    }

    public void registerSecurityResources(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        LinkedHashSet<BeanDefinition> candidates = new LinkedHashSet<>();
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableConfigurationMapping.class.getName());
        Class<?>[] classes = attributes == null ? null : (Class<?>[]) attributes.get("basePackage");
        if (classes == null || classes.length == 0) {
            ClassPathScanningCandidateComponentProvider scanner = getScanner();
            scanner.setResourceLoader(resourceLoader);
            scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationPropertiesMapping.class));
            for (String basePackage : getBasePackages(metadata)) {
                candidates.addAll(scanner.findCandidateComponents(basePackage));
            }
        } else {
            for (Class<?> resourceClass : classes) {
                candidates.add(new AnnotatedGenericBeanDefinition(resourceClass));
            }
        }

        Set<String> mappingClassNames = new LinkedHashSet<>();
        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                AnnotationMetadata candidateMetadata = annotatedBeanDefinition.getMetadata();
                if (!candidateMetadata.hasAnnotation(ConfigurationPropertiesMapping.class.getName())) {
                    throw new IllegalStateException("Explicit security resource class "
                            + candidateMetadata.getClassName() + " must be annotated with @"
                            + ConfigurationPropertiesMapping.class.getSimpleName());
                }
                mappingClassNames.add(candidateMetadata.getClassName());
            }
        }

        registerInfrastructureBeans(metadata, mappingClassNames, registry);
        LOGGER.debug("Discovered {} configuration mapping classes", mappingClassNames.size());
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent()
                        && !beanDefinition.getMetadata().isAnnotation();
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableConfigurationMapping.class.getName());
        if (attributes == null) {
            throw new IllegalStateException("Missing @" + EnableConfigurationMapping.class.getSimpleName());
        }

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> type : (Class<?>[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(type));
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private void registerInfrastructureBeans(AnnotationMetadata importingMetadata,
                                             Set<String> resourceClassNames,
                                             BeanDefinitionRegistry registry) {
        registerIfMissing(PropertiesMappingRegistry.OPERATION_REGISTRY_BEAN_NAME, DefaultListablePropertiesMappingFactory.class, registry);
        if (resourceClassNames.isEmpty()) {
            return;
        }

        String collectorBeanName = METADATA_COLLECTOR_BEAN_NAME + importingMetadata.getClassName();
        if (!registry.containsBeanDefinition(collectorBeanName)) {
            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(PropertiesMappingMetadataCollector.class)
                    .addConstructorArgValue(new ArrayList<>(resourceClassNames))
                    .getBeanDefinition();
            definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            registry.registerBeanDefinition(collectorBeanName, definition);
        }
    }

    private void registerIfMissing(String beanName, Class<?> beanClass, BeanDefinitionRegistry registry) {
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        BeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(beanClass).getBeanDefinition();
        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
    }
}