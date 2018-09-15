package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@SuppressWarnings("ALL")
public class CartServiceImpl implements CartService {

    /**
     * 将商品添加到购物车
     *
     * @param cartList 购物车列表
     * @param itemId 添加商品ID
     * @param num 添加商品数量
     * @return
     */
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //1.判断购物车列表中有无购物车
        Cart cart = getCarts(cartList, tbItem);
        //2.判断是否存在该商品所属商家的购物车
        if (cart == null) {
            //2.1没有新建一个购物车
            cart = new Cart();
            cart.setSellerId(tbItem.getSellerId());
            cart.setSellerName(tbItem.getSeller());
            cart.setOrderItemList(new ArrayList<TbOrderItem>());
            //5.不存在则直接添加到购物车
            addTbItemToCart(num, tbItem, cart);
            cartList.add(cart);
        } else {
            //3.商家购物车存在,获取购物车商品列表
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem tbOrderItem = getTbOrderItem(itemId, orderItemList);
            //4.判断购物车中是否存在该商品
            if (tbOrderItem != null) {
                //4.1.存在就更新商品数量
                tbOrderItem.setNum(tbOrderItem.getNum() + num);
                if (tbOrderItem.getNum() <= 0) {
                    orderItemList.remove(tbOrderItem);
                    if (orderItemList.size() <= 0) {
                        cartList.remove(cart);
                    }
                } else {
                    //4.2更新总价
                    tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum() * tbOrderItem.getPrice().doubleValue()));
                }
            } else {
                //5.不存在该商品,直接添加
                addTbItemToCart(num, tbItem, cart);
            }
        }
        return cartList;
    }

    @Override
    /**
     * 合并购物车
     * @param cartList_redis
     * @param cartList_cookie
     */
    public List<Cart> mergeCartList(List<Cart> cartList_redis, List<Cart> cartList_cookie) {
        for (Cart cart : cartList_cookie) {
            for (TbOrderItem item : cart.getOrderItemList()) {
                System.out.println(item.getNum());
                cartList_redis = addGoodsToCartList(cartList_redis, item.getItemId(), item.getNum());
            }
        }
        return cartList_redis;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从Redis中获取购物车
     *
     * @param username
     * @param cartListString
     * @return
     */
    @Override
    public List<Cart> getCartsFromRedis(String username, String cartListString) {
        //1.从Redis中获取购物车
        List<Cart> cartList_redis = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if ("".equals(cartList_redis) || cartList_redis == null) {
            cartList_redis = new ArrayList<>();
        }
        if (cartListString != null && !"".equals(cartListString)) {
            List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
            //合并购物车
            cartList_redis = mergeCartList(cartList_redis, cartList_cookie);
            redisTemplate.boundHashOps("cartList").put(username, cartList_redis);
            }
        cartList_redis = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);

        return cartList_redis;
    }

    /**
     * 将购物车添加到Redis
     *
     * @param username
     * @param cartList
     */
    @Override
    public void addtoRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    /**
     * 直接添加添加商品到商家购物车
     *
     * @param num
     * @param tbItem
     * @param cart
     */
    private void addTbItemToCart(Integer num, TbItem tbItem, Cart cart) {
        TbOrderItem tbOrderItem;
        tbOrderItem = new TbOrderItem();
        if (num <= 0) {
            throw new RuntimeException();
        }
        tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue() * num));
        tbOrderItem.setNum(num);
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        cart.getOrderItemList().add(tbOrderItem);
    }

    /**
     * 判断购物车中是否存在该商品
     *
     * @param itemId
     * @param orderItemList
     */
    private TbOrderItem getTbOrderItem(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if (tbOrderItem.getItemId().longValue() == itemId) {
                return tbOrderItem;
            }
        }
        return null;
    }
    /**
     * 判斷商家购物车是否存在
     *
     * @param cartList
     * @param tbItem
     * @return
     */
    private Cart getCarts(List<Cart> cartList, TbItem tbItem) {
        for (Cart cart : cartList) {
            if (tbItem.getSellerId().equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }
}