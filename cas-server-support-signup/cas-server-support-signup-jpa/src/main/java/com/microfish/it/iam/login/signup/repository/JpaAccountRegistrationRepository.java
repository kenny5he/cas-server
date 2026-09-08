/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfish.it.iam.login.signup.repository;

import com.microfish.it.iam.login.signup.entity.JpaAccountRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaAccountRegistrationRepository extends JpaRepository<JpaAccountRegistrationEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByCallingCodeAndPhoneNumber(String callingCode, String phoneNumber);
}
