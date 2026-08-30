/**
 * Copyright 2022 - Ren Jian Yan Huo
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

import org.springframework.core.annotation.AliasFor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ConfigurationMappingRegistrar.class)
public @interface EnableConfigurationMapping {

    /**
     * Base packages to scan. The value form is kept as the conventional alias
     * for {@link #basePackages()}.
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * Base packages to scan for {@link ConfigurationPropertiesMapping} types.
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * Explicit mapping types. Explicit types take precedence over package
     * scanning and must be annotated with {@link ConfigurationPropertiesMapping}.
     */
    Class<?>[] classes() default {};

    /**
     * Backwards-compatible spelling used by the first draft of this API.
     * Prefer {@link #basePackages()} for new code.
     */
    @Deprecated
    String[] basePackage() default {};
}
