package com.microfish.it.account.login.authentication.handler;

import com.microfish.it.account.login.authentication.repository.AccountRepository;
import lombok.val;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.monitor.Monitorable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;

/**
 * A JPA querying handler that loads the account through {@code AccountRepository}
 * and validates the encoded password using the configured database encoding
 * settings. Dynamic salt and iteration values are read from the mapped account
 * field map when those fields are present.
 * <p>
 * If the hashing behavior and/or configuration
 * of private and public salts does not meet your needs, a extension can be developed
 * to specify alternative methods of encoding and digestion of the encoded password.
 * </p>
 *
 * @author Misagh Moayyed
 * @author Charles Hasegawa
 * @since 4.1.0
 */
@Monitorable
public class QueryAndEncodeDatabaseAuthenticationHandler extends AbstractJpaUsernamePasswordAuthenticationHandler<QueryEncodeJdbcAuthenticationProperties> {

    private final DatabasePasswordEncoder databasePasswordEncoder;

    public QueryAndEncodeDatabaseAuthenticationHandler(final QueryEncodeJdbcAuthenticationProperties properties,
                                                       final PrincipalFactory principalFactory,
                                                       final AccountRepository accountRepository,
                                                       final DatabasePasswordEncoder databasePasswordEncoder) {
        super(properties, principalFactory, accountRepository);
        this.databasePasswordEncoder = databasePasswordEncoder;
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
        final UsernamePasswordCredential transformedCredential, final String originalPassword) throws Throwable {
        val username = transformedCredential.getUsername();
        try {
            val account = findAccount(username);
            val accountFields = collectAccountFields(account);
            accountFields.put(properties.getPasswordFieldName(), account.getPassword());
            val digestedPassword = databasePasswordEncoder.encode(transformedCredential.toPassword(), accountFields);

            if (!account.getPassword().equals(digestedPassword)) {
                throw new FailedLoginException("Password does not match value on record.");
            }
            verifyAccountStatus(account);
            val attributes = collectPrincipalAttributes(accountFields);
            val principal = principalFactory.createPrincipal(username, attributes);
            return createHandlerResult(transformedCredential, principal, new ArrayList<>());
        } catch (final IncorrectResultSizeDataAccessException e) {
            if (e.getActualSize() == 0) {
                throw new AccountNotFoundException(username + " not found with JPA query");
            }
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException(e);
        }
    }
}
