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

package com.microfish.it.account.login.authentication.pac4j.platform.workwechat.client;

import com.microfish.it.account.login.authentication.pac4j.Pac4jAuthenticationProperties;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClient;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClientBuilder;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Builds the custom pac4j Enterprise WeChat client from application properties.
 */
public final class WorkWechatDelegatedClientBuilder implements ConfigurableDelegatedClientBuilder {

    private final Pac4jAuthenticationProperties properties;

    public WorkWechatDelegatedClientBuilder(final Pac4jAuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ConfigurableDelegatedClient> build(final CasConfigurationProperties casProperties) {
        var clientProperties = properties.getWorkWechat();
        if (clientProperties == null
                || !clientProperties.isEnabled()
                || !StringUtils.hasText(clientProperties.getId())
                || !StringUtils.hasText(clientProperties.getSecret())
                || !StringUtils.hasText(clientProperties.getAgentId())) {
            return List.of();
        }

        var client = new WorkWechatClient(clientProperties.getId(),
                clientProperties.getAgentId(), clientProperties.getSecret());
        if (!StringUtils.hasText(clientProperties.getClientName())) {
            clientProperties.setClientName("Work_WeChat");
        }
        if (clientProperties.getScope() != null) {
            client.addScope(clientProperties.getScope());
        }
        return List.of(new ConfigurableDelegatedClient(client, clientProperties));
    }

    @Override
    public String getName() {
        return "workWechat";
    }
}
