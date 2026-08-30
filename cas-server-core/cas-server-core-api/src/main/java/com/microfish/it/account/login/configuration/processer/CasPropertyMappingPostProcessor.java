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

package com.microfish.it.account.configuration.processer;

import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.ConfigurableEnvironment;

import com.microfish.it.account.configuration.annotation.ConfigurationPropertiesMapping;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 * @author kenny.he
 * @since 2026/08/30
 */
public class CasPropertyMappingPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 1. 扫描带有 @CasPropertyMapping 注解的类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigurationPropertiesMapping.class));

        // 假设注解主要打在 config 包下，可根据实际包路径调整
        Set<?> components = scanner.findCandidateComponents("com.micorfish.it.account.cas.configuration");

        for (Object component : components) {
            try {
                Class<?> clazz = Class.forName(component.getBeanClassName());
                ConfigurationPropertiesMapping mapping = clazz.getAnnotation(ConfigurationPropertiesMapping.class);

                // 2. 遍历新前缀下的所有属性，强制覆盖到旧前缀
                environment.getPropertySources().forEach(propertySource -> {
                    if (propertySource instanceof EnumerablePropertySource) {
                        EnumerablePropertySource<?> eps =
                                (EnumerablePropertySource<?>) propertySource;
                        for (String propName : eps.getPropertyNames()) {
                            if (propName.startsWith(mapping.prefix())) {
                                String oldPropName = propName.replace(mapping.prefix(), mapping.casPrefix());
                                String value = environment.getProperty(propName);
                                if (StringUtils.hasText(value)) {
                                    System.setProperty(oldPropName, value);
                                }
                            }
                        }
                    }
                });
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to process Configuration Properties Mapping", e);
            }
        }
    }
}
