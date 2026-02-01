/**
 * Copyright 2022 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfoolish.it.sso.cas.captcha.validator;

import org.apereo.cas.configuration.model.support.captcha.GoogleRecaptchaProperties;
import org.apereo.cas.web.BaseCaptchaValidator;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义验证码
 *
 * @author kenny.he
 * @since 2022/07/26
 */
public class CustomCaptchaValidator extends BaseCaptchaValidator {

    public CustomCaptchaValidator(GoogleRecaptchaProperties recaptchaProperties) {
        super(recaptchaProperties);
    }

    @Override
    public boolean validate(String recaptchaResponse, String userAgent) {

        return false;
    }

    @Override
    public String getRecaptchaResponse(HttpServletRequest request) {
        return null;
    }

    @Override
    public GoogleRecaptchaProperties getRecaptchaProperties() {
        return null;
    }
}
