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
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;

import java.util.Map;

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

    @Override
    public String getClientName() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "gitee");
    }

    @Override
    public String getAuthUrl() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "https://gitee.com/oauth/authorize");
    }

    @Override
    public String getTokenUrl() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "https://gitee.com/oauth/token");
    }

    @Override
    public String getProfileUrl() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "https://gitee.com/api/v5/user");
    }

    @Override
    public String getProfileVerb() {
        return StringUtils.getIfBlank(super.getClientName(), ()-> "GET");
    }

    @Override
    public Map<String, String> getProfileAttrs() {
        return super.getProfileAttrs();
    }

    @Override
    public Map<String, String> getCustomParams() {
        return super.getCustomParams();
    }
}
