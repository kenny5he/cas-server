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

import org.apereo.cas.acct.AccountRegistrationUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.configurer.AbstractCasWebflowConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

/**
 * Exposes account registration through the main CAS login flow at {@code /registration}.
 *
 * @author kenny.he
 * @since 2026/09/04
 */
public class CasSignupWebflowConfigurer extends AbstractCasWebflowConfigurer {

    public static final String REGISTRATION_FLOW_PATH = "registration";

    public static final String REGISTRATION_REQUEST_ATTRIBUTE =
            CasSignupWebflowConfigurer.class.getName() + ".request";

    private static final String STATE_ID_REGISTRATION_ENTRY = "routeToAccountRegistration";

    public CasSignupWebflowConfigurer(
            final FlowBuilderServices flowBuilderServices,
            final FlowDefinitionRegistry flowDefinitionRegistry,
            final ConfigurableApplicationContext applicationContext,
            final CasConfigurationProperties casProperties) {
        super(flowBuilderServices, flowDefinitionRegistry, applicationContext, casProperties);
    }

    @Override
    protected void doInitialize() {
        var flow = getLoginFlow();
        var originalStartState = flow.getStartState().getId();
        var registrationEntry = createDecisionState(
                flow,
                STATE_ID_REGISTRATION_ENTRY,
                "externalContext.nativeRequest.getAttribute('" + REGISTRATION_REQUEST_ATTRIBUTE
                        + "') == true && requestParameters."
                        + AccountRegistrationUtils.REQUEST_PARAMETER_ACCOUNT_REGISTRATION_ACTIVATION_TOKEN
                        + " == null",
                CasWebflowConstants.STATE_ID_VIEW_ACCOUNT_SIGNUP,
                originalStartState);
        setStartState(flow, registrationEntry);
    }
}
