package com.pinyougou.search.service.impl;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        if("".equals(searchMap.get("keywords"))||searchMap.get("keywords")==null){
            return searchMap;
        }
        //处理关键字之间的空格
        searchMap.put("keywords",(searchMap.get("keywords")+"").replaceAll(" ",""));
        Map resultMap = new HashMap();
        //1.关键字高亮查询
        Map highLightMap = keyeordAndHighLightSearch(searchMap);
        resultMap.putAll(highLightMap);
        //2.分类查询
        Map categoryMap = categorySearch(searchMap);
        resultMap.putAll(categoryMap);
        //3.品牌列表和规格选项查询
        String category = (String) searchMap.get("category");
        if (!"".equals(category) && category != null) {//有分类选项按分类查询,没有按分类列表第一个分类查询
            Map map = brandSearch(category);
            resultMap.putAll(map);
        } else {
            List list = (List) categoryMap.get("categoryList");
            if (list.size() > 0) {
                Map map = brandSearch((String) list.get(0));
                resultMap.putAll(map);
            }
        }
        return resultMap;
    }

    private Map keyeordAndHighLightSearch(Map searchMap) {
        Map resultMap = new HashMap();
        //{"keywords":"手机","category":"手机","brand":"诺基亚","spec":{"网络":"移动3G","机身内存":"16G"}}
        //1.按照关键域进行查询 条件查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //设置高亮选项
        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        //添加查询条件
        HighlightQuery query = new SimpleHighlightQuery();
        query.addCriteria(criteria);

        query.setHighlightOptions(options);

        //2.过滤查询--分类
        String category = (String) searchMap.get("category");
        if (!"".equals(category) && category != null) {
            Criteria filterCriteria = new Criteria("item_category").is(category);
            query.addFilterQuery(new SimpleFilterQuery(filterCriteria));
        }
        //3.过滤查询--品牌
        String brand = (String) searchMap.get("brand");
        if (!"".equals(brand) && brand != null) {
            Criteria filterCriteria = new Criteria("item_brand").is(brand);
            query.addFilterQuery(new SimpleFilterQuery(filterCriteria));
        }
        //4.过滤查询--规格
        Map spec = (Map) searchMap.get("spec");
        System.out.println(spec);
        if (!"{}".equals(spec) && spec != null) {
            Set keys = spec.keySet();
            for (Object key : keys) {
                Criteria filterCriteria = new Criteria("item_spec_" + key).is(spec.get(key));
                query.addFilterQuery(new SimpleFilterQuery(filterCriteria));
            }
        }
        //5.过滤价格区间选项
        String priceStr = (String) searchMap.get("price");//price:'0-500'
        if (priceStr != null && !priceStr.equals("")) {
            String[] price = priceStr.split("-");
            System.out.println(price.length);
            FilterQuery filterQuery = new SimpleFilterQuery();
            if ("0".equals(price[0])) {
                filterQuery.addCriteria(new Criteria("item_price").lessThanEqual(price[1]));
            } else if ("*".equals(price[1])) {
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(price[0]));
            } else {
                filterQuery.addCriteria(new Criteria("item_price").lessThanEqual(price[1]));
                filterQuery.addCriteria(new Criteria("item_price").greaterThanEqual(price[0]));
            }
            query.addFilterQuery(filterQuery);
        }
        //6.搜索结果分页,前台传递数据:1.当前页currentPage 2.每页显示pageSize
        String currentPage = (String) (searchMap.get("currentPage")+"");
        String pageSize = (String) (searchMap.get("pageSize")+"");
        //判断传入参数是否存在可以,提高健壮性
        if(currentPage==null||"".equals(currentPage)){
            currentPage="1";
        }
        if(pageSize==null||"".equals(pageSize)){
            pageSize="20";
        }
        query.setOffset((Integer.parseInt(currentPage)-1)*Integer.parseInt(pageSize));
        query.setRows(Integer.parseInt(pageSize));
        //7.按照价格进行排序,前台传递数据:1.priceSort:DESC/ASC
        if(searchMap.get("sortField")!=null&&!"".equals(searchMap.get("sortField"))){
            if(searchMap.get("sort").equals("DESC")){
                Sort sort=new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }else if(searchMap.get("sort").equals("ASC")){
                Sort sort=new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }
        }




        //查询结果,分页显示,返回数据:1.总页数 2.总记录数
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        int totalPages = tbItems.getTotalPages();
        long totalElements = tbItems.getTotalElements();
        List<HighlightEntry<TbItem>> entryList = tbItems.getHighlighted();
        List<TbItem> list = new ArrayList<>();
        for (HighlightEntry<TbItem> entry : entryList) {
            TbItem entity = entry.getEntity();
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                entity.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
            list.add(entity);
        }
        resultMap.put("rows", list);
        resultMap.put("totalPages", totalPages);//1.总页数
        resultMap.put("totalElements", totalElements);//2.总记录数

        return resultMap;
    }

    //查询分类列表,使用分组查询对所有查询结果的分类字段进行分组统计
    private Map categorySearch(Map searchMap) {
        Map resultMap = new HashMap();
        List list = new ArrayList();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery();
        query.addCriteria(criteria);

        //分组条件设置
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);

        GroupResult<TbItem> itemCategory = page.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> entries = itemCategory.getGroupEntries();
        List<GroupEntry<TbItem>> content = entries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }
        resultMap.put("categoryList", list);
        return resultMap;
    }

    //从缓存中获取品牌和规格列表
    @Autowired
    private RedisTemplate redisTemplate;

    private Map brandSearch(String itemCatName) {
        Map resultMap = new HashMap();
        //获取关键字对应的分类---对应的模板ID
        Long typeId = (Long) redisTemplate.boundHashOps("itemCatList").get(itemCatName);
        System.out.println(typeId);
        //获取模板ID对应的品牌列表
        if (typeId != null) {
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
            resultMap.put("brandList", brandList);
            resultMap.put("specList", specList);
        }
        return resultMap;
    }
}
