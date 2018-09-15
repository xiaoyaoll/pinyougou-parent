package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojogroup.Cart;
import entity.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private OrderService orderService;

    /**
     * 添加商品到购物车
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addToCart")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        try {
            //获取登录信息
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(username)) {//未登陆用户
                //1.从cookie中获取购物车
                System.out.println("添加cookie");
                String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
                if ("".equals(cartListString) || cartListString == null) {
                    cartListString = "[]";
                }
                List<Cart> cartList = JSON.parseArray(cartListString, Cart.class);
                //添加商品到购物车
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                cartListString = JSON.toJSONString(cartList);
                //保存购物车到cookie
                CookieUtil.setCookie(request, response, "cartList", cartListString, 60 * 60 * 24, "UTF-8");
            } else {//登录用户
                //1.从Redis中获取购物车
                System.out.println("添加redis");
                List<Cart> cartList_redis = cartService.getCartsFromRedis(username, null);
                if ("".equals(cartList_redis) || cartList_redis == null) {
                    cartList_redis = new ArrayList<>();
                }
                //添加商品到购物车
                cartList_redis = cartService.addGoodsToCartList(cartList_redis, itemId, num);
                //保存购物车到Redis
                cartService.addtoRedis(username, cartList_redis);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    @RequestMapping("/getCartList")
    public List getCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(username)) {

            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (cartListString == "" || cartListString == null) {
                cartListString = "[]";
            }
            List<Cart> cartList = JSON.parseArray(cartListString, Cart.class);
            return cartList;
        } else {
            //2.从cookie中获取购物车
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            List<Cart> cartList_redis = cartService.getCartsFromRedis(username, cartListString);
            if (cartListString != null && !"".equals(cartListString)) {
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, JSON.parseArray(cartListString, Cart.class));
                cartService.addtoRedis(username, cartList_redis);
                //清空cookie
                CookieUtil.deleteCookie(request, response, "cartList");
            }
            return cartList_redis;
        }
    }


    @RequestMapping("/genOrder")
    public Result add(@RequestBody TbOrder order) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userName);
            order.setSourceType("2");
            orderService.add(order);
            return new Result(true,"订单生成成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"订单生成失败");
        }
    }

}
