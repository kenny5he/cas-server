/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.registration;

import java.util.Locale;

/** The two supported account-registration channels. */
public enum RegistrationType {
    EMAIL,
    PHONE;

    /**
     * Parse a client supplied channel name.
     *
     * @param value channel name
     * @return parsed channel
     */
    public static RegistrationType parse(final String value) {
        if (value == null || value.isBlank()) {
            return EMAIL;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "email", "mail" -> EMAIL;
            case "phone", "mobile", "sms" -> PHONE;
            default -> throw new IllegalArgumentException("Unsupported registration type");
        };
    }
}
