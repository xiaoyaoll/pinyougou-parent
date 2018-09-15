<html>

    <head>
        <meta charset="UTF-8">
        <title>freemarker入门小Deno</title>
    </head>

    <body>
        <#--freemarker的注释写法-->
        <#--插值-->
        ${name},你好!${message}<br>
**************************************************************************************************************
        <#--freemarker的指令:定义变量-->
        <#assign password='123'>
        <#assign pojo={"id":"5525","brand":"华为"}>
        ${password}<br>
        ${pojo.id}${pojo.brand}<br>
        <#--freemarker的指令:模板文件的嵌套-->
        <#include "head.ftl">
        <#--freemarker的指令:判断-->
        <#if success==true>
            判断为true
            <#else>
            判断为false
        </#if>
        <br>
        <#--freemarker的指令:集合-->
        *********商品列表*********<br>
        <#list goodsList as goods>
            ${goods_index+1} 商品名:${goods.name} 商品价格:${goods.price}<br>
        </#list>
**************************************************************************************************************
        <#--freemarker内建函数,语法:变量+?+函数名称-->
        <#--freemarker内建函数:size-->
        共有${goodsList?size}条记录
        <#--freemarker内建函数:字符串转换JSON -->
        <#assign pojo=obj?eval/><br>
        ${pojo.id}${pojo.name}<br>
        <#--freemarker内建函数:日期格式化-->
        ${dateTime?date}<br>
        ${dateTime?time}<br>
        ${dateTime?datetime}<br>
        ${dateTime?string("yyyy年MM月dd日 hh时mm分ss秒")}<br>
        <#--freemarker内建函数:数字转成字符串 数字会以每三位一个分隔符显示，-->
        ${num?c}<br>
**************************************************************************************************************
        <#--freemarker 空值处理,如果是空值会报错-->
        <#--freemarker 判断某变量是否存在:?? -->
        <#if aaa??>
            aaa存在值
        <#else>
            aaa不存在值<br>
        <#--freemarker 给空值变量赋值:! -->
            ${aaa!"空值赋值"}
        </#if>
**************************************************************************************************************
        <#--freemarker 运算符支持... -->
    </body>

</html>