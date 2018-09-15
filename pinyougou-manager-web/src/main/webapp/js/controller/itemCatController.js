 //控制层 
app.controller('itemCatController' ,function($scope,$controller,typeTemplateService,itemCatService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	/*$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}  */
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
            if($scope.grade==1){
                $scope.entity.parentId=0;
            }else if($scope.grade==2){
                $scope.entity.parentId=$scope.entity_1.id;
            }else if($scope.grade==3){
                $scope.entity.parentId=$scope.entity_2.id;
            }
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询
                    if($scope.grade==1){
                        $scope.findAll({id:0});
                    }else if($scope.grade==2){
                        $scope.findAll($scope.entity_1);
                    }else if($scope.grade==3){
                        $scope.findAll($scope.entity_2);
                    }

				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){
		//判断一下目录级别,下面有目录就不允许删除
        if($scope.grade==3){
            //获取选中的复选框
            itemCatService.dele( $scope.selectIds ).success(
                function(response){
                    if(response.success){
                        //$scope.reloadList();//刷新列表
                        $scope.selectIds=[];
                    }
                    $scope.findAll($scope.entity_2);
                }
            );
		}else{
        	alert("该目录下还有子目录,不允许这样操作");
		}

	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    $scope.grade=1;

    $scope.setGrade=function (value) {
        $scope.grade=value;
    }
	$scope.entity_1={};
	$scope.entity_2={};
	//根据父类ID查询,面包屑导航
    $scope.findAll=function(entity){
        itemCatService.findAll(entity.id).success(
            function(response){
                if($scope.grade==1){
                    $scope.entity_1={};
                    $scope.entity_2={};
                }else if($scope.grade==2){
                    $scope.entity_1=entity;
                    $scope.entity_2={};
                }else if($scope.grade==3){
                    $scope.entity_2=entity;
                }
                $scope.list= response;
            }
        );
    }
    
    //查询类型模板
    $scope.config1 = {
        data: [],
        placeholder: '尚无模板'
    };

	$scope.findTypeTemplate=function (typeId) {
        typeTemplateService.findTypeTemplate().success(function (data) {
			$scope.config1.data=data;
			$scope.config1.placeholder=typeId;
        });
    }


});	
