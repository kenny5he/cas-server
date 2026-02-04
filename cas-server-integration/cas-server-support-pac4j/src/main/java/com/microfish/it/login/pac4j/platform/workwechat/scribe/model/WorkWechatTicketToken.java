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
 * 用户 Ticket
 *
 * @author kenny.he
 * @since 2022/08/10
 */
public class WorkWechatTicketToken extends Token {

    private String ticket;

    private String userId;

    private String deviceId;

    public WorkWechatTicketToken(String ticket,String userId, String deviceId) {
        super(null);
        this.ticket = ticket;
        this.userId = userId;
        this.deviceId = deviceId;
    }

    public String getTicket() {
        return ticket;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
