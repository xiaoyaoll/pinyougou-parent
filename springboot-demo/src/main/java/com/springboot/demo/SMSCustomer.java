package com.springboot.demo;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SMSCustomer {

    @JmsListener(destination = "Hello")
    public void readMessage(String text){
        System.out.println("接收到消息："+text);
    }

    @JmsListener(destination = "Hellomap")
    public void getMessage(Map<String,String> text){
        System.out.println(text);
    }
}
