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

package com.microfish.it.login.pac4j.platform.workwechat.scribe.extractors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.utils.Preconditions;
import com.microfish.it.login.pac4j.platform.workwechat.scribe.model.WorkWechatConfirmCode;
import com.microfish.it.login.pac4j.platform.workwechat.scribe.model.WorkWechatToken;

import java.io.IOException;

/**
 * 企业微信
 *
 * @author kenny.he
 * @since 2022/08/09
 */
public class WorkWechatJsonExtractor extends OAuth2AccessTokenJsonExtractor {

    protected WorkWechatJsonExtractor() {
    }

    private static class InstanceHolder {

        private static final WorkWechatJsonExtractor INSTANCE = new WorkWechatJsonExtractor();
    }

    public static WorkWechatJsonExtractor instance() {
        return WorkWechatJsonExtractor.InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope,
                                            JsonNode response, String rawResponse) {
        var token = new OAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope, rawResponse);
        return token;
    }

    public OAuth2AccessToken extract(WorkWechatConfirmCode confirmCode, Response response) throws IOException {
        final String body = response.getBody();
        Preconditions.checkEmptyString(body, "Response body is incorrect. Can't extract a token from an empty string");
        if (response.getCode() != 200) {
            generateError(response);
        }
        return createToken(confirmCode,body);
    }

    private OAuth2AccessToken createToken(WorkWechatConfirmCode confirmCode, String rawResponse) throws IOException {
        final JsonNode response = OBJECT_MAPPER.readTree(rawResponse);
        final JsonNode expiresInNode = response.get("expires_in");
        return createToken(extractRequiredParameter(response, OAuthConstants.ACCESS_TOKEN, rawResponse).asText(),
                  expiresInNode == null ? null : expiresInNode.asInt(),
                confirmCode.getCode() , response,
                rawResponse);
    }

    protected OAuth2AccessToken createToken(String accessToken, Integer expiresIn, String code,
                                            JsonNode response, String rawResponse) {
        var token = new WorkWechatToken(accessToken, code, expiresIn ,rawResponse);
        return token;
    }
}
