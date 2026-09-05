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

package com.microfoolish.it.account.signup.configuration;

import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationService;
import org.apereo.cas.acct.AccountRegistrationUtils;
import org.apereo.cas.acct.webflow.SubmitAccountRegistrationAction;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.multitenancy.TenantExtractor;
import org.apereo.cas.notifications.CommunicationsManager;
import org.apereo.cas.ticket.TicketFactory;
import org.apereo.cas.ticket.TransientSessionTicket;
import org.apereo.cas.ticket.TransientSessionTicketFactory;
import org.apereo.cas.ticket.registry.TicketRegistry;
import org.apereo.cas.util.CollectionUtils;
import org.apache.hc.core5.net.URIBuilder;

import java.io.Serializable;

/**
 * Creates account activation links on the public registration endpoint.
 *
 * @author kenny.he
 * @since 2026/09/05
 */
public class CasSignupSubmitAccountRegistrationAction extends SubmitAccountRegistrationAction {

    private final AccountRegistrationService accountRegistrationService;

    private final CasConfigurationProperties casProperties;

    private final TicketFactory ticketFactory;

    private final TicketRegistry ticketRegistry;

    public CasSignupSubmitAccountRegistrationAction(
            final AccountRegistrationService accountRegistrationService,
            final CasConfigurationProperties casProperties,
            final CommunicationsManager communicationsManager,
            final TicketFactory ticketFactory,
            final TicketRegistry ticketRegistry,
            final TenantExtractor tenantExtractor) {
        super(accountRegistrationService, casProperties, communicationsManager,
                ticketFactory, ticketRegistry, tenantExtractor);
        this.accountRegistrationService = accountRegistrationService;
        this.casProperties = casProperties;
        this.ticketFactory = ticketFactory;
        this.ticketRegistry = ticketRegistry;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected String createAccountRegistrationActivationUrl(
            final AccountRegistrationRequest registrationRequest) throws Throwable {
        var token = accountRegistrationService.createToken(registrationRequest);
        var transientFactory = (TransientSessionTicketFactory) ticketFactory.get(TransientSessionTicket.class);
        var properties = CollectionUtils.<String, Serializable>wrap(
                AccountRegistrationUtils.PROPERTY_ACCOUNT_REGISTRATION_ACTIVATION_TOKEN, token);
        var ticket = transientFactory.create((Service) null, properties);
        ticketRegistry.addTicket(ticket);
        return new URIBuilder(casProperties.getServer().getPrefix() + "/"
                + CasSignupWebflowConfigurer.REGISTRATION_FLOW_PATH)
                .addParameter(AccountRegistrationUtils.REQUEST_PARAMETER_ACCOUNT_REGISTRATION_ACTIVATION_TOKEN,
                        ticket.getId())
                .build()
                .toURL()
                .toExternalForm();
    }
}
