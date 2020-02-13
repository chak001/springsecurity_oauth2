package com.itheima.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
