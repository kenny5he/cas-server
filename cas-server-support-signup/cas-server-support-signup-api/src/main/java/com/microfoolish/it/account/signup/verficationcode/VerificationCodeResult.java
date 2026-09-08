/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode;

import com.microfoolish.it.account.signup.registration.RegistrationType;

import java.time.Instant;

/** Public result of sending a verification code; the code itself is never returned. */
public record VerificationCodeResult(RegistrationType registrationType, Instant expiresAt) {
}
