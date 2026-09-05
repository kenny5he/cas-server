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

package com.microfish.it.account.login.cookie.configuration;

import com.microfish.it.account.login.configuration.annotation.EnableConfigurationMapping;
import org.apereo.cas.configuration.features.CasFeatureModule;
import org.apereo.cas.util.spring.boot.ConditionalOnFeatureEnabled;
import org.springframework.context.annotation.Configuration;

/**
 * 客制化 TGC 配置
 *
 * @author kenny.he
 * @since 2022/05/01
 */
@Configuration
@ConditionalOnFeatureEnabled(feature = CasFeatureModule.FeatureCatalog.AccountRegistration)
@EnableConfigurationMapping(classes = {CasCookieProperties.class, CasTicketGrantingCookieCryptoProperties.class})
public class CasTicketGrantingCookieAutoConfiguration {

}
