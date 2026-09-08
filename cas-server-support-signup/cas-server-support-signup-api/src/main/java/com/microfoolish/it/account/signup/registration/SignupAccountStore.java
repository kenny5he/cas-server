/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.registration;

import org.apereo.cas.acct.AccountRegistrationRequest;

public interface SignupAccountStore {
    boolean existsByEmail(String email);

    boolean existsByPhone(String callingCode, String phoneNumber);

    String save(AccountRegistrationRequest request, RegistrationType type);
}
