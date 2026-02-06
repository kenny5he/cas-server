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

package com.microfish.it.login.pac4j.platform.workwechat.scribe.api;

import java.io.OutputStream;
import java.util.Map;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.ParameterList;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

import com.microfish.it.login.pac4j.platform.workwechat.client.WorkWechatClient;
import com.microfish.it.login.pac4j.platform.workwechat.scribe.extractors.WorkWechatJsonExtractor;
import com.microfish.it.login.pac4j.platform.workwechat.scribe.service.WorkWechatService;

/**
 * 企业微信Api
 *
 * @author kenny.he
 * @since 2022/08/08
 */
public class WorkWechatApi extends DefaultApi20 {

    public static final String APPID = "appid";

    public static final String SECRET = "secret";

    public static final String AGENT_ID  = "agentid";

    public static final String AUTHORIZE_ENDPOINT_URL_1 = "https://open.work.weixin.qq.com/wwopen/sso/qrConnect";

    public static final String AUTHORIZE_ENDPOINT_URL_2 = "https://open.weixin.qq.com/connect/oauth2/authorize";

    public static final String TOKEN_ENDPOINT_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    private String agentid;

    public WorkWechatApi(String agentid) {
        this.agentid = agentid;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return TOKEN_ENDPOINT_URL;
    }

    @Override
    public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
                                      Map<String, String> additionalParams) {
        final ParameterList parameters = new ParameterList(additionalParams);
        parameters.add(OAuthConstants.RESPONSE_TYPE, responseType);
        parameters.add(APPID, apiKey);
        parameters.add(AGENT_ID, agentid);
        if (callback != null) {
            parameters.add(OAuthConstants.REDIRECT_URI, callback);
        }

        if (scope != null) {
            parameters.add(OAuthConstants.SCOPE, scope);
        }

        if (state != null) {
            parameters.add(OAuthConstants.STATE, state);
        }
        return parameters.appendTo(getAuthorizationBaseUrl(scope));
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return getAuthorizationBaseUrl(null);
    }

    /**
     * 授权路径
     * @param scope
     * @return
     */
    protected String getAuthorizationBaseUrl(String scope) {
        if (scope != null && scope.contains(
                WorkWechatClient.WorkWechatScope.SNSAPI_LOGIN.toString().toLowerCase())) {
            return AUTHORIZE_ENDPOINT_URL_1;
        } else {
            return AUTHORIZE_ENDPOINT_URL_2;
        }
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.GET;
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return WorkWechatJsonExtractor.instance();
    }

    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope,
                                        String responseType, OutputStream debugStream, String userAgent,
                                        HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new WorkWechatService(this, apiKey, apiSecret, callback, defaultScope, responseType, userAgent, httpClientConfig, httpClient);
    }

    @Override
    public BearerSignature getBearerSignature() {
        return BearerSignatureURIQueryParameter.instance();
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }
}
