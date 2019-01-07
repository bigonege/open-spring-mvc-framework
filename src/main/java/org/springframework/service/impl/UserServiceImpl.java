package org.springframework.service.impl;

import org.springframework.service.UserService;

/**
 * @Auther: Wang Ky
 * @Date: 2018/12/29 10:55
 * @Description:
 */
public class UserServiceImpl implements UserService {
    public String getUser(String name, Integer age) {
        return "name = "+name+"  "+"age = " +age;
    }
}
