package com.pinyougou.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    //注入服务层
    @Reference
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //创建授权认证角色的集合
        List<GrantedAuthority> authorities=new ArrayList<>();
        //添加授权角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //创建该角色类型用户 参数1:用户名,参数2:密码,参数3:角色集合

        TbSeller oneUser = sellerService.findOne(username);
        if(oneUser!=null){
            if("1".equals(oneUser.getStatus())){
                return new User(username,oneUser.getPassword(),authorities);
            }
        }

        return null;
    }
}
