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

package com.microfish.it.account.login.authentication.pac4j.platform.gitee;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;

/**
 * gitee 登录集成: 参考博客: https://blog.csdn.net/Coder_Knight/article/details/120740279
 *
 * @author kenny.he
 * @since 2026/09/05
 */
@Getter
@Setter
@Accessors(chain = true)
public class Pac4jGiteeClientProperties extends Pac4jOAuth20ClientProperties {

    /**
     * Backward-compatible spelling used by the existing application YAML.
     * CAS 7.x calls the inherited property {@code principalIdAttribute}.
     */
    private String principalAttributeId;

    public Pac4jGiteeClientProperties() {
        setProfileVerb("GET");
        setDefaultCustomParams();
        setDefaultProfileAttrs();
    }

    @Override
    public String getClientName() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "gitee");
    }

    @Override
    public String getAuthUrl() {
        return StringUtils.getIfBlank(super.getAuthUrl(), () -> "https://gitee.com/oauth/authorize");
    }

    @Override
    public String getTokenUrl() {
        return StringUtils.getIfBlank(super.getTokenUrl(), () -> "https://gitee.com/oauth/token");
    }

    @Override
    public String getProfileUrl() {
        return StringUtils.getIfBlank(super.getProfileUrl(), () -> "https://gitee.com/api/v5/user");
    }

    @Override
    public String getProfileVerb() {
        return StringUtils.getIfBlank(super.getProfileVerb(), () -> "GET");
    }

    @Override
    public String getPrincipalIdAttribute() {
        return StringUtils.getIfBlank(super.getPrincipalIdAttribute(), () -> principalAttributeId);
    }

    @Override
    public Map<String, String> getProfileAttrs() {
        var profileAttrs = super.getProfileAttrs();
        profileAttrs.putIfAbsent("updated_at", "updated_at");
        profileAttrs.putIfAbsent("created_at", "created_at");
        profileAttrs.putIfAbsent("name", "name");
        profileAttrs.putIfAbsent("email", "email");
        profileAttrs.putIfAbsent("login", "login");
        profileAttrs.putIfAbsent("type", "type");
        profileAttrs.putIfAbsent("avatar_url", "avatar_url");
        return profileAttrs;
    }

    @Override
    public Map<String, String> getCustomParams() {
        var customParams = super.getCustomParams();
        customParams.putIfAbsent("scope", "user_info");
        return customParams;
    }

    private void setDefaultCustomParams() {
        getCustomParams();
    }

    private void setDefaultProfileAttrs() {
        getProfileAttrs();
    }
}
