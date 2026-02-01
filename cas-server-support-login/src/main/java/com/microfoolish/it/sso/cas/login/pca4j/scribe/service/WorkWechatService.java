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

package com.microfoolish.it.sso.cas.login.pca4j.scribe.service;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.microfoolish.it.sso.cas.login.pca4j.scribe.extractors.WorkWechatJsonExtractor;
import com.microfoolish.it.sso.cas.login.pca4j.scribe.model.WorkWechatConfirmCode;
import org.pac4j.scribe.service.WechatService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author kenny.he
 * @since 2022/08/09
 */
public class WorkWechatService extends WechatService {
    public static final String CORP_ID = "corpid";

    public static final String CORP_SECRET = "corpsecret";

    private DefaultApi20 api;

    /**
     * Default constructor
     *
     * @param api              OAuth2.0 api information
     * @param apiKey           the API key
     * @param apiSecret        the API secret
     * @param callback         the callback URL
     * @param scope            the scope
     * @param responseType     the response type
     * @param userAgent        the user agent
     * @param httpClientConfig the HTTP client configuration
     * @param httpClient       the HTTP client
     */
    public WorkWechatService(DefaultApi20 api, String apiKey, String apiSecret, String callback, String scope, String responseType, String userAgent, HttpClientConfig httpClientConfig, HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, scope, responseType, userAgent, httpClientConfig, httpClient);
        this.api = api;
    }

    @Override
    protected OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());
        request.addParameter(CORP_ID, getApiKey());
        request.addParameter(CORP_SECRET, getApiSecret());
        return request;
    }

    @Override
    public Future<OAuth2AccessToken> getAccessTokenAsync(String code) {
        WorkWechatConfirmCode confirmCode = new WorkWechatConfirmCode(code);
        return getAccessToken(AccessTokenRequestParams.create(code), confirmCode,null);
    }

    public Future<OAuth2AccessToken> getAccessTokenAsync(AccessTokenRequestParams params, WorkWechatConfirmCode confirmCode) {
        return getAccessToken(params,confirmCode, null);
    }

    @Override
    public OAuth2AccessToken getAccessToken(String code) throws IOException, InterruptedException, ExecutionException {
        WorkWechatConfirmCode confirmCode = new WorkWechatConfirmCode(code);
        return getAccessToken(AccessTokenRequestParams.create(code),confirmCode);
    }

    public OAuth2AccessToken getAccessToken(AccessTokenRequestParams params, WorkWechatConfirmCode confirmCode)
            throws IOException, InterruptedException, ExecutionException {
        return sendAccessTokenRequestSync(createAccessTokenRequest(params),confirmCode);
    }

    /**
     * Start the request to retrieve the access token. The optionally provided callback will be called with the Token
     * when it is available.
     *
     * @param params params
     * @param callback optional callback
     * @return Future
     */
    public Future<OAuth2AccessToken> getAccessToken(AccessTokenRequestParams params,
                                                    WorkWechatConfirmCode confirmCode,
                                                    OAuthAsyncRequestCallback<OAuth2AccessToken> callback) {
        return sendAccessTokenRequestAsync(createAccessTokenRequest(params),confirmCode, callback);
    }

    public Future<OAuth2AccessToken> getAccessToken(String code,
                                                    OAuthAsyncRequestCallback<OAuth2AccessToken> callback) {
        WorkWechatConfirmCode confirmCode = new WorkWechatConfirmCode(code);
        return getAccessToken(AccessTokenRequestParams.create(code),confirmCode ,callback);
    }

    protected OAuth2AccessToken sendAccessTokenRequestSync(OAuthRequest request, WorkWechatConfirmCode confirmCode)
            throws IOException, InterruptedException, ExecutionException {
        if (isDebug()) {
            log("send request for access token synchronously to %s", request.getCompleteUrl());
        }
        try (Response response = execute(request)) {
            if (isDebug()) {
                log("response status code: %s", response.getCode());
                log("response body: %s", response.getBody());
            }

            if (api.getAccessTokenExtractor() instanceof WorkWechatJsonExtractor) {
               return  ((WorkWechatJsonExtractor)api.getAccessTokenExtractor()).extract(confirmCode,response);
            } else {
                throw new OAuthException("");
            }
        }
    }

    protected Future<OAuth2AccessToken> sendAccessTokenRequestAsync(OAuthRequest request, WorkWechatConfirmCode confirmCode) {
        return sendAccessTokenRequestAsync(request,confirmCode, null);
    }

    protected Future<OAuth2AccessToken> sendAccessTokenRequestAsync(OAuthRequest request,
                                                                    WorkWechatConfirmCode confirmCode,
                                                                    OAuthAsyncRequestCallback<OAuth2AccessToken> callback) {
        if (isDebug()) {
            log("send request for access token asynchronously to %s", request.getCompleteUrl());
        }

        return execute(request, callback, new OAuthRequest.ResponseConverter<OAuth2AccessToken>() {
            @Override
            public OAuth2AccessToken convert(Response response) throws IOException {
                log("received response for access token");
                if (isDebug()) {
                    log("response status code: %s", response.getCode());
                    log("response body: %s", response.getBody());
                }
                OAuth2AccessToken token;
                if (api.getAccessTokenExtractor() instanceof WorkWechatJsonExtractor) {
                    token =  ((WorkWechatJsonExtractor)api.getAccessTokenExtractor()).extract(confirmCode,response);
                } else {
                    throw new OAuthException("");
                }
                response.close();
                return token;
            }
        });
    }
}
