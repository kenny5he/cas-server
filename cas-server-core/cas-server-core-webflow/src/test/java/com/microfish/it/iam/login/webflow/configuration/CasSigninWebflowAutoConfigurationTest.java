/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfish.it.iam.login.webflow.configuration;

import java.util.List;

import junit.framework.TestCase;
import org.apereo.cas.web.flow.CasWebflowConfigurer;

public class CasSigninWebflowAutoConfigurationTest extends TestCase {

    public void testSigninPathUsesLoginWebflow() {
        var extractor = new CasSigninWebflowAutoConfiguration().signinWebflowIdExtractor();

        assertEquals(CasWebflowConfigurer.FLOW_ID_LOGIN,
                extractor.extract(null, CasSigninWebflowAutoConfiguration.SIGNIN_FLOW_PATH));
        assertEquals("tenant/login", extractor.extract(null, "tenant/signin"));
        assertEquals("logout", extractor.extract(null, "logout"));
    }

    public void testSigninEndpointIsPubliclyAccessible() {
        var endpointConfigurer = new CasSigninWebflowAutoConfiguration().casSigninEndpointConfigurer();

        assertEquals(List.of("/signin"), endpointConfigurer.getIgnoredEndpoints());
    }
}
