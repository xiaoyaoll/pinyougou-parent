package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加,录入商品
     */
    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private TbBrandMapper tbBrandMapper;
    @Autowired
    private TbItemCatMapper tbItemCatMapper;
    @Autowired
    private TbSellerMapper tbSellerMapper;

    @Override
    public void add(Goods goods) {
        //添加商品基本信息
        goods.getTbGoods().setAuditStatus("0");//未审核
        goodsMapper.insert(goods.getTbGoods());
        //添加商品介绍信息
        goods.getTbGoodsDesc().setGoodsId(goods.getTbGoods().getId());//给主键赋值
        tbGoodsDescMapper.insert(goods.getTbGoodsDesc());
        //添加商品SKU信息
        if (goods.getItemList() != null) {

            for (TbItem item : goods.getItemList()) {
//标题
                String title = goods.getTbGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);
                item.setGoodsId(goods.getTbGoods().getId());//商品 SPU 编号
                item.setSellerId(goods.getTbGoods().getSellerId());//商家编号
                item.setCategoryid(goods.getTbGoods().getCategory3Id());//商品分类编号（3 级）
                item.setCreateTime(new Date());//创建日期
                item.setUpdateTime(new Date());//修改日期
//品牌名称
                TbBrand brand =
                        tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
                item.setBrand(brand.getName());
//分类名称
                TbItemCat itemCat =
                        tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
                item.setCategory(itemCat.getName());
//商家名称
                TbSeller seller =
                        tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
                item.setSeller(seller.getNickName());
//图片地址（取 spu 的第一个图片）
                List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),
                        Map.class);
                if (imageList.size() > 0) {
                    item.setImage((String) imageList.get(0).get("url"));
                }
                tbItemMapper.insert(item);
            }
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goods.getTbGoods().setAuditStatus("0");//未审核
        //更新基本信息
        goodsMapper.updateByPrimaryKey(goods.getTbGoods());
        //更新详细信息
        tbGoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());
        //更新SKU信息
        List<TbItem> itemList = goods.getItemList();
        for (TbItem tbItem : itemList) {
            tbItemMapper.deleteByPrimaryKey(tbItem.getId());
            tbItemMapper.insert(tbItem);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        //查询商品基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setTbGoods(tbGoods);
        //查询商品详细信息
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
        goods.setTbGoodsDesc(tbGoodsDesc);
        //查询商品SKU信息
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(tbGoods.getId());
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除,同时更新索引库，删除静态页面
     */
    @Autowired
    private Destination deleQueueSolrDestination;
    @Autowired
    private Destination deleTopicPageDestination;

    @Override
    public void delete(Long[] ids) {
        for (final Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");

            jmsTemplate.send(deleQueueSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

            //删除静态页面

            jmsTemplate.send(deleTopicPageDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;

    @Override
    //参数ID为模板ID
    public List<Map> findSpecificationItems(Long id) {
        //查询模板表得出规格选项
        TbTypeTemplate typeTemplate = tbTypeTemplateMapper.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            //map中有规格选项外键,查询规格选项表
            Long specId = new Long(map.get("id") + "");
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            example.createCriteria().andSpecIdEqualTo(specId);
            List<TbSpecificationOption> options = tbSpecificationOptionMapper.selectByExample(example);
            map.put("options", options);
        }
        return list;
    }

    //更新商品状态，同时更新索引库，同时生成静态页面
    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination addQueueSolrDestination;

    @Autowired
    private Destination addTopicPageDestination;

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (final Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);

            //更新索引库
            //根据goodsid查询itemList
            if ("1".equals(status)) {
                TbItemExample example = new TbItemExample();
                example.createCriteria().andGoodsIdEqualTo(id);
                final List<TbItem> list = itemMapper.selectByExample(example);
                //发送消息
                if (list.size() > 0) {
                    jmsTemplate.send(addQueueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(JSONArray.toJSONString(list));
                        }
                    });
                }

                //生成静态页面,使用的是发布订阅的消息传递方式
                //itemPageService.genItemPage(id);
                jmsTemplate.send(addTopicPageDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });
            }

            //更新商品状态
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

}
