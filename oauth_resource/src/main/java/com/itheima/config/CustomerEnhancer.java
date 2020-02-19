package com.itheima.config;

import com.itheima.domain.SysUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/*
import java.util.HashMap;
import java.util.Map;


*/
/**
 * 自定义一个类实现TokenEnhancer 重写enhancer 方法来实现添加额外的信息;
 *
 * @Author: Eric
 * @Date: 2020/2/18 11:10
 *//*


public class CustomerEnhancer implements TokenEnhancer {

    //自定义生成token携带的信息
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        HashMap<String, Object> additionalInfo = new HashMap<>();
        SysUser sysUser = (SysUser) authentication.getPrincipal();
        additionalInfo.put("username", sysUser.getUsername());
        additionalInfo.put("authorities", sysUser.getAuthorities());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}

*/
