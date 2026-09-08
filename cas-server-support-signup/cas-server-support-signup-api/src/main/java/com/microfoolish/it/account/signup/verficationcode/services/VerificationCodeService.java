/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode.services;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeResult;

/** Generates, sends and consumes registration verification codes. */
public interface VerificationCodeService {
    VerificationCodeResult send(VerificationCodeRequest request) throws Exception;

    boolean verify(RegistrationType type, String target, String code);
}
