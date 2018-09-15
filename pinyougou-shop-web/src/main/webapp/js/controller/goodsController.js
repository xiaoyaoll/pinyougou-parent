 //控制层 
app.controller('goodsController' ,function($location,$scope,specificationService,typeTemplateService,$controller,fileUploadService,goodsService,itemCatService){

	$controller('baseController',{$scope:$scope});//继承

    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                editor.html($scope.entity.tbGoodsDesc.introduction);
                $scope.entity.tbGoodsDesc.itemImages=JSON.parse($scope.entity.tbGoodsDesc.itemImages);
                $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse($scope.entity.tbGoodsDesc.customAttributeItems);
                $scope.entity.tbGoodsDesc.specificationItems=JSON.parse($scope.entity.tbGoodsDesc.specificationItems);
                if($scope.entity.tbGoods.isEnableSpec=='1'){
                    $scope.findSpecificationItems($scope.entity.tbGoods.typeTemplateId);
                }

                for(var i=0;i<$scope.entity.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	};
	//开启规格
	$scope.isOpenSpec=function () {
        if($scope.entity.tbGoods.isEnableSpec=='1'){
            //$scope.findSpecificationItems($scope.entity.tbGoods.typeTemplateId);
            return true;
        }
    }
	//规格选项上的勾选状态
	$scope.checkAttributeValue=function (keyName,keyValue) {
        var specentity =$scope.ifContains($scope.entity.tbGoodsDesc.specificationItems,"attributeName",keyName);
        if(specentity!=null){
            var specOption=specentity["attributeValue"];
        	for(var i=0; i<specOption.length;i++){
				if(specOption[i]==keyValue){
					return true;
				}
			}
		}else{
        	return false;
		}

    };

    //判断集合中是否存在该名称
    $scope.ifContains=function (list,key,keyValue) {
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];//存在
            }
        }
        return null;//不存在
    }

    //修改商品信息
    $scope.updateItems=function () {
        var id=$location.search()['id'];
        $scope.findOne(id);
    }
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
        $scope.entity.tbGoodsDesc.introduction= editor.html();
		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改
		}else{

			serviceObject=goodsService.add( $scope.entity  );//增加
		}
		serviceObject.success(
            function(response){
                if(response.success){
                    $scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};
                    document.getElementById("openSpec").checked=false;
                    $scope.SpecificationItemsList=[];
                    editor.html("");
                }else{
                    alert(response.message);
                }
            }
		);				
	}

    $scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};
    //新建商品录入
  /*  $scope.add=function(){
        $scope.entity.tbGoodsDesc.introduction= editor.html();
        goodsService.add( $scope.entity  ).success(
            function(response){
                if(response.success){
                    $scope.entity={tbGoodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};
                    document.getElementById("openSpec").checked=false;
                    $scope.SpecificationItemsList=[];
                    editor.html("");
                }else{
                    alert(response.message);
                }
            }
        );
    }*/

    //添加图片到GoodsDesc列表
    $scope.addToGoodsDesc=function (imgEntity) {

        $scope.entity.tbGoodsDesc.itemImages.push(imgEntity);
    }
    //从GoodsDesc列表中删除图片
    $scope.deleImg=function (index) {
        $scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    }

    //一级列表
	$scope.findSelectValue1=function (parentId) {
        goodsService.findSelectValue(parentId).success(function (data) {
			$scope.selectValue1=data;
        });
    }
    //二级列表
	//监控
	$scope.$watch("entity.tbGoods.category1Id",function (newValue,oldValue) {
        $scope.findSelectValue2(newValue);
        $scope.selectValue3=[];
        //$scope.entity.tbGoods.typeTemplateId=null;
    });
    $scope.findSelectValue2=function (parentId) {
        goodsService.findSelectValue(parentId).success(function (data) {
            $scope.selectValue2=data;
        });
    }

    //三级列表
    $scope.$watch("entity.tbGoods.category2Id",function (newValue,oldValue) {
        $scope.findSelectValue3(newValue);
        //$scope.entity.tbGoods.typeTemplateId=null;
    });
    $scope.findSelectValue3=function (parentId) {
        goodsService.findSelectValue(parentId).success(function (data) {
            $scope.selectValue3=data;
        });
    }
    //模板ID更新
    $scope.$watch("entity.tbGoods.category3Id",function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(function (data) {
			$scope.entity.tbGoods.typeTemplateId=data.typeId;
        });
    });

    //模板ID监控,查询模板表更新品牌列表
	$scope.$watch("entity.tbGoods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(function (data) {
            $scope.brandList=JSON.parse(data.brandIds);
            if( $scope.entity.tbGoods.id==null){

                $scope.entity.tbGoodsDesc.customAttributeItems=JSON.parse(data.customAttributeItems);
            }
        });
    });

	//启动规格
	$scope.openSpec=function ($event,id) {
		if($event.target.checked){
            $scope.findSpecificationItems(id);
            $scope.entity.tbGoods.isEnableSpec="1";
		}else{
            $scope.entity.tbGoods.isEnableSpec="0";
            $scope.SpecificationItemsList=[];
            $scope.entity.tbGoodsDesc.specificationItems=[];
            $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
		}
    }

	//查询规格和规格选项
	$scope.findSpecificationItems=function (id) {
        specificationService.findSpecificationItems(id).success(function (data) {
            $scope.SpecificationItemsList=data;
        });
    }
    //添加被选中的规格和规格选项
	$scope.checkedSpecOptions=function ($event,attributeName,attributeValue) {
        var flag=$scope.ifContains($scope.entity.tbGoodsDesc.specificationItems,"attributeName",attributeName);
        if($event.target.checked){
            if(flag!=null){
                flag.attributeValue.push(attributeValue);
            }else{
                $scope.entity.tbGoodsDesc.specificationItems.push({"attributeName":attributeName,"attributeValue":[attributeValue]});
			}
		}else{
            if(flag!=null){
                flag.attributeValue.splice(flag.attributeValue.indexOf(attributeValue,1));
                if(flag.attributeValue.length==0){
                    $scope.entity.tbGoodsDesc.specificationItems.splice($scope.entity.tbGoodsDesc.specificationItems.indexOf(flag),1);
				}							
            }
		}
    }

    //产生商品的SKU,是要存到item表的,都是ksu
	$scope.createItemList=function () {
		//items=[
		// 		{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
		// 		{"attributeName":"屏幕尺寸","attributeValue":["6寸","5.5寸"]}
		// 		]
        var items=$scope.entity.tbGoodsDesc.specificationItems;
        //初始化列表
		$scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
		for(var i=0;i<items.length;i++){//{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]
            $scope.entity.itemList=addConlums($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}
    }
	//添加列和行
    addConlums=function (list,attributeName,attributeValue) {
		var newList=[];
		for(var i=0;i<list.length;i++){//[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
			var oldRow=list[i];//{spec:{},price:0,num:9999,status:'0',isDefault:'0'}
			for(var j=0;j<attributeValue.length;j++){//attributeValue=["移动3G","移动4G"]
				var newRow=JSON.parse(JSON.stringify(oldRow));
                newRow.spec[attributeName]=attributeValue[j];
                newList.push(newRow);
			}
		}
		return newList;
    }

	//批量删除
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	//状态
	$scope.status=[{id:0,name:"未审核"},{id:1,name:"已审核"},{id:2,name:"未通过"},{id:3,name:"关闭"}];
	//查询所有分类列表
	$scope.categoryList=[];
	$scope.findAllCategory=function () {
        itemCatService.findAllCategory().success(function (data) {
			for(var i=0;i<data.length;i++){
                $scope.categoryList[data[i].id]=data[i].name;
			}
        });
    }
	

    //文件上传
	$scope.imgEntity={};

    $scope.fileUpload=function () {
        fileUploadService.fileUpload().success(function (data) {
			if(data.success){
				$scope.imgEntity.url=data.message;
			}else{
				alert(data.message);
			}
        });
    }





});
