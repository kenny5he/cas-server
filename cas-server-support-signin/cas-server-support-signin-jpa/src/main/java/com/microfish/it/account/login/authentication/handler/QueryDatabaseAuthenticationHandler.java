package com.microfish.it.account.login.authentication.handler;

import com.microfish.it.account.login.authentication.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.monitor.Monitorable;

/**
 * Authenticates an account loaded through Spring Data JPA.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@Slf4j
@Monitorable
public class QueryDatabaseAuthenticationHandler extends AbstractJpaUsernamePasswordAuthenticationHandler<QueryJdbcAuthenticationProperties> {

    public QueryDatabaseAuthenticationHandler(final QueryJdbcAuthenticationProperties properties,
                                               final PrincipalFactory principalFactory,
                                               final AccountRepository accountRepository) {
        super(properties, principalFactory, accountRepository);
    }
}
