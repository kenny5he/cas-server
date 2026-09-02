package com.microfish.it.account.login.authentication.handler;

import com.microfish.it.account.login.authentication.repository.AccountRepository;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.SearchJdbcAuthenticationProperties;
import org.apereo.cas.monitor.Monitorable;

/**
 * Search authentication backed by the application's Spring Data JPA account
 * repository. The legacy table and field properties are retained for CAS
 * configuration compatibility, but the account is resolved by {@code code}
 * and its password is validated by the base JPA handler.
 *
 * @author Scott Battaglia
 * @author Dmitriy Kopylenko
 * @author Marvin S. Addison
 * @since 3.0.0
 */
@Monitorable
public class SearchModeSearchDatabaseAuthenticationHandler
    extends AbstractJpaUsernamePasswordAuthenticationHandler<SearchJdbcAuthenticationProperties> {

    public SearchModeSearchDatabaseAuthenticationHandler(final SearchJdbcAuthenticationProperties properties,
                                                         final PrincipalFactory principalFactory,
                                                         final AccountRepository accountRepository) {
        super(properties, principalFactory, accountRepository);
    }
}
