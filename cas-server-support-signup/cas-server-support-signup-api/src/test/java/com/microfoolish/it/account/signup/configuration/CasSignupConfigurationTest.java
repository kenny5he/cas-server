/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfoolish.it.account.signup.configuration;

import com.microfish.it.account.login.configuration.processer.ConfigurationMappingEnvironment;
import com.microfish.it.account.login.configuration.processer.ConfigurationMappingScanner;
import jakarta.servlet.http.HttpServletRequest;
import junit.framework.TestCase;
import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.web.flow.CasWebflowConfigurer;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.webflow.config.FlowBuilderServicesBuilder;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistryImpl;
import org.springframework.webflow.engine.ActionState;
import org.springframework.webflow.engine.DecisionState;
import org.springframework.webflow.engine.Flow;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CasSignupConfigurationTest extends TestCase {

    public void testCryptoPropertiesAreMappedToCasAccountRegistration() {
        var environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "account.signup.crypto.encryption.key", "encryption-key",
                "account.signup.crypto.signing.key", "signing-key")));

        var definitions = ConfigurationMappingScanner.scan(
                List.of(CasSignupCryptoProperties.class), List.of(),
                new DefaultResourceLoader(), environment);
        ConfigurationMappingEnvironment.apply(environment, definitions);

        assertEquals("encryption-key", environment.getProperty(
                "cas.account-registration.core.crypto.encryption.key"));
        assertEquals("signing-key", environment.getProperty(
                "cas.account-registration.core.crypto.signing.key"));
    }

    public void testRegistrationPathUsesLoginWebflow() {
        var requestAttributes = new HashMap<String, Object>();
        var request = (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                (proxy, method, arguments) -> {
                    if (method.getName().equals("setAttribute")) {
                        requestAttributes.put((String) arguments[0], arguments[1]);
                    }
                    return null;
                });
        var extractor = new CasSignupAutoConfiguration().registrationWebflowIdExtractor();

        assertEquals(CasWebflowConfigurer.FLOW_ID_LOGIN,
                extractor.extract(request, CasSignupWebflowConfigurer.REGISTRATION_FLOW_PATH));
        assertEquals(Boolean.TRUE,
                requestAttributes.get(CasSignupWebflowConfigurer.REGISTRATION_REQUEST_ATTRIBUTE));
        assertEquals("logout", extractor.extract(request, "logout"));
    }

    public void testRegistrationEndpointIsPubliclyAccessible() {
        var endpointConfigurer = new CasSignupAutoConfiguration().casSignupEndpointConfigurer();

        assertEquals(List.of("/registration"), endpointConfigurer.getIgnoredEndpoints());
    }

    public void testRegistrationEntryIsAddedToLoginWebflow() {
        var applicationContext = new StaticApplicationContext();
        var flowBuilderServices = new FlowBuilderServicesBuilder(applicationContext).build();
        var flowRegistry = new FlowDefinitionRegistryImpl();
        var loginFlow = new Flow(CasWebflowConfigurer.FLOW_ID_LOGIN);
        new ActionState(loginFlow, "originalStart");
        new ActionState(loginFlow, CasWebflowConstants.STATE_ID_VIEW_ACCOUNT_SIGNUP);
        loginFlow.setStartState("originalStart");
        flowRegistry.registerFlowDefinition(loginFlow);

        var configurer = new CasSignupWebflowConfigurer(
                flowBuilderServices, flowRegistry, applicationContext, new CasConfigurationProperties());
        configurer.initialize();

        assertTrue(loginFlow.getStartState() instanceof DecisionState);
        assertEquals("routeToAccountRegistration", loginFlow.getStartState().getId());
    }

    public void testActivationUrlUsesRegistrationPath() throws Throwable {
        var registrationService = proxy(AccountRegistrationService.class,
                (proxy, method, arguments) -> method.getName().equals("createToken") ? "token" : null);
        var ticket = proxy(TransientSessionTicket.class,
                (proxy, method, arguments) -> method.getName().equals("getId") ? "ticket-id" : null);
        var transientTicketFactory = proxy(TransientSessionTicketFactory.class,
                (proxy, method, arguments) -> method.getName().equals("create") ? ticket : null);
        var ticketFactory = proxy(TicketFactory.class,
                (proxy, method, arguments) -> method.getName().equals("get") ? transientTicketFactory : null);
        var ticketRegistry = proxy(TicketRegistry.class,
                (proxy, method, arguments) -> method.getName().equals("addTicket") ? ticket : null);
        var casProperties = new CasConfigurationProperties();
        casProperties.getServer().setPrefix("https://cas.example.org/cas");
        var action = new CasSignupSubmitAccountRegistrationAction(
                registrationService, casProperties, null, ticketFactory, ticketRegistry, null);

        assertEquals("https://cas.example.org/cas/registration?acctregtoken=ticket-id",
                action.createAccountRegistrationActivationUrl(new AccountRegistrationRequest()));
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(final Class<T> type, final java.lang.reflect.InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{type}, handler);
    }
}
