package com.itheima.oauth_server;

import com.itheima.oauth_server.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import javax.sql.DataSource;

@SpringBootTest
class OauthServerApplicationTests {

    @Autowired
    private DataSource dataSource;
    @Test
    void contextLoads() {

        System.out.println(dataSource);
    }

    @Autowired
    UserService userService;

    @Test
    public void test() {
        System.out.println(dataSource);
        UserDetails userDetails = userService.loadUserByUsername("chak");
        System.out.println(userDetails.getPassword()+userDetails.getUsername()+userDetails.getAuthorities());
        System.out.println(dataSource);
    }


}
