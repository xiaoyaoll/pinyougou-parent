package com.springboot.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot启动类
 * 1.启动springboot内置的Tomcat服务
 * 2.自动扫描启动类所在包下的所有spring注解
 * 3.定义启动类为一个配置类
 */
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }
}
