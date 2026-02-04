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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;
import com.microfoolish.it.sso.cas.login.pac4j.client.WorkWechatClient;
import com.microfoolish.it.sso.cas.login.pac4j.scribe.model.WorkWechatTicketToken;
import com.microfoolish.it.sso.cas.login.pac4j.scribe.model.WorkWechatToken;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.creator.OAuth20ProfileCreator;
import org.pac4j.oauth.profile.wechat.WechatProfile;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


/**
 * 企业微信 User Profile 信息
 *
 * @author kenny.he
 * @since 2022/08/10
 */
public class WorkWechatProfileCreator extends OAuth20ProfileCreator {

    private static final String CODE = "code";

    private OAuth20Configuration configuration;

    public WorkWechatProfileCreator(OAuth20Configuration configuration, IndirectClient client) {
        super(configuration, client);
        this.configuration = configuration;
    }

    @Override
    protected void signRequest(final OAuthService service, final Token token, final OAuthRequest request) {
        ((OAuth20Service) service).signRequest((OAuth2AccessToken) token, request);
        if (token instanceof OAuth2AccessToken) {
            final var accessToken = ((OAuth2AccessToken) token).getAccessToken();
            final var code = ((WorkWechatToken)token).getCode();
            request.addParameter(OAuthConfiguration.OAUTH_TOKEN, accessToken);
            request.addParameter(CODE, code);
        } else if (token instanceof WorkWechatTicketToken) {
            final var accessToken = ((WorkWechatTicketToken) token).getTicket();
            request.addParameter(OAuthConstants.ACCESS_TOKEN,accessToken);
        }
    }

    public WorkWechatTicketToken retrieveUserTicketFromToken(final WebContext context, final Token accessToken) {
        final var profileDefinition = configuration.getProfileDefinition();
        final var profileUrl = profileDefinition.getProfileUrl(accessToken, configuration);
        final var service = this.configuration.buildService(context, client);
        final var body = sendRequestForData(service, accessToken, profileUrl, profileDefinition.getProfileVerb());
        return extractUserTicket(body);
    }

    public WorkWechatTicketToken extractUserTicket(String rawResponse) {
        try {
            final JsonNode response = mapper.readTree(rawResponse);
            final JsonNode ticketNode = response.get("user_ticket");
            final JsonNode userIdNode = response.get("UserId");
            final JsonNode deviceIdNode = response.get("DeviceId");
            if (ticketNode == null &&
                WorkWechatClient.WorkWechatScope.SNSAPI_LOGIN.toString().equalsIgnoreCase((configuration.getScope()))) {
                    return new WorkWechatTicketToken(null,userIdNode.asText(),null);
            }
            return new WorkWechatTicketToken(ticketNode.asText(),
                                                userIdNode.asText(),
                                                deviceIdNode.asText());
        } catch (Exception e) {
            throw new OAuthException("User Ticket Error: ", e);
        }
    }

    @Override
    protected Optional<UserProfile> retrieveUserProfileFromToken(final WebContext context, final Token accessToken) {
        final var profileDefinition = configuration.getProfileDefinition();
        WorkWechatTicketToken ticketToken = retrieveUserTicketFromToken(context,accessToken);
        if (ticketToken.getTicket() == null &&
            WorkWechatClient.WorkWechatScope.SNSAPI_LOGIN.toString().equalsIgnoreCase((configuration.getScope()))) {
            final var profile = new WechatProfile();
            profile.setAccessToken(((OAuth2AccessToken) accessToken).getAccessToken());
            profile.setId(ticketToken.getUserId());
            return Optional.of(profile);
        }
        final var profileUrl = profileDefinition.getProfileUrl(ticketToken, configuration);
        final var service = this.configuration.buildService(context, client);
        final var body = sendRequestForData(service, ticketToken, profileUrl, profileDefinition.getProfileVerb());

        logger.info("UserProfile: " + body);
        if (body == null) {
            throw new HttpCommunicationException("No data found for accessToken: " + ticketToken);
        }
        final UserProfile profile = configuration.getProfileDefinition().extractUserProfile(body);
        addTokenToProfile(profile, ticketToken);
        return Optional.of(profile);
    }

    protected String sendRequestForData(final OAuthService service, final Token accessToken, final String dataUrl, Verb verb) {
        logger.debug("accessToken: {} / dataUrl: {}", accessToken, dataUrl);
        final var t0 = System.currentTimeMillis();
        final var request = createOAuthRequest(dataUrl, verb);
        signRequest(service, accessToken, request);
        final String body;
        final int code;
        try {
            var response = service.execute(request);
            code = response.getCode();
            body = response.getBody();
        } catch (final IOException | InterruptedException | ExecutionException e) {
            throw new HttpCommunicationException("Error getting body: " + e.getMessage());
        }
        final var t1 = System.currentTimeMillis();
        logger.debug("Request took: " + (t1 - t0) + " ms for: " + dataUrl);
        logger.debug("response code: {} / response body: {}", code, body);
        if (code != 200) {
            throw new HttpCommunicationException(code, body);
        }
        return body;
    }

    @Override
    protected void addTokenToProfile(UserProfile profile, final Token token) {
        if (profile == null) {
            profile = new WechatProfile();
        }
        final var oauth2Token = (WorkWechatTicketToken) token;
        final var accessToken = oauth2Token.getTicket();
        logger.debug("add access_token: {} to profile", accessToken);
        ((OAuth20Profile) profile).setAccessToken(accessToken);
        ((OAuth20Profile) profile).setId(oauth2Token.getUserId());
    }
}
