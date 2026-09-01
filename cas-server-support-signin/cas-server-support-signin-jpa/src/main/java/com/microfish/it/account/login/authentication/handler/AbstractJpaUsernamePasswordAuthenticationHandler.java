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
import org.apereo.cas.authentication.CoreAuthenticationUtils;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.configuration.model.support.jdbc.authn.BaseJdbcAuthenticationProperties;
import org.apereo.cas.util.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class AbstractJpaUsernamePasswordAuthenticationHandler<T extends BaseJdbcAuthenticationProperties> extends AbstractUsernamePasswordAuthenticationHandler {

    protected final T properties;

    protected final JdbcTemplate jdbcTemplate;

    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected final DataSource dataSource;

    protected AbstractJpaUsernamePasswordAuthenticationHandler(final T properties,
                                                                final PrincipalFactory principalFactory,
                                                                final DataSource dataSource) {
        super(properties.getName(), principalFactory, properties.getOrder());
        this.properties = properties;
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }


    protected Map<String, List<Object>> collectPrincipalAttributes(final Map<String, Object> dbFields) {
        val attributes = new HashMap<String, List<Object>>();
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

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws Throwable {
        return null;
    }
}
