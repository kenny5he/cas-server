/**
 * Copyright 2023 - Ren Jian Yan Huo
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

package com.microfish.it.login.saml.configuration;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Configuration;

import org.apereo.cas.config.CasSamlIdPJpaAutoConfiguration;

/**
 * SAML 配置信息
 * @author kenny.he
 * @since 2023/02/12
 */
@Configuration
@AutoConfigureBefore({CasSamlIdPJpaAutoConfiguration.class})
public class SamlAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SamlAutoConfiguration.class);

    @PostConstruct
    public void init() {
        logger.info("Loding Saml Configuration...");
    }

}
