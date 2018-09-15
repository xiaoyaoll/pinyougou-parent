package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;

@Component
public class PageDeleListener implements MessageListener{

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage=(TextMessage)message;
        try {
            File file=new File("F:\\freemarker\\"+Long.parseLong(textMessage.getText())+".html");
            file.delete();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
