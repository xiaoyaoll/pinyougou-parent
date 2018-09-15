package com.pinyougou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Override
    public boolean addUser(TbUser user,String code) {
        String phoneCode = (String) redisTemplate.boundHashOps("phoneCode").get(user.getPhone());
        if(!code.equals(phoneCode)){
            return false;
        }

        user.setCreated(new Date());//创建日期
        user.setUpdated(new Date());//修改日期
        //密码加密:MD5
        System.out.println(user);
        System.out.println(user.getPassword());
        String password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(password);

        userMapper.insert(user);
        return true;
    }

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination sendSMSQueueDestination;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void sendCode(final String phone) {
        final String code=(long)(Math.random()*1000000)+"";//强转成转成long类型,以消除小数
        //发送到消息中间件:activeMQ
        jmsTemplate.send(sendSMSQueueDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                Map<String,String> map=new HashMap<>();
                map.put(phone,code);
                return session.createTextMessage(JSONObject.toJSONString(map));
            }
        });
        //保存到Redis
        redisTemplate.boundHashOps("phoneCode").put(phone,code);
    }
}
