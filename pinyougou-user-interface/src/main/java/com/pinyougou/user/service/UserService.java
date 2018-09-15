package com.pinyougou.user.service;
import java.util.List;
import com.pinyougou.pojo.TbUser;

import entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface UserService {

    public boolean addUser(TbUser user,String code);

    public void sendCode(String phone);



}
