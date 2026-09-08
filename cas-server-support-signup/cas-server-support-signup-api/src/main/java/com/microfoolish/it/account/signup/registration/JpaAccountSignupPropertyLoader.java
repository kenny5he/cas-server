/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfoolish.it.account.signup.registration;

import java.util.Map;
import java.util.LinkedHashMap;

import org.apereo.cas.acct.AccountRegistrationProperty;
import org.apereo.cas.acct.AccountRegistrationPropertyLoader;
import org.apereo.cas.acct.DefaultAccountRegistrationPropertyLoader;
import org.springframework.core.io.Resource;

import com.microfoolish.it.account.signup.registration.services.RegistrationPropertyService;

/**
 * Loads account-registration properties from JPA storage.
 *
 * @author kenny
 * @since 7.3.0
 */
public class JpaAccountSignupPropertyLoader implements AccountRegistrationPropertyLoader {

    private final RegistrationPropertyService registeredSelectionService;

    private final Resource fallbackResource;

    public JpaAccountSignupPropertyLoader(final RegistrationPropertyService registeredSelectionService) {
        this(registeredSelectionService, null);
    }

    public JpaAccountSignupPropertyLoader(final RegistrationPropertyService registeredSelectionService,
                                          final Resource fallbackResource) {
        this.registeredSelectionService = registeredSelectionService;
        this.fallbackResource = fallbackResource;
    }

    @Override
    public Map<String, AccountRegistrationProperty> load() {
        var properties = registeredSelectionService.find();
        if (!properties.isEmpty() || fallbackResource == null) {
            return properties;
        }
        var defaults = new DefaultAccountRegistrationPropertyLoader(fallbackResource).load();
        var categorized = new LinkedHashMap<String, AccountRegistrationProperty>();
        defaults.forEach((name, property) -> categorized.put(name, categorize(name, property)));
        return categorized;
    }

    private static AccountSignupProperty categorize(final String name, final AccountRegistrationProperty source) {
        var category = switch (name.toLowerCase(java.util.Locale.ROOT)) {
            case "email", "mail" -> "email";
            case "phone", "phonenumber", "mobile", "mobilephone", "callingcode", "calling_code" -> "phone";
            default -> "";
        };
        return AccountSignupProperty.builder()
            .order(source.getOrder()).name(source.getName()).type(source.getType()).pattern(source.getPattern())
            .required(source.isRequired()).label(source.getLabel()).cssClass(source.getCssClass())
            .title(source.getTitle()).validationMessage(source.getValidationMessage()).values(source.getValues())
            .category(category).build();
    }

    @Override
    public AccountRegistrationPropertyLoader store(final Map<String, AccountRegistrationProperty> map) {
        registeredSelectionService.save(map);
        return this;
    }
}
