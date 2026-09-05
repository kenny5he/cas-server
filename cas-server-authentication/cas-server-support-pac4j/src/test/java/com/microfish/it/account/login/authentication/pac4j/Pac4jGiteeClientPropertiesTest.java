/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfish.it.account.login.authentication.pac4j;

import com.microfish.it.account.login.authentication.pac4j.configuration.OAuth2PropertiesPostProcessor;
import com.microfish.it.account.login.authentication.pac4j.platform.gitee.Pac4jGiteeClientProperties;
import junit.framework.TestCase;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.pac4j.oauth.Pac4jOAuth20ClientProperties;

/**
 * Tests for application-specific Gitee OAuth2 defaults and CAS property merging.
 */
public class Pac4jGiteeClientPropertiesTest extends TestCase {

    public void testGiteeDefaults() {
        var properties = new Pac4jGiteeClientProperties();

        assertEquals("gitee", properties.getClientName());
        assertEquals("https://gitee.com/oauth/authorize", properties.getAuthUrl());
        assertEquals("https://gitee.com/oauth/token", properties.getTokenUrl());
        assertEquals("https://gitee.com/api/v5/user", properties.getProfileUrl());
        assertEquals("GET", properties.getProfileVerb());
        properties.setPrincipalAttributeId("name");
        assertEquals("name", properties.getPrincipalIdAttribute());
        assertEquals("user_info", properties.getCustomParams().get("scope"));
        assertEquals("updated_at", properties.getProfileAttrs().get("updated_at"));
        assertEquals("created_at", properties.getProfileAttrs().get("created_at"));
        assertEquals("name", properties.getProfileAttrs().get("name"));
        assertEquals("email", properties.getProfileAttrs().get("email"));
        assertEquals("login", properties.getProfileAttrs().get("login"));
        assertEquals("type", properties.getProfileAttrs().get("type"));
        assertEquals("avatar_url", properties.getProfileAttrs().get("avatar_url"));
    }

    public void testConfiguredGiteeClientIsAddedToCasOAuth2Properties() {
        var casProperties = new CasConfigurationProperties();
        var giteeProperties = new Pac4jGiteeClientProperties();
        giteeProperties.setId("gitee-id");
        giteeProperties.setSecret("gitee-secret");

        OAuth2PropertiesPostProcessor.mergeGiteeClient(casProperties, giteeProperties);

        assertEquals(1, casProperties.getAuthn().getPac4j().getOauth2().size());
        var client = casProperties.getAuthn().getPac4j().getOauth2().get(0);
        assertSame(giteeProperties, client);
        assertEquals("user_info", client.getCustomParams().get("scope"));
        assertEquals("avatar_url", client.getProfileAttrs().get("avatar_url"));
    }

    public void testExplicitCasGiteeValuesAreRetainedAndDefaultsAreFilled() {
        var casProperties = new CasConfigurationProperties();
        var casGitee = new Pac4jOAuth20ClientProperties();
        casGitee.setClientName("Gitee");
        casGitee.setAuthUrl("https://example.test/authorize");
        casGitee.getCustomParams().put("scope", "custom_scope");
        casProperties.getAuthn().getPac4j().getOauth2().add(casGitee);

        var giteeProperties = new Pac4jGiteeClientProperties();
        giteeProperties.setId("gitee-id");
        giteeProperties.setSecret("gitee-secret");

        OAuth2PropertiesPostProcessor.mergeGiteeClient(casProperties, giteeProperties);

        assertEquals(1, casProperties.getAuthn().getPac4j().getOauth2().size());
        assertEquals("https://example.test/authorize", casGitee.getAuthUrl());
        assertEquals("custom_scope", casGitee.getCustomParams().get("scope"));
        assertEquals("gitee-id", casGitee.getId());
        assertEquals("gitee-secret", casGitee.getSecret());
        assertEquals("avatar_url", casGitee.getProfileAttrs().get("avatar_url"));
    }
}
