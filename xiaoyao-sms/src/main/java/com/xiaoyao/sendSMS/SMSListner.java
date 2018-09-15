package com.xiaoyao.sendSMS;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SMSListner {

    @JmsListener(destination = "XiaoYaosms")
    public void readMessage(String text){
        System.out.println(text);
    }
}
