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

package com.microfoolish.it.account.signup.services;

import com.microfoolish.it.account.signup.AccountSignupProperty;
import org.apereo.cas.acct.AccountRegistrationProperty;

import java.util.List;
import java.util.Map;

/**
 * Stores and loads registration-property definitions.
 *
 * @author kenny
 * @since 7.3.0
 */
public interface RegistrationPropertyService {

    /**
     * Save a batch of registration properties.
     *
     * @param map properties by name
     */
    void save(Map<String, AccountRegistrationProperty> map);

    /**
     * Find all registration properties.
     *
     * @return registration properties
     */
    Map<String, AccountRegistrationProperty> find();
}
