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

package com.microfish.it.login.cas.captcha.controller;

import javax.imageio.ImageIO;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.microfish.it.login.cas.captcha.Captcha;
import com.microfish.it.login.cas.captcha.CaptchaConstant;
import com.microfish.it.login.cas.captcha.builder.ICaptchaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录验证码
 *
 * @author kenny.he
 * @since 2022/07/26
 */
@RestController
public class CaptchaController {

    @Autowired
    private ICaptchaBuilder captchaBuilder;

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Captcha captcha = captchaBuilder.getCaptcha(0);
        session.removeAttribute(CaptchaConstant.CAPTCHA_SESSION_KEY);
        session.setAttribute(CaptchaConstant.CAPTCHA_SESSION_KEY, captcha.getAnswer());
        if (captcha.getType().equals(Captcha.Type.MATH) || captcha.getType().equals(Captcha.Type.CLICK)
                || captcha.getType().equals(Captcha.Type.SLIDE)) {
            // 将内存中的图片通过流动形式输出到客户端
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            ImageIO.write(captcha.getImage(), "JPEG", response.getOutputStream());
        }
    }
}
