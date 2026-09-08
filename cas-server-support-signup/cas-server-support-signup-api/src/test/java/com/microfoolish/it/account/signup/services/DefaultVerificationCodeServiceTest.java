/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.services;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.verficationcode.cache.VerificationCodeCache;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeSender;
import com.microfoolish.it.account.signup.verficationcode.services.DefaultVerificationCodeService;
import junit.framework.TestCase;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author kenny.he
 * @since 2026/09/06
 */
public class DefaultVerificationCodeServiceTest extends TestCase {
    public void testCodeIsMixedAndConsumedAfterVerification() throws Exception {
        var cache = new InMemoryCache();
        var sentCode = new AtomicReference<String>();
        VerificationCodeSender sender = new VerificationCodeSender() {
            @Override
            public RegistrationType type() {
                return RegistrationType.EMAIL;
            }

            @Override
            public void send(final String target, final String code) {
                sentCode.set(code);
            }
        };
        var service = new DefaultVerificationCodeService(cache, List.of(sender), Duration.ofMinutes(5), new java.security.SecureRandom());
        service.send(new VerificationCodeRequest("email", "User@example.org", null));

        var code = sentCode.get();
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("[A-Za-z0-9]{6}"));
        assertTrue(service.verify(RegistrationType.EMAIL, "user@example.org", code));
        assertFalse(service.verify(RegistrationType.EMAIL, "user@example.org", code));
    }

    private static final class InMemoryCache implements VerificationCodeCache {
        private final java.util.Map<String, String> values = new java.util.concurrent.ConcurrentHashMap<>();

        @Override
        public void put(final String key, final String code, final Duration ttl) {
            values.put(key, code);
        }

        @Override
        public Optional<String> get(final String key) {
            return Optional.ofNullable(values.get(key));
        }

        @Override
        public void remove(final String key) {
            values.remove(key);
        }
    }
}
