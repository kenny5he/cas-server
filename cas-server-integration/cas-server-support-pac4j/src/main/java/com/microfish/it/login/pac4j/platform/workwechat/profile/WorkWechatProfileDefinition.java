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

package com.microfish.it.login.pac4j.platform.workwechat.profile;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import com.microfoolish.it.sso.cas.login.pac4j.scribe.model.WorkWechatTicketToken;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.wechat.WechatProfileDefinition;

/**
 * 企业微信 User Profile Definition
 *
 * @author kenny.he
 * @since 2022/08/08
 */
public class WorkWechatProfileDefinition extends WechatProfileDefinition {

    @Override
    public String getProfileUrl(final Token token, final OAuthConfiguration configuration) {
        if (token instanceof OAuth2AccessToken) {
            return "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
        } else if (token instanceof WorkWechatTicketToken) {
            return "https://qyapi.weixin.qq.com/cgi-bin/user/getuserdetail";
        } else {
            throw new OAuthException("");
        }
    }
}
