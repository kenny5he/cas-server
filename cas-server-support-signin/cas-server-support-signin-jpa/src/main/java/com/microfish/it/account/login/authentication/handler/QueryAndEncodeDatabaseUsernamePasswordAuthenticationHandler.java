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

import com.microfish.it.account.login.authentication.repository.AccountRepository;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryEncodeJdbcAuthenticationProperties;

public class QueryAndEncodeDatabaseUsernamePasswordAuthenticationHandler extends AbstractJpaUsernamePasswordAuthenticationHandler<QueryEncodeJdbcAuthenticationProperties> {

    protected QueryAndEncodeDatabaseUsernamePasswordAuthenticationHandler(final QueryEncodeJdbcAuthenticationProperties properties,
                                                                          final PrincipalFactory principalFactory,
                                                                          final AccountRepository accountRepository) {
        super(properties, principalFactory, accountRepository);
    }
}
