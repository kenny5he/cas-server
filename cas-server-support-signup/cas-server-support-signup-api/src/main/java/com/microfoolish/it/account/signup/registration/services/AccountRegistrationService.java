/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.registration.services;

import java.util.List;

import com.microfoolish.it.account.signup.registration.AccountSignupProperty;
import com.microfoolish.it.account.signup.registration.RegistrationTokenResult;
import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.registration.RegistrationVerificationRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeResult;

public interface AccountRegistrationService {

    List<AccountSignupProperty> loadProperties(RegistrationType type);

    VerificationCodeResult sendVerificationCode(VerificationCodeRequest request) throws Exception;

    RegistrationTokenResult verifyAndRegister(RegistrationVerificationRequest request);
}
