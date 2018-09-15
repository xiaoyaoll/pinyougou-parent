//刷新

app.controller("baseController",function ($scope) {


    //刷新页面
    $scope.reloadList=function () {
        $scope.ids=[];
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

//分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            //页面一加载就会执行onchange,调用分页查询的方法
            $scope.reloadList();
        }
    };



    //1.定义数组
    $scope.selectIds=[];
    //创建方法获取被选中ID
    $scope.getDeleteIds=function ($event,id) {
        //添加前判断一下是勾选还是没勾选
        if($event.target.checked){
            //被勾选
            $scope.selectIds.push(id);
        }else{
            //未被勾选1.查找位置2.从数组中删除
            var index=$scope.selectIds.indexOf(id);
            //参数1:位置,参数2:删除个数
            $scope.selectIds.splice(index,1);
        }
    }

    $scope.stringToJson=function (jsonString,key) {
        var json=JSON.parse(jsonString);
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","+json[i][key];
            }else{
                value=json[i][key];
            }
        }
        return value;
    }



});
