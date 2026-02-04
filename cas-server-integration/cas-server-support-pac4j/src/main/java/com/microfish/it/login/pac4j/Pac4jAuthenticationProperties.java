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

package com.microfish.it.login.pac4j;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import org.apereo.cas.configuration.model.support.pac4j.Pac4jDelegatedAuthenticationProperties;

import com.microfish.it.login.pac4j.platform.wechat.Pac4jWeChatClientProperties;
import com.microfish.it.login.pac4j.platform.workwechat.Pac4jWorkWeChatClientProperties;

/**
 * 客制化
 * @author kenny.he
 * @since 2022/08/07
 */
@Getter
@Setter
@Accessors(chain = true)
@ConfigurationProperties(value = "cas.authn.pac4j")
public class Pac4jAuthenticationProperties extends Pac4jDelegatedAuthenticationProperties {
    @NestedConfigurationProperty
    private Pac4jWorkWeChatClientProperties workWechat = new Pac4jWorkWeChatClientProperties();

    @NestedConfigurationProperty
    private Pac4jWeChatClientProperties wechat = new Pac4jWeChatClientProperties();


}
