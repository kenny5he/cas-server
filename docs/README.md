## CAS 统一单点登录平台
### 代办任务项
0. 提供验证码功能
1. 提供注册页面，开发Register Webflow功能。
2. 开发支持QQ、微信、支付宝对接登录服务。
3. 集成CAS的邮件、短信、双因子认证服务功能。
### CAS原理
1. 页面访问部分
   1. 默认进入登录页面
      1. CasWebAppConfiguration通过SpringMVC的SimpleUrlHandlerMapping进行根路径处理注册，并设定ModelAndView。
   2. Login 页面访问
      1. 通过自动配置类CasWebflowContextConfiguration的loginHandlerAdapter方法将标识为login的flowId注册为CasFlowHandlerAdapter实例
      2. CasFlowHandlerAdapter继承自Spring MVC的WebfLow处理适配器FlowHandlerAdapter，并覆写父类supports处理方法
      3. 请求在执行Spring MVC的DispatherServlet.doDispatch时，会通过方法DispatherServlet.getHandlerAdapter循环获取HandlerAdapter，
         此时注册的flowId与FlowHandler.getFlowId()的一致时即匹配成功。注: FlowHandler.getFlowId()是DefaultFlowUrlHandler.getFlowId通过路径解析获取而来
      4. 自动配置类CasWebflowContextConfiguration注册登录的CasFlowHandlerAdapter处理适配器时注册的执行器设定的登录步骤loginFlowRegistry()从配置文件/login/*-webflow.xml中读取。
      5. webflow/login/login-webflow.xml配置的expression执行步骤为initializeLoginAction
      6. 登录执行步骤信息一开始就由CasWebflowContextConfiguration的defaultWebflowConfigurer方法，将DefaultLoginWebflowConfigurer注册为执行计划。
      7. FlowDefinitionRegistry
2. Cas WebFlow 注册编排
    1. CasWebflowContextConfiguration.casWebflowExecutionPlan 中执行plan.execute 执行初始化。
    2. DefaultCasWebflowExecutionPlan.execute 循环CasWebflowConfigurer执行initialize(通过Java方法替代webflow.xml配置流程操作)，将流程动作、参数信息设置到对应的FlowDefinitionRegistry中
#### TGC/TGG 凭证原理
1. 生成凭证: CreateTicketGrantingTicketAction
2. CAS 验证TGC
   1. 验证接口: https://login.microfoolish.com/cas/p3/serviceValidate?ticket=ST-6-HsVNHLzUdDLrIElroeax7Qe22oc-apayedeMacBook-Pro&service=https%3A%2F%2Fmicroservice.microfoolish.com%3A1443%2Fframework%2Flogin%2Fcas
   2. 验证Controller: V3ServiceValidateController
   3. CAS 认证服务: DefaultCentralAuthenticationService
   4. 默认通过 Map存储Ticket: DefaultTicketRegistry

3. Cookie设置 
   1. 配置类: CasCookieConfiguration
   2. 实现类: CookieRetrievingCookieGenerator
   
4. 清理凭证: DefaultTicketRegistryCleaner

webflowEventResolutionConfigurationContext.getCentralAuthenticationService().createTicketGrantingTicket(authenticationResult);


参考文档: https://ningyu1.github.io/site/post/54-cas-server/