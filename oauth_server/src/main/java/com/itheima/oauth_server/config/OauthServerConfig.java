package com.itheima.oauth_server.config;

import com.itheima.oauth_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;


/**
 * @Author: Eric
 * @Date: 2020/2/10 13:59
 */

@Configuration
@EnableAuthorizationServer
public class OauthServerConfig extends AuthorizationServerConfigurerAdapter {

    private DataSource dataSource;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public OauthServerConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //从数据库中查询出客户端信息
    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    //token保存策略
    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    //授权信息保存策略
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }

    //授权码模式专用对象
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }

    //指定客户端登录信息来源
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
       //  clients.withClientDetails(clientDetailsService());
        // 从内存中取数据
        clients.inMemory()
                .withClient("baidu")
                .secret(passwordEncoder.encode("123"))
                .resourceIds("product_api")
                .authorizedGrantTypes(
                        "authorization_code",
                        "password",
                        "client_credentials",
                        "implicit",
                        "refresh_token"
                )// 该client允许的授权类型 authorization_code,password,refresh_token,implicit,client_credentials
                .scopes("read", "write")// 允许的授权范围
                .autoApprove(false)
                //加上验证回调地址
                .redirectUris("http://www.baidu.com")
                .accessTokenValiditySeconds(1200)
                .refreshTokenValiditySeconds(50000);

    }

    //检测token的策略
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients()
                // 开启/oauth/token_key验证端口无权限访问
                .tokenKeyAccess("permitAll()")
                // 开启/oauth/check_token验证端口认证权限访问
                .checkTokenAccess("isAuthenticated()");


    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .approvalStore(approvalStore())
                .authenticationManager(authenticationManager)
                .authorizationCodeServices(authorizationCodeServices())
                .tokenStore(tokenStore())
                .userDetailsService(userService);
    }


}
