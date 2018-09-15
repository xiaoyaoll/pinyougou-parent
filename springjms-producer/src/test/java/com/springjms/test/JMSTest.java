package com.springjms.test;

import com.springjms.demo.Producer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-jms-producer.xml")
public class JMSTest {

    @Autowired
    private Producer producer;

    @Test
    public void test(){
        producer.smsSender("hello activeMQ");
    }

}
