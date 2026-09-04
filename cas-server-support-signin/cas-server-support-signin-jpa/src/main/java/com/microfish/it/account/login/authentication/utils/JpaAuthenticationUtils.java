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

package com.microfish.it.account.login.authentication.utils;

import jakarta.persistence.EntityManagerFactory;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.apache.commons.lang3.StringUtils;

import org.springframework.context.ConfigurableApplicationContext;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalNameTransformerUtils;
import org.apereo.cas.configuration.support.JpaBeans;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.authentication.support.password.PasswordPolicyContext;
import org.apereo.cas.configuration.model.support.jdbc.authn.BaseJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.BindJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.ProcedureJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryEncodeJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.QueryJdbcAuthenticationProperties;
import org.apereo.cas.configuration.model.support.jdbc.authn.SearchJdbcAuthenticationProperties;

import com.microfish.it.account.login.authentication.handler.BindModeSearchDatabaseAuthenticationHandler;
import com.microfish.it.account.login.authentication.handler.QueryAndEncodeDatabaseAuthenticationHandler;
import com.microfish.it.account.login.authentication.handler.QueryAndEncodeDatabasePasswordEncoder;
import com.microfish.it.account.login.authentication.handler.QueryDatabaseAuthenticationHandler;
import com.microfish.it.account.login.authentication.handler.SearchModeSearchDatabaseAuthenticationHandler;
import com.microfish.it.account.login.authentication.handler.StoredProcedureAuthenticationHandler;
import com.microfish.it.account.login.authentication.repository.AccountRepository;

/**
 * A JDBC utility class.
 *
 * @author Jerome LELEU
 * @since 7.0.0
 */
@UtilityClass
@Slf4j
public class JpaAuthenticationUtils {

    /**
     * Configure a JDBC authentication handler.
     *
     * @param handler            the authn handler
     * @param config             the password policy
     * @param properties         the JDBC properties
     * @param applicationContext the application context
     */
    public static void configureJdbcAuthenticationHandler(final AbstractUsernamePasswordAuthenticationHandler handler,
                                                          final PasswordPolicyContext config,
                                                          final BaseJdbcAuthenticationProperties properties,
                                                          final ConfigurableApplicationContext applicationContext) {
        handler.setPasswordEncoder(PasswordEncoderUtils.newPasswordEncoder(properties.getPasswordEncoder(), applicationContext));
        handler.setPrincipalNameTransformer(PrincipalNameTransformerUtils.newPrincipalNameTransformer(properties.getPrincipalTransformation()));
        handler.setPasswordPolicyConfiguration(config);
        handler.setState(properties.getState());
        if (StringUtils.isNotBlank(properties.getCredentialCriteria())) {
            handler.setCredentialSelectionPredicate(CoreAuthenticationUtils.newCredentialSelectionPredicate(properties.getCredentialCriteria()));
        }
        LOGGER.trace("Configured authentication handler [{}] to handle database url at [{}]", handler.getName(), properties.getName());
    }

