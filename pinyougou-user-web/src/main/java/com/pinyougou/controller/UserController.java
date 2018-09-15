package com.pinyougou.controller;

import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;


	@RequestMapping("/register")
	public Result register(@RequestBody TbUser user,String code){
		if(code==null||code==""){
			return  new Result(false,"没有填写验证码");
		}

		try {
			boolean b = userService.addUser(user, code);
			if(!b){
				return new Result(false,"验证码错误");
			}
			return new Result(true,"注册成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"错误");
		}

	}

	/**
	 * 发送验证码到activemq:包括:电话号码和验证码
	 * 同时将电话号码和验证码存入Redis缓存
	 * @param phone
	 * @return
	 */


	@RequestMapping("/sendCode")
	public Result sendCode(String phone){
		try {

			/**
			 * TODO验证码的正则表达式校验
			 */
			userService.sendCode(phone);
			return new Result(true,"验证码发送成功");
		}catch (Exception e){
			e.printStackTrace();
			return new Result(false,"验证码发送失败");
		}
	}
	

	
}
