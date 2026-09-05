/**
 * Copyright 2026 - Ren Jian Yan Huo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfoolish.it.account.signup;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apereo.cas.acct.AccountRegistrationProperty;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Account-registration property with persistent selectable values.
 *
 * @author kenny
 * @since 7.3.0
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@SuperBuilder
@NoArgsConstructor
public class AccountSignupProperty extends AccountRegistrationProperty {
    @Serial
    private static final long serialVersionUID = 4914588762184718634L;

    @Builder.Default
    private List<AccountSignupPropertyValue> propertyValues = new ArrayList<>();
}
