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

package com.microfish.it.login.pac4j.platform.workwechat.scribe.model;

import com.github.scribejava.core.model.Token;

/**
 * Work WeChat Confirm Token
 * @author kenny.he
 * @since 2022/08/10
 */
public class WorkWechatConfirmCode extends Token {

    private String code;

    public WorkWechatConfirmCode(String token) {
        super(null);
        this.code = token;
    }

    public String getCode() {
        return code;
    }
}
