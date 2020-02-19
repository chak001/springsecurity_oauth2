package com.itheima.utils;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Eric
 * @Date: 2020/2/5 18:19
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}
