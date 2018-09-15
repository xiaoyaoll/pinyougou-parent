package com.freemarker.demo;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Demo {

    public static void main(String[] args) throws IOException, TemplateException {
        //创建配置类,参数为版本号
        Configuration configuration=new Configuration(Configuration.getVersion());
        //创建模板所在目录的对象
        configuration.setDirectoryForTemplateLoading(new File("D:\\IDEAWorkSpace\\pinyougou-parent\\freemarker\\src\\main\\resources"));
        //设置字符集
        configuration.setDefaultEncoding("UTF-8");
        //加载模板
        Template template=configuration.getTemplate("demo.ftl");
        //创建数据模型
        Map map=new HashMap();
        map.put("name","freemarker");
        map.put("message","卧槽,太难了");
        map.put("success",true);

        List goodsList=new ArrayList();
        Map goods1=new HashMap();
        goods1.put("name", "苹果");
        goods1.put("price", 5.8);
        Map goods2=new HashMap();
        goods2.put("name", "香蕉");
        goods2.put("price", 2.5);
        Map goods3=new HashMap();
        goods3.put("name", "橘子");
        goods3.put("price", 3.2);
        goodsList.add(goods1);
        goodsList.add(goods2);
        goodsList.add(goods3);
        map.put("goodsList", goodsList);
        String str="{\"id\":\"12\",\"name\":\"张三\"}";
        map.put("obj",str);
        map.put("dateTime",new Date());
        map.put("num",156525);
        //创建输出流对象
        Writer writer=new FileWriter("f:\\freemarker.html");
        //输出模板
        template.process(map,writer);
        //关闭流对象
        writer.close();
    }

}
