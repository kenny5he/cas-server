/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfish.it.iam.login.webflow.configuration;

import java.util.List;

import org.apereo.cas.web.CasWebSecurityConfigurer;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowIdExtractor;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Exposes the standard CAS login webflow at {@code /signin}.
 *
 * <p>The internal flow id remains {@code login} because CAS webflow extensions
 * use that id to locate and customize the login flow.</p>
 *
 * @author kenny.he
 * @since 2026/09/27
 */
@AutoConfiguration
public class CasSigninWebflowAutoConfiguration {

    public static final String SIGNIN_FLOW_PATH = "signin";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(name = "signinWebflowIdExtractor")
    public CasWebflowIdExtractor signinWebflowIdExtractor() {
        return (request, flowId) -> {
            var signinPathSuffix = '/' + SIGNIN_FLOW_PATH;
            if (SIGNIN_FLOW_PATH.equals(flowId)) {
                return CasWebflowConfigurer.FLOW_ID_LOGIN;
            }
            if (flowId.endsWith(signinPathSuffix)) {
                return flowId.substring(0, flowId.length() - SIGNIN_FLOW_PATH.length())
                        + CasWebflowConfigurer.FLOW_ID_LOGIN;
            }
            return flowId;
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "casSigninEndpointConfigurer")
    public CasWebSecurityConfigurer<Void> casSigninEndpointConfigurer() {
        return new CasWebSecurityConfigurer<>() {
            @Override
            public List<String> getIgnoredEndpoints() {
                return List.of('/' + SIGNIN_FLOW_PATH);
            }
        };
    }
}
