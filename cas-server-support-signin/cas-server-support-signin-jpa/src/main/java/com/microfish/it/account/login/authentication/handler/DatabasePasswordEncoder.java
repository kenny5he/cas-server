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

import java.util.Map;

/**
 * This is {@link DatabasePasswordEncoder}.
 *
 * @author Misagh Moayyed
 * @since 7.0.0
 */
@FunctionalInterface
public interface DatabasePasswordEncoder {
    /**
     * Encode the given password, give the results of the SQL query.
     * The provided password is often supplied by the user, and is then encoded
     * and digested using the query results here (salt, iterations, etc) that attached
     * to that record and password. The final result, that is the encoded password, can then
     * be compared with the actual encoded password found for the user record.
     *
     * @param password    the password
     * @param queryValues the query values
     * @return the object
     */
    String encode(String password, Map<String, Object> queryValues);
}
