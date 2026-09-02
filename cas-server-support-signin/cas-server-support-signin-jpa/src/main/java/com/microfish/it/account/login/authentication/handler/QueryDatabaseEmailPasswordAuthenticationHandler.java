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

import javax.security.auth.login.AccountNotFoundException;

import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;

import com.microfish.it.account.login.authentication.entity.AccountEntity;
import com.microfish.it.account.login.authentication.repository.AccountRepository;

public class QueryDatabaseEmailPasswordAuthenticationHandler extends AbstractJpaUsernamePasswordAuthenticationHandler<QueryJdbcAuthenticationProperties> {

    protected QueryDatabaseEmailPasswordAuthenticationHandler(final QueryJdbcAuthenticationProperties properties,
                                                              final PrincipalFactory principalFactory,
                                                              final AccountRepository accountRepository) {
        super(properties, principalFactory, accountRepository);
    }

    @Override
    protected AccountEntity findAccount(final String email) throws AccountNotFoundException {
        return accountRepository.findByEmail(email)
            .orElseThrow(() -> new AccountNotFoundException(email + " not found with JPA query"));
    }
}
