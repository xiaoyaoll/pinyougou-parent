package com.xiaoyao.casSecurity.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserDemo {

   @RequestMapping("/name")
    public void name(){
       String name = SecurityContextHolder.getContext().getAuthentication().getName();
       System.out.println(name);
   }
}
