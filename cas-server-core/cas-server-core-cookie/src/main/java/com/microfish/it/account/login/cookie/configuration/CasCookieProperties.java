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

package com.microfish.it.account.login.cookie.configuration;

import com.microfish.it.account.login.configuration.annotation.ConfigurationPropertiesMapping;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @see org.apereo.cas.configuration.model.support.cookie.TicketGrantingCookieProperties
 *
 * @author kenny.he
 * @since 2026/09/04
 */
@Getter
@Setter
@ConfigurationPropertiesMapping(casPrefix = "cas.tgc", prefix = "account.cookie")
public class CasCookieProperties {

    private Boolean secure = true;

    private String name = "W3-TGC";
}
