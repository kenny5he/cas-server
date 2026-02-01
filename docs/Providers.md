# 外部集成 Providers
## PAC4j OAuth2(QQ/WeChat)/GitHub/SAML2/CAS
1. 配置文件: Pac4jDelegatedAuthenticationProperties
2. PAC4j 配置读取: (Pac4jAuthenticationEventExecutionPlanConfiguration)
3. 跳转路径配置读取/构造: OAuth20RedirectionActionBuilder
4. 验证 code 调用读取用户信息 (DelegatedClientAuthenticationAction)
   1. 判断回调路径Client Name，获取Token校验生成 (populateContextWithClientCredential)
   2. 通过 client.getCredentials 获取 Credentials
   3. 通过 OAuth20CredentialsExtractor#getOAuthCredentials 提取 Code 
   4. DelegatedClientAuthenticationHandler 的 doAuthentication 方法通过client.getUserProfile 获取用户信息
   5. GenericOAuth20ProfileDefinition 的extractUserProfile 方法提取用户属性字段 

## PAC4j OAuth2 (Extension Work WeChat) 
1. 方案
   1. 配置类Pac4jAuthenticationEventExecutionPlanConfiguration 自定义 DelegatedClientFactory 方便读取 WorkWeChatClient，
   2. 当然也可以自定义设置 DelegatedClientFactoryCustomizer，对 Client 进行克制化设置。
2. 企业微信登录实战步骤
   1. 