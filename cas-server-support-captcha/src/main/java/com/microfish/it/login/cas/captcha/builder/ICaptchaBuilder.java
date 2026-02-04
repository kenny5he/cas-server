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

package com.microfish.it.login.cas.captcha.builder;

import com.microfish.it.login.cas.captcha.Captcha;

/**
 * @author kenny.he
 * @since 2022/07/26
 */
public interface ICaptchaBuilder {
    /**
     * 获取验证码信息
     * @param failCount 验证码失败次数
     * @return 验证码信息
     */
    Captcha getCaptcha(Integer failCount) ;
}
