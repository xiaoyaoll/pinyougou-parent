package solrDemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-solr.xml")
public class SolrDemo {

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    /**
     * 添加,springdata-solr是对solrj封装,操作的是Java对象,
     * 底层是将对象转换成document,添加 到solr库里面的.
     */
    public void test1(){
        List list=new ArrayList();
        for (int i = 0; i <100 ; i++) {
            TbItem item=new TbItem();
            item.setId(0L+i);
            item.setBrand("华为"+i);
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setSeller("华为 2 号专卖店");
            item.setTitle("华为 Mate9"+i);
            item.setPrice(new BigDecimal(2000+i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);//批量保存
        solrTemplate.commit();
    }

    @Test
    /**
     * 按主键删除,增删改必须要提交事务,才能生效
     */
    public void test2(){
        solrTemplate.deleteById("2");
        solrTemplate.commit();
    }

    @Test
    /**
     * 按照主键的简单查询
     */
    public void test3(){
        TbItem item = solrTemplate.getById(2L, TbItem.class);
        System.out.println(item.getTitle());
    }

    @Test
    /**
     * 分页查询
     */
    public void test4(){
        Query query=new SimpleQuery("*:*");
        query.setRows(6);//每页显示个数
        query.setOffset(10);//分页起始位置
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = page.getContent();
        System.out.println("总页数:"+page.getTotalPages());
        System.out.println("每页显示:"+page.getNumberOfElements());
        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }
    }

    @Test
    /**
     * 条件查找
     */
    public void test5(){
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_title").contains("2");
        query.addCriteria(criteria);
        query.setOffset(12);
        query.setRows(10);
        ScoredPage<TbItem> items = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> content = items.getContent();
        for (TbItem item : content) {
            System.out.println(item.getTitle());
        }
    }
    
    @Test
    /**
     * 按照查询结果批量删除
     */
    public void test6(){
        Query query=new SimpleQuery("*:*");
        //Criteria criteria=new Criteria("item_title").contains("3");
        //query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Test
    /**
     *高亮查询
     */
    public void test7(){
        //查询条件
        HighlightQuery query=new SimpleHighlightQuery();
        Criteria criteria=new Criteria("item_title").contains("2");
        query.addCriteria(criteria);

        //高亮选项设置---------HighlightOptions
        HighlightOptions highlightOptions=new HighlightOptions();
        highlightOptions.addField("item_title");//高亮字段
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮字段前显示
        highlightOptions.setSimplePostfix("</em>");//高亮字段后显示
        query.setHighlightOptions(highlightOptions);

        //获取结果集
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //对结果进行处理 snipplet:片段,通过可视化界面对这些API进行理解
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        for (HighlightEntry<TbItem> entry : highlighted) {
            List<HighlightEntry.Highlight> highlights = entry.getHighlights();
            for (HighlightEntry.Highlight highlight : highlights) {
                List<String> snipplets = highlight.getSnipplets();
                for (String snipplet : snipplets) {
                    System.out.println(snipplet);
                }
            }
        }
    }
}
