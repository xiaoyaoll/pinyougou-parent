package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController  {

    @RequestMapping("/getName")
    public Map getName(){
        //获取security上下文
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map=new HashMap();
        map.put("username",name);
        return map;
    }

}
