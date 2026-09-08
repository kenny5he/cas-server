/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfoolish.it.account.signup.verficationcode.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.verficationcode.cache.VerificationCodeCache;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeResult;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeSender;


@Slf4j
public class DefaultVerificationCodeService implements VerificationCodeService {
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);

    private static final String CACHE_PREFIX = "cas:signup:verification:";

    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    private static final String DIGITS = "0123456789";

    private final VerificationCodeCache cache;

    private final Map<RegistrationType, VerificationCodeSender> senders = new EnumMap<>(RegistrationType.class);

    private final SecureRandom random;

    private final Duration ttl;

    public DefaultVerificationCodeService(final VerificationCodeCache cache,
                                           final Iterable<VerificationCodeSender> senders) {
        this(cache, senders, DEFAULT_TTL, new SecureRandom());
    }

    public DefaultVerificationCodeService(final VerificationCodeCache cache,
                                          final Iterable<VerificationCodeSender> senders,
                                          final Duration ttl,
                                          final SecureRandom random) {
        this.cache = cache;
        this.ttl = ttl == null || ttl.isNegative() || ttl.isZero() ? DEFAULT_TTL : ttl;
        this.random = random == null ? new SecureRandom() : random;
        senders.forEach(sender -> this.senders.put(sender.type(), sender));
    }

    @Override
    public VerificationCodeResult send(final VerificationCodeRequest request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("Verification-code request is required");
        }
        var type = request.type();
        var target = type == RegistrationType.PHONE
            ? normalizePhoneTarget(request.callingCode(), request.target())
            : normalizeTarget(type, request.target());
        var sender = senders.get(type);
        if (sender == null) {
            throw new IllegalStateException("No verification-code sender is configured for " + type);
        }
        var code = generateCode();
        var key = cacheKey(type, target);
        cache.put(key, code, ttl);
        try {
            sender.send(target, code);
        } catch (final Exception e) {
            cache.remove(key);
            throw e;
        }
        return new VerificationCodeResult(type, Instant.now().plus(ttl));
    }

    @Override
    public boolean verify(final RegistrationType type, final String target, final String code) {
        if (type == null || !StringUtils.hasText(code) || code.trim().length() != 6) {
            return false;
        }
        final String normalizedTarget;
        try {
            normalizedTarget = normalizeTarget(type, target);
        } catch (final IllegalArgumentException e) {
            return false;
        }
        var key = cacheKey(type, normalizedTarget);
        var expected = cache.get(key);
        if (expected.isEmpty()) {
            return false;
        }
        var matches = MessageDigest.isEqual(expected.get().getBytes(StandardCharsets.US_ASCII),
            code.trim().getBytes(StandardCharsets.US_ASCII));
        if (matches) {
            cache.remove(key);
        }
        return matches;
    }

    public String cacheKey(final RegistrationType type, final String target) {
        return CACHE_PREFIX + type.name().toLowerCase(Locale.ROOT) + ":" + target;
    }

    public String normalizeTarget(final RegistrationType type, final String target) {
        if (!StringUtils.hasText(target)) {
            throw new IllegalArgumentException("Verification target is required");
        }
        var normalized = target.trim();
        if (type == RegistrationType.EMAIL) {
            if (!normalized.matches("(?i)^[a-z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?(?:\\.[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?)+$")) {
                throw new IllegalArgumentException("Invalid email address");
            }
            return normalized.toLowerCase(Locale.ROOT);
        }
        var phone = normalized.replaceAll("[\\s-]", "");
        if (!phone.matches("^\\+?[0-9]{6,20}$")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        return phone;
    }

    /** Normalize a phone number and optional country calling code to one cache key/transport target. */
    public String normalizePhoneTarget(final String callingCode, final String target) {
        var phone = normalizeTarget(RegistrationType.PHONE, target);
        if (phone.startsWith("+") || !StringUtils.hasText(callingCode)) {
            return phone;
        }
        var code = callingCode.trim().replaceAll("[\\s-]", "");
        if (!code.startsWith("+")) {
            code = "+" + code;
        }
        if (!code.matches("^\\+[0-9]{1,4}$")) {
            throw new IllegalArgumentException("Invalid calling code");
        }
        return code + phone;
    }

    private String generateCode() {
        var chars = new char[6];
        chars[0] = DIGITS.charAt(random.nextInt(DIGITS.length()));
        chars[1] = LETTERS.charAt(random.nextInt(LETTERS.length()));
        var alphabet = DIGITS + LETTERS;
        for (var i = 2; i < chars.length; i++) {
            chars[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        }
        for (var i = chars.length - 1; i > 0; i--) {
            var j = random.nextInt(i + 1);
            var current = chars[i];
            chars[i] = chars[j];
            chars[j] = current;
        }
        return new String(chars);
    }
}
