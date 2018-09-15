//创建你控制器
app.controller("brandController",function($scope,$controller,brandService){//要将自定义的服务进行注入

    $controller('baseController',{$scope:$scope});//继承

    $scope.findAll=function () {
        brandService.findAll().success(function (data) {
            $scope.list=data;
        });
    }
    <!--分页操作-->


    //分页配置,异步请求
    $scope.findPage=function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize).success(function (data) {
            //给总记录数赋值
            $scope.paginationConf.totalItems=data.total;
            //获取当页记录
            $scope.list=data.rows;
        });
    }


    //条件查询品牌
    $scope.searchEntity={};
    $scope.search=function (pageNum,pageSize) {
        brandService.search(pageNum,pageSize,$scope.searchEntity).success(function (data) {
            //重置总记录数
            $scope.paginationConf.totalItems=data.total;
            $scope.list=data.rows;
        });
    }


    //添加或修改品牌
    $scope.saveOrUpdate=function () {
        brandService.saveOrUpdate($scope.entity).success(function (data) {
            if(data.flag){
                $scope.reloadList();
            }else{
                alert(data.message);
            }
        });
    }

    //根据ID查询品牌
    $scope.selectOne=function (id) {
        brandService.selectOne(id).success(function (data) {
            $scope.entity=data;
        });
    }

    //删除品牌

    //删除
    $scope.deleteChecked=function () {
        if(confirm("确定要删除吗?")){//删除时的确认弹框
            brandService.deleteChecked($scope.selectIds).success(function (data) {
                $scope.reloadList();
            });
        }
    }


});