/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode;

import com.microfoolish.it.account.signup.registration.RegistrationType;

/** Request to send a registration verification code. */
public record VerificationCodeRequest(String registrationType, String target, String callingCode) {
    public RegistrationType type() {
        return RegistrationType.parse(registrationType);
    }
}
