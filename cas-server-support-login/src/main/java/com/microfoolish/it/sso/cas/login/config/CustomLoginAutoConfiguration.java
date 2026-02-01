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

package com.microfoolish.it.sso.cas.login.config;

import com.microfoolish.it.sso.cas.login.pca4j.CustomPac4jAuthenticationProperties;
import com.microfoolish.it.sso.cas.login.pca4j.handler.support.CustomDelegatedClientFactory;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.authentication.CasSSLContext;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.support.pac4j.authentication.DefaultDelegatedClientFactory;
import org.apereo.cas.support.pac4j.authentication.DelegatedClientFactory;
import org.apereo.cas.support.pac4j.authentication.DelegatedClientFactoryCustomizer;
import org.apereo.cas.support.pac4j.authentication.RestfulDelegatedClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 客制化 文件配置读取
 *
 * @author kenny.he
 * @since 2022/07/29
 */
@Configuration
@EnableConfigurationProperties({CustomPac4jAuthenticationProperties.class})
@PropertySource(value = {"classpath:/configs/login.pac4j.properties","classpath:/configs/login.jdbc.properties"})
public class CustomLoginAutoConfiguration  {
    private static final Logger logger = LoggerFactory.getLogger(CustomLoginAutoConfiguration.class);

    /**
     * 客制化 构建 Client 配置
     */
    @Configuration(value = "CustomPac4jAuthenticationEventExecutionPlanClientFactoryConfiguration", proxyBeanMethods = false)
    @EnableConfigurationProperties({CasConfigurationProperties.class,CustomPac4jAuthenticationProperties.class})
    public static class CustomPac4jAuthenticationEventExecutionPlanClientFactoryConfiguration {
        @Bean
        @RefreshScope(proxyMode = ScopedProxyMode.DEFAULT)
        public DelegatedClientFactory pac4jDelegatedClientFactory(
                CasConfigurationProperties casProperties,
                CustomPac4jAuthenticationProperties pac4j,
                ConfigurableApplicationContext applicationContext,
                ObjectProvider<List<DelegatedClientFactoryCustomizer>> customizerList,
                @Qualifier(CasSSLContext.BEAN_NAME)
                CasSSLContext casSslContext) {
            val rest = casProperties.getAuthn().getPac4j().getRest();
            if (StringUtils.isNotBlank(rest.getUrl())) {
                return new RestfulDelegatedClientFactory(casProperties);
            }
            val customizers = Optional.ofNullable(customizerList.getIfAvailable())
                    .map(result -> {
                        AnnotationAwareOrderComparator.sortIfNecessary(result);
                        return result;
                    }).orElseGet(() -> new ArrayList<>(0));
            return new CustomDelegatedClientFactory(casProperties,pac4j, customizers, casSslContext, applicationContext);
        }
    }

}
