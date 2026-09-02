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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.BindJdbcAuthenticationProperties;
import org.apereo.cas.monitor.Monitorable;

import javax.security.auth.login.FailedLoginException;
import javax.sql.DataSource;
import java.util.ArrayList;

/**
 * This class attempts to authenticate the user by opening a connection to the
 * database with the provided username and password. Servers are provided as a
 * Properties class with the key being the URL and the property being the type
 * of database driver needed.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @since 3.0.0
 */

@Slf4j
@Monitorable
public class BindModeSearchDatabaseAuthenticationHandler extends AbstractUsernamePasswordAuthenticationHandler {

    @Getter
    private final DataSource dataSource;

    public BindModeSearchDatabaseAuthenticationHandler(
        final BindJdbcAuthenticationProperties properties,
        final PrincipalFactory principalFactory, final DataSource dataSource) {
        super(properties.getName(), principalFactory, properties.getOrder());
        this.dataSource = dataSource;
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
        final UsernamePasswordCredential credential, final String originalPassword) throws Throwable {
        val username = credential.getUsername();
        val password = credential.toPassword();
        try (val connection = getDataSource().getConnection(username, password)) {
            LOGGER.trace("Established connection to schema [{}]", connection.getSchema());
            val principal = principalFactory.createPrincipal(username);
            return createHandlerResult(credential, principal, new ArrayList<>());
        } catch (final Throwable e) {
            throw new FailedLoginException(e.getMessage());
        }
    }
}
