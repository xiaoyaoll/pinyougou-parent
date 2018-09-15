package solrDemo;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * solr官方提供的API
 */

public class SolrJDemo {

    @Test
    /**
     * 简单搜索
     */
    public void test1() throws SolrServerException {
        //创建solr服务器实例
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8081/solr");
        //创建查询实例
        SolrQuery query = new SolrQuery("*:*");
        //执行查询,得到搜索结果
        QueryResponse response = solrServer.query(query);
        //处理搜索结果
        //1.获取文档信息
        SolrDocumentList documentList = response.getResults();
        for (SolrDocument document : documentList) {
            System.out.println(document.get("item_title"));
            System.out.println(document.get("item_price"));
        }
        //2.获取查询结果个数
        System.out.println(documentList.getNumFound());
    }

    @Test
    public void test2() throws SolrServerException {
        //创建solr服务器实例
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8081/solr");
        //创建查询实例
        // SolrQuery query=new SolrQuery("item_keywords:华为");//关键字搜索
        SolrQuery query = new SolrQuery();
        //设置查询条件
        //query.setFields("item_title");//设置搜索结果中显示的域
        query.setQuery("小米");//设置搜索信息
        query.set("df", "item_title");//设置搜索的域
        //query.set("fq","item_price:[1000 TO 2000]");//过滤搜索
        query.set("hl",true);

        //设置高亮显示
        //query.setHighlight(true);
        query.setHighlightSimplePost("</em>");
        //query.setHighlightSimplePre("<em style='color:red'>");
        query.set("hl.simple.pre","<em style='color:red'>");
        query.addHighlightField("item_title");//添加高亮域

        QueryResponse response = solrServer.query(query);
        SolrDocumentList documentList = response.getResults();
        for (SolrDocument document : documentList) {
            System.out.println(document);
        }

        //高亮结果遍历
        Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
        Set<String> keySet = highlighting.keySet();
        for (String key : keySet) {
            Map<String, List<String>> listMap = highlighting.get(key);
            Set<String> strings = listMap.keySet();
            for (String s : strings) {
                List<String> list = listMap.get(s);
                for (String s1 : list) {
                    System.out.println(s1);
                }
            }
        }
    }


    @Test
    /**
     * 简单搜索
     */
    public void test3() throws SolrServerException {
        //创建solr服务器实例
        SolrServer solrServer = new HttpSolrServer("http://192.168.25.130:8081/solr");
        //创建查询实例
        SolrQuery query = new SolrQuery();

        query.set("q","item_title:华为");

        QueryResponse response = solrServer.query(query);
        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results) {
            System.out.println(result);
        }

    }

}
