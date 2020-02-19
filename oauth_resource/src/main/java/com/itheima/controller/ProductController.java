package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @Author: Eric
 * @Date: 2020/2/5 15:35
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @RequestMapping("/findAll")
    //@PreAuthorize("hasRole(USER)")
    public String getAll() {
        return "product-list";
    }

    @Autowired
    private TokenStore tokenStore;

/*    //从访问令牌中提取额外的声明：
    public Map<String, Object> getExtraInfo(OAuth2Authentication auth) {
        OAuth2AuthenticationDetails details
                = (OAuth2AuthenticationDetails) auth.getDetails();
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(details.getTokenValue());
        return accessToken.getAdditionalInformation();
    }*/
}
