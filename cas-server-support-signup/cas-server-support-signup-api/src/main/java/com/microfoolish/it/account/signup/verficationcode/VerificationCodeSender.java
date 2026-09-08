/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package com.microfoolish.it.account.signup.verficationcode;

import com.microfoolish.it.account.signup.registration.RegistrationType;

/**
 *
 * @author kenny.he
 * @since 2026/09/06
 */
public interface VerificationCodeSender {
    RegistrationType type();

    void send(String target, String code) throws Exception;
}
