package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Value("${me.url}")
    private String dirurl;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean genItemPage(Long goodsId) {
        try {
            Map map = new HashMap();
            //获取商品基本信息
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            //获取商品详细信息
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //获取配置对象,加载模板,获取模板对象
            Configuration configuration = freeMarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            //加载SKU列表信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//状态为有效
            criteria.andGoodsIdEqualTo(goodsId);//指定 SPU ID
            example.setOrderByClause("is_default desc");//按照状态降序，保证第一个为默
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //查询分类,生成面包屑
            TbItemCat itemCat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat itemCat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat itemCat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            //获取数据模型,创建输出流对象
            map.put("goods", tbGoods);
            map.put("goodsDesc", goodsDesc);
            map.put("itemCat1", itemCat1.getName());
            map.put("itemCat2", itemCat2.getName());
            map.put("itemCat3", itemCat3.getName());
            map.put("itemList", itemList);
            Writer writer = new FileWriter(dirurl + goodsId + ".html");
            //输出静态页面
            template.process(map, writer);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
