package com.springjms.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class Producer {

    @Autowired
    //注入消息的发送者(同时也是接收者),相当于MessageProducer
    private JmsTemplate jmsTemplate;

    @Autowired
    //注入消息的目的地(接口:-->实现类1.点对点:ActiveMQQueue 2.发布订阅:ActiveMQTopic)
    private Destination queueTextDestination;


    public void smsSender(final String message){
        jmsTemplate.send(queueTextDestination, new MessageCreator() {
            //接口:MessageCreator:消息生产者,用于创建消息
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);//正文的消息格式,字符串类型
            }
        });
    }
}
