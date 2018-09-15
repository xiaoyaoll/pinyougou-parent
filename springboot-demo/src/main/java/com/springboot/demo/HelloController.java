package com.springboot.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    //获取属性文件自定义的属性
    @Autowired
    private Environment env;

    @RequestMapping("/hello")
    public String sayHello(){
        return env.getProperty("url")+"HELLO WORLD";
    }
}
