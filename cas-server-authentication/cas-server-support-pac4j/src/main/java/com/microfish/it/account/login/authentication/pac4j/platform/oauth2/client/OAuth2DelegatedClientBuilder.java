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

package com.microfish.it.account.login.authentication.pac4j.platform.oauth2.client;

import com.github.scribejava.core.model.Verb;
import com.microfish.it.account.login.authentication.pac4j.Pac4jAuthenticationProperties;
import com.microfish.it.account.login.authentication.pac4j.configuration.OAuth2PropertiesPostProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClient;
import org.apereo.cas.support.pac4j.authentication.clients.ConfigurableDelegatedClientBuilder;
import org.pac4j.oauth.client.GenericOAuth20Client;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Creates the generic pac4j OAuth2 client used by Gitee.
 *
 * <p>In CAS 7.3, OAuth2 entries in {@code CasConfigurationProperties} are
 * model data only. They are converted to pac4j clients by a
 * {@link ConfigurableDelegatedClientBuilder}; without this builder the
 * configured Gitee values can be bound successfully but never appear in the
 * delegated provider list.</p>
 */
public final class OAuth2DelegatedClientBuilder implements ConfigurableDelegatedClientBuilder {

    private final Pac4jAuthenticationProperties properties;

    public OAuth2DelegatedClientBuilder(final Pac4jAuthenticationProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ConfigurableDelegatedClient> build(final CasConfigurationProperties casProperties) {
        var oauth2Properties = resolveOAuth2Properties(casProperties);
        if (CollectionUtils.isEmpty(oauth2Properties)) {
            return List.of();
        }
        List<ConfigurableDelegatedClient> delegatedClients = new ArrayList<>();
        oauth2Properties.forEach(clientProperties -> {
            if (isConfigured(clientProperties)) {
                var client = new GenericOAuth20Client();
                client.setName(clientProperties.getClientName());
                client.setKey(clientProperties.getId());
                client.setSecret(clientProperties.getSecret());
                client.setAuthUrl(clientProperties.getAuthUrl());
                client.setTokenUrl(clientProperties.getTokenUrl());
                client.setProfileUrl(clientProperties.getProfileUrl());
                client.setProfileId(clientProperties.getPrincipalIdAttribute());
                client.setProfileVerb(Verb.valueOf(clientProperties.getProfileVerb().toUpperCase(Locale.ROOT)));
                client.setProfileAttrs(clientProperties.getProfileAttrs());
                client.setCustomParams(clientProperties.getCustomParams());
                client.setScope(clientProperties.getScope());
                client.setWithState(clientProperties.isWithState());
                client.setClientAuthenticationMethod(clientProperties.getClientAuthenticationMethod());
                client.getConfiguration().setResponseType(clientProperties.getResponseType());
                delegatedClients.add(new ConfigurableDelegatedClient(client, clientProperties));
            }
        });
        return delegatedClients;
    }

    /**
     * Resolve the single OAuth2 model consumed by CAS. The application binds
     * custom entries below {@code account.authn.pac4j}, while CAS binds native
     * entries below {@code cas.authn.pac4j}. Gitee is a named application
     * property and therefore needs to be merged explicitly as well.
     */
    private List<Pac4jOAuth20ClientProperties> resolveOAuth2Properties(
            final CasConfigurationProperties casProperties) {
        var oauth2Properties = casProperties.getAuthn().getPac4j().getOauth2();

        properties.getOauth2().forEach(applicationClient -> {
            if (oauth2Properties.stream().noneMatch(existing -> sameClient(existing, applicationClient))) {
                oauth2Properties.add(applicationClient);
            }
        });

        OAuth2PropertiesPostProcessor.mergeGiteeClient(casProperties, properties.getGitee());
        return oauth2Properties;
    }

    private static boolean sameClient(final Pac4jOAuth20ClientProperties left,
                                      final Pac4jOAuth20ClientProperties right) {
        if (left == null || right == null) {
            return left == right;
        }
        if (StringUtils.hasText(left.getClientName()) && StringUtils.hasText(right.getClientName())) {
            return left.getClientName().equalsIgnoreCase(right.getClientName());
        }
        return StringUtils.hasText(left.getId()) && left.getId().equals(right.getId());
    }

    private static boolean isConfigured(final Pac4jOAuth20ClientProperties clientProperties) {
        return clientProperties != null
                && clientProperties.isEnabled()
                && StringUtils.hasText(clientProperties.getId())
                && StringUtils.hasText(clientProperties.getSecret())
                && StringUtils.hasText(clientProperties.getAuthUrl())
                && StringUtils.hasText(clientProperties.getTokenUrl())
                && StringUtils.hasText(clientProperties.getProfileUrl());
    }

    @Override
    public String getName() {
        return "OAuth2";
    }
}
