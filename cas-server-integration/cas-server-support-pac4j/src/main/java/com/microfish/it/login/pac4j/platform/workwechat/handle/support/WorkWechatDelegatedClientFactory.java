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

package com.microfish.it.login.pac4j.platform.workwechat.handle.support;

import com.microfoolish.it.sso.cas.login.pac4j.Pac4jAuthenticationProperties;
import com.microfoolish.it.sso.cas.login.pac4j.client.WorkWechatClient;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.pac4j.authentication.DefaultDelegatedClientFactory;
import org.apereo.cas.support.pac4j.authentication.DelegatedClientFactoryCustomizer;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.oauth.client.WechatClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author kenny.he
 * @since 2022/08/07
 */

@Slf4j
public class WorkWechatDelegatedClientFactory extends DefaultDelegatedClientFactory implements DisposableBean {

    private Pac4jAuthenticationProperties pca4j;

    private Set<IndirectClient> customClients = new LinkedHashSet<>();

    public WorkWechatDelegatedClientFactory(CasConfigurationProperties casProperties, Collection<DelegatedClientFactoryCustomizer> customizers, CasSSLContext casSSLContext, ApplicationContext applicationContext) {
        super(casProperties, customizers, casSSLContext, applicationContext);
    }

    public WorkWechatDelegatedClientFactory(CasConfigurationProperties casProperties, Pac4jAuthenticationProperties pca4j, Collection<DelegatedClientFactoryCustomizer> customizers, CasSSLContext casSSLContext, ApplicationContext applicationContext) {
        super(casProperties, customizers, casSSLContext, applicationContext);
        this.pca4j = pca4j;
    }
    @Override
    @Synchronized
    public Collection<IndirectClient> build() {
        super.build();
        Set<IndirectClient> clients = super.getClients();
        if (this.customClients.isEmpty() || !pca4j.getCore().isLazyInit()) {
            this.configureWeChatClient(customClients);
            this.configureWorkWeChatClient(customClients);
            clients.addAll(customClients);
        }
        return clients;
    }


    /**
     * Configure WeChat client.
     *
     * @param properties the properties
     */
    protected void configureWeChatClient(final Collection<IndirectClient> properties) {
        val wechat = pca4j.getWechat();
        if (wechat.isEnabled() && StringUtils.isNotBlank(wechat.getId()) && StringUtils.isNotBlank(wechat.getSecret())) {
            val client = new WechatClient(wechat.getId(), wechat.getSecret());
            configureClient(client, wechat);
            client.addScope(wechat.getScope());
            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            properties.add(client);
        }
    }

    /**
     * Configure Work WeChat client.
     *
     * @param properties the properties
     */
    protected void configureWorkWeChatClient(final Collection<IndirectClient> properties) {
        val workWechat = pca4j.getWorkWechat();
        if (workWechat.isEnabled() && StringUtils.isNotBlank(workWechat.getId()) && StringUtils.isNotBlank(workWechat.getSecret())) {
            val client = new WorkWechatClient(workWechat.getId(),workWechat.getAgentId(), workWechat.getSecret());
            configureClient(client, workWechat);
            client.addScope(workWechat.getScope());
            LOGGER.debug("Created client [{}] with identifier [{}]", client.getName(), client.getKey());
            properties.add(client);
        }
    }
}
