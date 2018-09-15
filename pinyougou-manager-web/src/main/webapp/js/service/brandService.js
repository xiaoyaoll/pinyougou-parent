

//自定义服务,参数1:服务名称,参数2:服务中的内容.用到其他服务就注入
//所谓服务就是处理数据的,与后台进行交互的
//服务的提取与后台交互的部分进行提取

app.service("brandService",function ($http) {
    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    }

    this.findPage=function (pageNum,pageSize) {
        return $http.get("../brand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    }

    this.search=function (pageNum,pageSize,searchEntity) {
        return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
    }

    this.saveOrUpdate=function (entity) {
        return $http.post("../brand/saveOrUpdate.do",entity);
    }

    this.selectOne=function (id) {
        return $http.get("../brand/selectOne.do?id="+id);
    }

    this.deleteChecked=function (ids) {
        return $http.post("../brand/delete.do",ids);
    }

    this.selectAll=function () {
        return $http.get("../brand/selectAll.do");
    }

});