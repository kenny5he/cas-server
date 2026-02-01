/**
 * Copyright 2022 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfoolish.it.sso.cas.account.config;

import org.apereo.cas.acct.AccountRegistrationRequest;
import org.apereo.cas.acct.AccountRegistrationResponse;
import org.apereo.cas.acct.provision.AccountRegistrationProvisioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kenny.he
 * @since 2022/10/04
 */
public class JpaAccountRegistrationProvisioner implements AccountRegistrationProvisioner {
    private static final Logger logger = LoggerFactory.getLogger(JpaAccountRegistrationProvisioner.class);
    @Override
    public AccountRegistrationResponse provision(AccountRegistrationRequest request) throws Exception {
        logger.info("{}",request);
        return null;
    }
}
