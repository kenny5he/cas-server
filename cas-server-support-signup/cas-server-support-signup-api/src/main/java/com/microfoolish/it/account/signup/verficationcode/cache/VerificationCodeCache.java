/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode.cache;

import java.time.Duration;
import java.util.Optional;

/** Stores short-lived verification codes without persisting them in the database. */
public interface VerificationCodeCache {
    void put(String key, String code, Duration ttl);

    Optional<String> get(String key);

    void remove(String key);
}
