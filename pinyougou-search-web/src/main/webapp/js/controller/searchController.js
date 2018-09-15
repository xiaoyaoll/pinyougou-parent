app.controller('searchController', function ($scope, searchService,$location) {

    //搜索对象的初始化
    $scope.searchMap = {"keywords": "", "category": "", "brand": "", "spec": {},"price":"","currentPage":1,
                        "pageSize":40,"sortField":"","sort":""};

    //首页搜索对接
    $scope.initKeywords=function () {
        $scope.searchMap.keywords= $location.search()['keywords'];
        $scope.search();
    }

    //搜索
    $scope.resultMap={};
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                $scope.pageLable();
                //判断关键字是否是品牌,如果是则隐藏.
                for(var i=0;i<$scope.resultMap.brandList.length;i++){
                    if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)!=-1){
                        $scope.brandFlag=false;
                        break;
                    }
                    $scope.brandFlag=true;
                }
            }
        );
    }

    $scope.clearSearchEntity = function () {
        $scope.searchMap.category = "";
        $scope.searchMap.brand = "";
        $scope.searchMap.spec = {};
    }

    //添加分类条件
    $scope.searchEntity = function (key, value) {
        if (key == 'category' || key == 'brand'||key=='price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
    //删除分类条件
    $scope.remove = function (key, value) {
        if (key == 'category' || key == 'brand'||key=='price') {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }
    //排序搜索
    $scope.searchSort=function (sortField,sort) {
        $scope.searchMap['sortField'] = sortField;
        $scope.searchMap['sort'] = sort;
        $scope.search();
    }


    //分页设计
    $scope.pageLable=function () {
       var totalPages=$scope.resultMap.totalPages;//1.总页数
        $scope.totalElements=$scope.resultMap.totalElements;//2.总记录数

        $scope.startPage=1;
        $scope.endPage=totalPages;

        if(totalPages>5){
            if($scope.searchMap.currentPage<=3){
                $scope.startPage=1;
                $scope.endPage=5;
            } else if($scope.searchMap.currentPage>=totalPages-2){
                $scope.startPage=totalPages-4;
                $scope.endPage=totalPages;
            }else{
                $scope.startPage=$scope.searchMap.currentPage-2;
                $scope.endPage=$scope.searchMap.currentPage+2;
            }
        }
        $scope.pageList=[];
        for(var i=$scope.startPage;i<=$scope.endPage;i++){
            $scope.pageList.push(i);
        }
    }
    //翻页查询
    $scope.searchPage=function (page) {
        page=parseInt(page+"");
        if(page>0&&page<=$scope.resultMap.totalPages){
            $scope.searchMap.currentPage=page;
            $scope.search();
        }else{
            return ;
        }
    }

});