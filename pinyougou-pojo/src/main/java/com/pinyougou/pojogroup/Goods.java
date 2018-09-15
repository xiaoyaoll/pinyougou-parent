package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

//商品录入组合类:包括商品基本信息和商品介绍
public class Goods implements Serializable{

    private static final long serialVersionUID = 1186417220866775950L;
    private TbGoods tbGoods;
    private TbGoodsDesc tbGoodsDesc;

    private List<TbItem> itemList;

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }

    public Goods() {
    }

    public Goods(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        this.tbGoods = tbGoods;
        this.tbGoodsDesc = tbGoodsDesc;
    }

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getTbGoodsDesc() {
        return tbGoodsDesc;
    }

    public void setTbGoodsDesc(TbGoodsDesc tbGoodsDesc) {
        this.tbGoodsDesc = tbGoodsDesc;
    }
}
