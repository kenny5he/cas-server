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

package com.microfish.it.account.login.authentication.repository;

import com.microfish.it.account.login.authentication.entity.AccountEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    /**
     * 通过用户名查询用户信息
     * @param username
     * @return 用户
     */
    @Query("select a from JpaAccountEntity a where a.code = :username")
    Optional<AccountEntity> findByUsername(@Param("username") String username);

    /**
     * 通过 邮箱 查询用户信息
     * @param email 邮箱
     * @return 用户
     */
    Optional<AccountEntity> findByEmail(String email);

    /**
     * 通过 手机号码 查询用户信息
     * @param callingCode 国家电话区号
     * @param phoneNumber 手机号码
     * @return 用户
     */
    @Query("select a from JpaAccountEntity a where a.callingCode = :callingCode and a.phoneNumber = :phoneNumber")
    Optional<AccountEntity> findByPhoneNumber(@Param("callingCode") String callingCode,
                                              @Param("phoneNumber") String phoneNumber);
}
