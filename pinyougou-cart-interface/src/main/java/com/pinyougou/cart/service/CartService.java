package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {
    //添加商品到购物车
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList_redis, List<Cart> cartList_cookie) ;

    //从Redis获取购物车
    public List<Cart> getCartsFromRedis(String username,String cartList);

    //将购物车存入Redis
    public void addtoRedis(String username,List<Cart> cartList );

}
