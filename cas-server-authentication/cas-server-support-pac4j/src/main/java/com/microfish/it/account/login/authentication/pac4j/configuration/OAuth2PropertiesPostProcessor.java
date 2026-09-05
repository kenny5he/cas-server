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

package com.microfish.it.account.login.authentication.pac4j.configuration;

import com.microfish.it.account.login.authentication.pac4j.Pac4jAuthenticationProperties;
import com.microfish.it.account.login.authentication.pac4j.platform.gitee.Pac4jGiteeClientProperties;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copies the application-specific Gitee client into the CAS OAuth2 client list.
 *
 * <p>CAS builds delegated clients from {@link CasConfigurationProperties}, while
 * the application-facing configuration is bound to {@link Pac4jAuthenticationProperties}.
 * This processor bridges the two objects after both property beans have been bound
 * and before delegated clients are created.</p>
 */
public final class OAuth2PropertiesPostProcessor implements BeanPostProcessor, BeanFactoryAware, PriorityOrdered {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) {
        if (bean instanceof CasConfigurationProperties casProperties && beanFactory != null) {
            var applicationProperties = beanFactory
                    .getBeanProvider(Pac4jAuthenticationProperties.class)
                    .getIfAvailable();
            if (applicationProperties != null) {
                mergeGiteeClient(casProperties, applicationProperties.getGitee());
            }
        }
        return bean;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * Merges the application Gitee properties into CAS OAuth2 properties.
     * Existing OAuth2 values are retained; Gitee values fill missing values and
     * default maps. If no Gitee client exists, a configured Gitee client is added.
     *
     * @param casProperties CAS properties to update
     * @param oauth2Properties application OAuth properties
     */
    public static void mergeGiteeClient(final CasConfigurationProperties casProperties,
                                        final Pac4jOAuth20ClientProperties oauth2Properties) {
        if (casProperties == null || oauth2Properties == null) {
            return;
        }

        var oauth2Clients = casProperties.getAuthn().getPac4j().getOauth2();
        var existing = oauth2Clients.stream()
                .filter(client -> isGiteeClient(client.getClientName()))
                .findFirst()
                .orElse(null);

        if (existing == null) {
            if (StringUtils.hasText(oauth2Properties.getId())
                    && StringUtils.hasText(oauth2Properties.getSecret())) {
                oauth2Clients.add(oauth2Properties);
            }
            return;
        }

        copyIfBlank(existing, oauth2Properties);
        existing.getCustomParams().putAll(defaultEntries(existing.getCustomParams(), oauth2Properties.getCustomParams()));
        existing.getProfileAttrs().putAll(defaultEntries(existing.getProfileAttrs(), oauth2Properties.getProfileAttrs()));
    }

    private static boolean isGiteeClient(final String clientName) {
        return StringUtils.hasText(clientName) && "gitee".equalsIgnoreCase(clientName.trim());
    }

    private static void copyIfBlank(final Pac4jOAuth20ClientProperties target,
                                    final Pac4jOAuth20ClientProperties source) {
        if (!StringUtils.hasText(target.getClientName())) {
            target.setClientName(source.getClientName());
        }
        if (!StringUtils.hasText(target.getId())) {
            target.setId(source.getId());
        }
        if (!StringUtils.hasText(target.getSecret())) {
            target.setSecret(source.getSecret());
        }
        if (!StringUtils.hasText(target.getAuthUrl())) {
            target.setAuthUrl(source.getAuthUrl());
        }
        if (!StringUtils.hasText(target.getTokenUrl())) {
            target.setTokenUrl(source.getTokenUrl());
        }
        if (!StringUtils.hasText(target.getProfileUrl())) {
            target.setProfileUrl(source.getProfileUrl());
        }
        if (!StringUtils.hasText(target.getProfileVerb())) {
            target.setProfileVerb(source.getProfileVerb());
        }
        if (!StringUtils.hasText(target.getResponseType())) {
            target.setResponseType(source.getResponseType());
        }
        if (!StringUtils.hasText(target.getScope())) {
            target.setScope(source.getScope());
        }
        if (!StringUtils.hasText(target.getClientAuthenticationMethod())) {
            target.setClientAuthenticationMethod(source.getClientAuthenticationMethod());
        }
        if (!StringUtils.hasText(target.getPrincipalIdAttribute())) {
            target.setPrincipalIdAttribute(source.getPrincipalIdAttribute());
        }
    }

    private static <K, V> Map<K, V> defaultEntries(final Map<K, V> target,
                                                   final Map<K, V> defaults) {
        var missing = new LinkedHashMap<K, V>();
        defaults.forEach((key, value) -> {
            if (!target.containsKey(key)) {
                missing.put(key, value);
            }
        });
        return missing;
    }
}
