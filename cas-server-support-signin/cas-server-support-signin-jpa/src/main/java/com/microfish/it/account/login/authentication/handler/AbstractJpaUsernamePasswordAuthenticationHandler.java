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

import javax.security.auth.login.AccountExpiredException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.BaseJdbcAuthenticationProperties;
import org.apereo.cas.util.CollectionUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.microfish.it.account.login.authentication.entity.AccountEntity;
import com.microfish.it.account.login.authentication.repository.AccountRepository;

/**
 * Base username/password handler backed by the application's Spring Data JPA repository.
 * The logical CAS username is resolved against {@link AccountEntity#getCode()}.
 */
@Getter
@Slf4j
public abstract class AbstractJpaUsernamePasswordAuthenticationHandler<T extends BaseJdbcAuthenticationProperties>
    extends AbstractUsernamePasswordAuthenticationHandler {

    protected final T properties;

    protected final AccountRepository accountRepository;

    protected AbstractJpaUsernamePasswordAuthenticationHandler(final T properties,
                                                                final PrincipalFactory principalFactory,
                                                                final AccountRepository accountRepository) {
        super(properties.getName(), principalFactory, properties.getOrder());
        this.properties = properties;
        this.accountRepository = accountRepository;
    }

    protected Map<String, List<Object>> collectPrincipalAttributes(final Map<String, Object> dbFields) {
        val attributes = new LinkedHashMap<String, List<Object>>();
        val principalAttributeMap = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(properties.getPrincipalAttributeList());
        principalAttributeMap.forEach((key, names) -> {
            val attribute = dbFields.get(key);
            if (attribute != null) {
                LOGGER.debug("Found attribute [{}] from the query results", key);
                val attributeNames = CollectionUtils.toCollection(names);
                attributeNames.forEach(attrName -> {
                    LOGGER.debug("Principal attribute [{}] is virtually remapped/renamed to [{}]", key, attrName);
                    attributes.put(attrName.toString(), CollectionUtils.wrap(attribute.toString()));
                });
            } else {
                LOGGER.warn("Requested attribute [{}] could not be found in the query results", key);
            }
        });
        return attributes;
    }

    protected AccountEntity findAccount(final String username) throws AccountNotFoundException {
        return accountRepository.findByUsername(username)
            .orElseThrow(() -> new AccountNotFoundException(username + " not found with JPA query"));
    }

    protected void verifyAccountStatus(final AccountEntity account) throws AccountDisabledException, AccountExpiredException {
        if (account.getExpired() != 0) {
            throw new AccountDisabledException("Account has been disabled");
        }

        val now = LocalDateTime.now();
        if (account.getEffectiveTime() != null && now.isBefore(account.getEffectiveTime())) {
            throw new AccountDisabledException("Account is not effective yet");
        }
        if (account.getExpirationTime() != null && !now.isBefore(account.getExpirationTime())) {
            throw new AccountExpiredException("Account has expired");
        }
    }

    protected Map<String, Object> collectAccountFields(final AccountEntity account) {
        val fields = new LinkedHashMap<String, Object>();
        putAccountField(fields, account.getId(), "id");
        putAccountField(fields, account.getCode(), "code", "username");
        putAccountField(fields, account.getName(), "name");
        putAccountField(fields, account.getFirstName(), "firstName", "first_name");
        putAccountField(fields, account.getLastName(), "lastName", "last_name");
        putAccountField(fields, account.getNikeName(), "nikeName", "nike_name");
        putAccountField(fields, account.getType(), "type");
        putAccountField(fields, account.getPassword(), "password");
        putAccountField(fields, account.getEmail(), "email");
        putAccountField(fields, account.getEmail_reverse(), "email_reverse");
        putAccountField(fields, account.getCallingCode(), "callingCode", "calling_code");
        putAccountField(fields, account.getPhoneNumber(), "phoneNumber", "phone_number");
        putAccountField(fields, account.getExpired(), "expired");
        putAccountField(fields, account.getEffectiveTime(), "effectiveTime", "effective_time");
        putAccountField(fields, account.getExpirationTime(), "expirationTime", "expiration_time");
        return fields;
    }

    private static void putAccountField(final Map<String, Object> fields,
                                        final Object value,
                                        final String... names) {
        if (value != null) {
            for (val name : names) {
                fields.put(name, value);
            }
        }
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
        final UsernamePasswordCredential credential, final String originalPassword) throws Throwable {
        val username = credential.getUsername();
        try {
            val account = findAccount(username);
            val databasePassword = account.getPassword();
            val suppliedPassword = credential.toPassword();
            val originalPasswordMatchFails = StringUtils.isNotBlank(originalPassword)
                && !matches(originalPassword, databasePassword);
            val transformedPasswordMatchFails = StringUtils.isBlank(originalPassword)
                && !Strings.CI.equals(suppliedPassword, databasePassword);
            if (originalPasswordMatchFails || transformedPasswordMatchFails) {
                throw new FailedLoginException("Password does not match value on record");
            }

            verifyAccountStatus(account);
            val principal = principalFactory.createPrincipal(username,
                collectPrincipalAttributes(collectAccountFields(account)));
            return createHandlerResult(credential, principal, new ArrayList<>());
        } catch (final IncorrectResultSizeDataAccessException e) {
            throw new FailedLoginException("Multiple records found for " + username);
        } catch (final DataAccessException e) {
            throw new PreventedException(e);
        }
    }
}
