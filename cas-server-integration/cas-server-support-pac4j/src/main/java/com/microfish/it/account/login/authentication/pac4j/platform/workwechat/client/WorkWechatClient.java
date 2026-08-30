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

package com.microfish.it.login.pac4j.platform.workwechat.client;

import java.util.ArrayList;
import java.util.List;

import org.pac4j.oauth.client.OAuth20Client;

import com.microfish.it.login.pac4j.platform.workwechat.scribe.api.WorkWechatApi;
import com.microfish.it.login.pac4j.platform.workwechat.profile.WorkWechatProfileCreator;
import com.microfish.it.login.pac4j.platform.workwechat.profile.WorkWechatProfileDefinition;

/**
 * 企业微信
 *
 * @author kenny.he
 * @since 2022/08/08
 */
public class WorkWechatClient extends OAuth20Client {

    protected String agentId;

    protected List<WorkWechatScope> scopes;

    public enum WorkWechatScope {
        /**
         * Only for WeChat QRCode login. Get the nickname, avatar, and gender of the logged in user.
         */
        SNSAPI_LOGIN,
        /**
         * Exchange code for access_token, refresh_token, and authorized scope
         */
        SNSAPI_BASE,
        /**
         * Get user personal information
         */
        SNSAPI_PRICATEINFO
    }

    public enum WorkWechatType {
        /**
         * 第三方应用开发
         */
        THIRD_PARTY,

        /**
         * 企业内部开发
         */
        INTRANET

    }

    public WorkWechatClient(final String key,final String agentId, final String secret) {
        setKey(key);
        setSecret(secret);
        this.agentId = agentId;
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        this.configuration.setApi(new WorkWechatApi(agentId));
        this.configuration.setScope(this.getOAuthScope());
        this.configuration.setProfileDefinition(new WorkWechatProfileDefinition());
        this.configuration.setWithState(true);
        this.setProfileCreatorIfUndefined(new WorkWechatProfileCreator(this.configuration, this));
        super.internalInit(forceReinit);
    }

    public String getAgentId() {
        return agentId;
    }

    protected String getOAuthScope() {
        StringBuilder builder = null;
        if (scopes == null || scopes.isEmpty()) {
            scopes = new ArrayList<>();
            scopes.add(WorkWechatScope.SNSAPI_BASE);
        }
        if (scopes != null) {
            for (var value : scopes) {
                if (builder == null) {
                    builder = new StringBuilder();
                } else {
                    builder.append(",");
                }
                builder.append(value.toString().toLowerCase());
            }
        }
        return builder == null ? null : builder.toString();
    }

    public List<WorkWechatScope> getScopes() {
        return scopes;
    }

    public void setScopes(List<WorkWechatScope> scopes) {
        this.scopes = scopes;
    }

    public void addScope(WorkWechatScope scopes) {
        if (this.scopes == null)
            this.scopes = new ArrayList<>();
        this.scopes.add(scopes);
    }
}
