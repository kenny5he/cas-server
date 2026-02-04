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

package com.microfish.it.login.cas.captcha;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author kenny.he
 * @since 2022/07/28
 */
@Getter
@Setter
public class Captcha {
    private BufferedImage image;

    private Type type;

    private String answer;

    private String question;

     public static enum Type {
         MATH(1,"MATH"),
         SLIDE(2,3,"SLIDE"),
         CLICK(2,3,"CLICK"),
         GOOGLE(3,5,"GOOGLE");

        private Integer start = 0;
         private Integer end;

        private String name;



        Type (Integer end, String name) {
             this.end = end;
            this.name = name;
        }

        Type (Integer start, Integer end, String name) {
             this.start = start;
             this.end = end;
             this.name = name;
         }
    }
}
