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

package com.microfish.it.account.login.server.nativex;

import org.apereo.cas.util.nativex.CasRuntimeHintsRegistrar;
import org.springframework.aot.hint.RuntimeHints;

/**
 *
 * @see org.apereo.cas.nativex.CasWebAppResourcesRuntimeHints
 *
 * @author kenny.he
 * @since 2026/08/30
 */
public class CasWebAppResourcesRuntimeHints implements CasRuntimeHintsRegistrar {
    @Override
    public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
        hints.resources()
                .registerPattern("application.properties")
                .registerPattern("application.yml")
                .registerPattern("spring.properties")
                .registerPattern("services/*.json");
    }
}
