package com.springjms.demo;

import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
/**
 * 消息监听类,需要实现MessageListener接口
 */
public class Listener implements MessageListener{

    @Override
    //监听
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;//监听获取到的数据
        try {
            System.out.println("收到消息"+textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
