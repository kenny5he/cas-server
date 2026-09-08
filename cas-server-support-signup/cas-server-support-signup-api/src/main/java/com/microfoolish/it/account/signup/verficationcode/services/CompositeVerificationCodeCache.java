/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode.services;

import com.microfoolish.it.account.signup.verficationcode.cache.VerificationCodeCache;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Writes codes to the local JVM cache and, when configured, to Redis. Redis is
 * deliberately optional so a single-node installation remains usable while a
 * clustered installation gets shared verification state.
 */
public class CompositeVerificationCodeCache implements VerificationCodeCache {
    private final Map<String, LocalValue> local = new ConcurrentHashMap<>();

    private final ObjectProvider<StringRedisTemplate> redisTemplate;

    public CompositeVerificationCodeCache(final ObjectProvider<StringRedisTemplate> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void put(final String key, final String code, final Duration ttl) {
        var expiresAt = Instant.now().plus(ttl);
        local.put(key, new LocalValue(code, expiresAt));
        var redis = redisTemplate == null ? null : redisTemplate.getIfAvailable();
        if (redis != null) {
            try {
                redis.opsForValue().set(key, code, ttl);
            } catch (final RuntimeException ignored) {
                // The local cache still provides a bounded fallback when Redis is unavailable.
            }
        }
    }

    @Override
    public Optional<String> get(final String key) {
        var value = local.get(key);
        if (value != null) {
            if (value.expiresAt().isAfter(Instant.now())) {
                return Optional.of(value.code());
            }
            local.remove(key, value);
        }
        var redis = redisTemplate == null ? null : redisTemplate.getIfAvailable();
        if (redis != null) {
            try {
                return Optional.ofNullable(redis.opsForValue().get(key));
            } catch (final RuntimeException ignored) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public void remove(final String key) {
        local.remove(key);
        var redis = redisTemplate == null ? null : redisTemplate.getIfAvailable();
        if (redis != null) {
            try {
                redis.delete(key);
            } catch (final RuntimeException ignored) {
                // Expiration in Redis remains the final cleanup mechanism.
            }
        }
    }

    private record LocalValue(String code, Instant expiresAt) {
    }
}
