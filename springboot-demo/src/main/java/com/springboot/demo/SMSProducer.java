package com.springboot.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SMSProducer {

    @Autowired
    private JmsMessagingTemplate template;

    @RequestMapping("/helloworld")
    public void sendsms(String message){
        template.convertAndSend("Hello",message);
    }

    @RequestMapping("/hellomap")
    public void sendmap(){
        Map<String,String> map=new HashMap<String, String>();
        map.put("name","zs");
        map.put("sex","man");
        template.convertAndSend("Hellomap",map);//参数一:消息的名称 参数二:消息
    }




}
