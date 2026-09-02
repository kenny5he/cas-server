/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.login.authentication.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.handler.TenantAuthenticationHandlerBuilder;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.BaseJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.BindJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.ProcedureJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.SearchJdbcAuthenticationProperties;
import org.apereo.cas.configuration.support.ConfigurationPropertiesBindingContext;
import org.apereo.cas.multitenancy.TenantDefinition;

import com.microfish.it.account.login.authentication.utils.JpaAuthenticationUtils;

/**
 * This is {@link TenantJpaAuthenticationHandlerBuilder}.
 *
 * @author Misagh Moayyed
 * @since 7.3.0
 */
@Slf4j
@RequiredArgsConstructor
public class TenantJpaAuthenticationHandlerBuilder implements TenantAuthenticationHandlerBuilder {
    private final PasswordPolicyContext passwordPolicyConfiguration;

    private final PrincipalFactory jdbcPrincipalFactory;

    private final ConfigurableApplicationContext applicationContext;

    @Override
    public List<AuthenticationHandler> buildInternal(final TenantDefinition tenantDefinition,
                                                     final ConfigurationPropertiesBindingContext<CasConfigurationProperties> bindingContext) {
        val handlers = new ArrayList<AuthenticationHandler>();
        val jdbc = bindingContext.value().getAuthn().getJdbc();
        if (bindingContext.containsBindingFor(BindJdbcAuthenticationProperties.class)) {
            createHandler(jdbc.getBind(), handlers);
        }
        if (bindingContext.containsBindingFor(QueryJdbcAuthenticationProperties.class)) {
            createHandler(jdbc.getQuery(), handlers);
        }
        if (bindingContext.containsBindingFor(QueryEncodeJdbcAuthenticationProperties.class)) {
            createHandler(jdbc.getEncode(), handlers);
        }
        if (bindingContext.containsBindingFor(SearchJdbcAuthenticationProperties.class)) {
            createHandler(jdbc.getSearch(), handlers);
        }
        if (bindingContext.containsBindingFor(ProcedureJdbcAuthenticationProperties.class)) {
            createHandler(jdbc.getProcedure(), handlers);
        }
        return handlers;
    }

    protected void createHandler(
        final List<? extends BaseJdbcAuthenticationProperties> container,
        final List<AuthenticationHandler> finalHandlers) {
        container.forEach(properties -> {
            val handler = JpaAuthenticationUtils.newAuthenticationHandler(properties, applicationContext,
                jdbcPrincipalFactory, passwordPolicyConfiguration);
            finalHandlers.add(handler.markDisposable());
        });
    }
}
