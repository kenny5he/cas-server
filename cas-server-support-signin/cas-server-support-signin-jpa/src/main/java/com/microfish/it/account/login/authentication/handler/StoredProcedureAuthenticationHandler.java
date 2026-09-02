package com.microfish.it.account.login.authentication.handler;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.ParameterMode;

import lombok.val;

import org.apache.commons.lang3.BooleanUtils;

import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.ProcedureJdbcAuthenticationProperties;
import org.apereo.cas.util.CollectionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.FailedLoginException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Executes CAS's procedure authentication contract through JPA.
 *
 * 存储过程
 *
 * <p>The procedure must expose {@code username} and {@code password} input
 * parameters and a boolean {@code status} output parameter. The status value is
 * converted with CAS's boolean rules.</p>
 *
 * @author Misagh Moayyed
 * @since 7.2.0
 */
public class StoredProcedureAuthenticationHandler
    extends AbstractUsernamePasswordAuthenticationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoredProcedureAuthenticationHandler.class);

    private final ProcedureJdbcAuthenticationProperties properties;

    private final EntityManagerFactory entityManagerFactory;

    public StoredProcedureAuthenticationHandler(
        final ProcedureJdbcAuthenticationProperties properties,
        final PrincipalFactory principalFactory,
        final EntityManagerFactory entityManagerFactory) {
        super(properties.getName(), principalFactory, properties.getOrder());
        this.properties = properties;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(
        final UsernamePasswordCredential credential, final String originalPassword) throws Throwable {
        val username = credential.getUsername();
        val password = credential.toPassword();
        val entityManager = entityManagerFactory.createEntityManager();
        try {
            val procedure = entityManager.createStoredProcedureQuery(properties.getProcedureName());
            procedure.registerStoredProcedureParameter("username", String.class, ParameterMode.IN);
            procedure.registerStoredProcedureParameter("password", String.class, ParameterMode.IN);
            procedure.registerStoredProcedureParameter("status", Boolean.class, ParameterMode.OUT);
            procedure.setParameter("username", username);
            procedure.setParameter("password", password);
            procedure.execute();

            val status = procedure.getOutputParameterValue("status");
            LOGGER.debug("Procedure [{}] returned status [{}] for [{}]", properties.getProcedureName(), status, username);
            if (status == null || !BooleanUtils.toBoolean(status.toString())) {
                throw new FailedLoginException("Failed to authenticate user");
            }

            val attributes = new LinkedHashMap<String, Object>();
            attributes.put("status", status);
            val principal = principalFactory.createPrincipal(username, CollectionUtils.toMultiValuedMap(attributes));
            return createHandlerResult(credential, principal, new ArrayList<>());
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }
}