    /**
     * New authentication handler.
     *
     * @param properties           the properties
     * @param applicationContext   the application context
     * @param jdbcPrincipalFactory the jdbc principal factory
     * @param passwordPolicy       the password policy
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final BindJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext passwordPolicy) {
        val handler = new BindModeSearchDatabaseAuthenticationHandler(properties,
            jdbcPrincipalFactory, JpaBeans.newDataSource(properties));
        configureJdbcAuthenticationHandler(handler, passwordPolicy, properties, applicationContext);
        return handler;
    }

    /**
     * New authentication handler.
     *
     * @param properties                                the properties
     * @param applicationContext                        the application context
     * @param jdbcPrincipalFactory                      the jdbc principal factory
     * @param queryAndEncodePasswordPolicyConfiguration the query and encode password policy configuration
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final QueryEncodeJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext queryAndEncodePasswordPolicyConfiguration) {
        return newAuthenticationHandler(properties, applicationContext, jdbcPrincipalFactory,
            queryAndEncodePasswordPolicyConfiguration, applicationContext.getBean(AccountRepository.class));
    }

    /**
     * New authentication handler.
     *
     * @param properties                                the properties
     * @param applicationContext                        the application context
     * @param jdbcPrincipalFactory                      the jdbc principal factory
     * @param queryAndEncodePasswordPolicyConfiguration the query and encode password policy configuration
     * @param accountRepository                         the account repository
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final QueryEncodeJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext queryAndEncodePasswordPolicyConfiguration,
                                                                 final AccountRepository accountRepository) {
        val databasePasswordEncoder = new QueryAndEncodeDatabasePasswordEncoder(properties);
        val handler = new QueryAndEncodeDatabaseAuthenticationHandler(properties,
            jdbcPrincipalFactory, accountRepository, databasePasswordEncoder);
        configureJdbcAuthenticationHandler(handler, queryAndEncodePasswordPolicyConfiguration, properties, applicationContext);
        return handler;
    }

    /**
     * New authentication handler.
     *
     * @param properties                       the properties
     * @param applicationContext               the application context
     * @param jdbcPrincipalFactory             the jdbc principal factory
     * @param queryPasswordPolicyConfiguration the query password policy configuration
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final QueryJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext queryPasswordPolicyConfiguration) {
        return newAuthenticationHandler(properties, applicationContext, jdbcPrincipalFactory,
            queryPasswordPolicyConfiguration, applicationContext.getBean(AccountRepository.class));
    }

    /**
     * New JPA query authentication handler.
     *
     * @param properties                       the query properties
     * @param applicationContext               the application context
     * @param jdbcPrincipalFactory             the principal factory
     * @param queryPasswordPolicyConfiguration the password policy configuration
     * @param accountRepository                the Spring Data JPA repository
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final QueryJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext queryPasswordPolicyConfiguration,
                                                                 final AccountRepository accountRepository) {

        val handler = new QueryDatabaseAuthenticationHandler(properties, jdbcPrincipalFactory, accountRepository);
        configureJdbcAuthenticationHandler(handler, queryPasswordPolicyConfiguration, properties, applicationContext);
        return handler;
    }

    /**
     * New authentication handler.
     *
     * @param properties                            the properties
     * @param applicationContext                    the application context
     * @param jdbcPrincipalFactory                  the jdbc principal factory
     * @param searchModePasswordPolicyConfiguration the search mode password policy configuration
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final SearchJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext searchModePasswordPolicyConfiguration) {
        return newAuthenticationHandler(properties, applicationContext, jdbcPrincipalFactory,
            searchModePasswordPolicyConfiguration, applicationContext.getBean(AccountRepository.class));
    }

    /**
     * New JPA search authentication handler.
     *
     * @param properties                            the search properties
     * @param applicationContext                    the application context
     * @param jdbcPrincipalFactory                  the principal factory
     * @param searchModePasswordPolicyConfiguration the password policy configuration
     * @param accountRepository                     the Spring Data JPA repository
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final SearchJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext searchModePasswordPolicyConfiguration,
                                                                 final AccountRepository accountRepository) {
        val handler = new SearchModeSearchDatabaseAuthenticationHandler(properties, jdbcPrincipalFactory, accountRepository);
        configureJdbcAuthenticationHandler(handler, searchModePasswordPolicyConfiguration, properties, applicationContext);
        return handler;
    }

    /**
     * New authentication handler.
     *
     * @param properties                            the properties
     * @param applicationContext                    the application context
     * @param jdbcPrincipalFactory                  the jdbc principal factory
     * @param searchModePasswordPolicyConfiguration the search mode password policy configuration
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final ProcedureJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext searchModePasswordPolicyConfiguration) {
        return newAuthenticationHandler(properties, applicationContext, jdbcPrincipalFactory,
            searchModePasswordPolicyConfiguration, applicationContext.getBean(EntityManagerFactory.class));
    }

    /**
     * New JPA procedure-compatible authentication handler.
     *
     * @param properties                            the procedure properties
     * @param applicationContext                    the application context
     * @param jdbcPrincipalFactory                  the principal factory
     * @param searchModePasswordPolicyConfiguration the password policy configuration
     * @param entityManagerFactory                  the JPA entity manager factory
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final ProcedureJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext searchModePasswordPolicyConfiguration,
                                                                 final EntityManagerFactory entityManagerFactory) {
        val handler = new StoredProcedureAuthenticationHandler(properties, jdbcPrincipalFactory, entityManagerFactory);
        configureJdbcAuthenticationHandler(handler, searchModePasswordPolicyConfiguration, properties, applicationContext);
        return handler;
    }

    /**
     * New authentication handler.
     *
     * @param properties            the properties
     * @param applicationContext    the application context
     * @param jdbcPrincipalFactory  the jdbc principal factory
     * @param passwordPolicyContext the search mode password policy configuration
     * @return the authentication handler
     */
    public static AuthenticationHandler newAuthenticationHandler(final BaseJdbcAuthenticationProperties properties,
                                                                 final ConfigurableApplicationContext applicationContext,
                                                                 final PrincipalFactory jdbcPrincipalFactory,
                                                                 final PasswordPolicyContext passwordPolicyContext) {
        return switch (properties) {
            case QueryJdbcAuthenticationProperties query -> newAuthenticationHandler(query, applicationContext, jdbcPrincipalFactory, passwordPolicyContext);
            case QueryEncodeJdbcAuthenticationProperties queryEncode -> newAuthenticationHandler(queryEncode, applicationContext, jdbcPrincipalFactory, passwordPolicyContext);
            case ProcedureJdbcAuthenticationProperties procedure -> newAuthenticationHandler(procedure, applicationContext, jdbcPrincipalFactory, passwordPolicyContext);
            case SearchJdbcAuthenticationProperties search -> newAuthenticationHandler(search, applicationContext, jdbcPrincipalFactory, passwordPolicyContext);
            case BindJdbcAuthenticationProperties bind -> newAuthenticationHandler(bind, applicationContext, jdbcPrincipalFactory, passwordPolicyContext);
            default -> throw new IllegalStateException("Unexpected value: " + properties.getClass().getName());
        };
    }
}
