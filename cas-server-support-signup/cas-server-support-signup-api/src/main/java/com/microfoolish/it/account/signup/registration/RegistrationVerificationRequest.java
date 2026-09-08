/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.registration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;

/** Registration details submitted together with a verification code. */
public record RegistrationVerificationRequest(
        String registrationType,
        String verificationCode,
        Map<String, Object> properties) {

    public RegistrationVerificationRequest {
        properties = properties == null
            ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(properties));
    }

    public RegistrationType type() {
        return RegistrationType.parse(registrationType);
    }
}
