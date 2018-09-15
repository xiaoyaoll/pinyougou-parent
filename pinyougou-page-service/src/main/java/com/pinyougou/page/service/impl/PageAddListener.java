package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class PageAddListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            itemPageService.genItemPage(Long.parseLong(textMessage.getText()));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

   /* @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            itemPageService.genItemPage(Long.parseLong(textMessage.getText()));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }*/
}
