package com.itheima.config;

import com.itheima.domain.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @Author: Eric
 * @Date: 2020/2/18 13:24
 */
public class CustomerAccessTokenConverter extends DefaultAccessTokenConverter {

    //自定义token信息中添加的信息
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put("user_name",authentication.getName());
        response.put("name",((SysUser)authentication.getPrincipal()).getUsername());
        response.put("id",((SysUser)authentication.getPrincipal()).getId());
        response.put("status",((SysUser)authentication.getPrincipal()).getStatus());
        if (authentication.getAuthorities() !=null && !authentication.getAuthorities().isEmpty()) {
            response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }
}
