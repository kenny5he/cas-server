/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.microfoolish.it.account.signup.configuration;

import com.microfoolish.it.account.signup.registration.RegistrationTokenResult;
import com.microfoolish.it.account.signup.registration.RegistrationType;
import com.microfoolish.it.account.signup.registration.RegistrationVerificationRequest;
import com.microfoolish.it.account.signup.registration.services.AccountRegistrationService;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeRequest;
import com.microfoolish.it.account.signup.verficationcode.VerificationCodeResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class SignupRegistrationController {
    private final AccountRegistrationService registrationService;

    public SignupRegistrationController(final AccountRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/properties")
    public List<?> properties(final @RequestParam(required = false, defaultValue = "email") String registrationType) {
        return registrationService.loadProperties(RegistrationType.parse(registrationType));
    }

    @GetMapping("/properties/{registrationType}")
    public List<?> propertiesByPath(final @PathVariable String registrationType) {
        return registrationService.loadProperties(RegistrationType.parse(registrationType));
    }

    @PostMapping("/code")
    public VerificationCodeResult sendCode(final @RequestBody VerificationCodeRequest request) throws Exception {
        return registrationService.sendVerificationCode(request);
    }

    @PostMapping("/verify")
    public RegistrationTokenResult verify(final @RequestBody RegistrationVerificationRequest request) {
        return registrationService.verifyAndRegister(request);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(final RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of("error", exception.getMessage() == null ? "Invalid registration request" : exception.getMessage()));
    }
}
