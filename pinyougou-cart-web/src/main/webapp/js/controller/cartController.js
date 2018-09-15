    app.controller('cartController', function ($scope, cartService) {

    $scope.getCartList=function () {
        cartService.getCartList().success(function (data) {
           $scope.cartList=data;
            $scope.sum();
        });
    };
    $scope.addGoodsToCartList=function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(function (data) {
            if(data.success){
                $scope.getCartList();
            }else{
                alert(data.message);
            }
        });
    };
    $scope.sum=function () {
        $scope.num=0;
        $scope.totalFee=0;
        for(var i=0;i<$scope.cartList.length;i++){
            var itemList=$scope.cartList[i].orderItemList;
            for(var j=0;j<itemList.length;j++){
                $scope.num+=itemList[j].num;
                $scope.totalFee+=itemList[j].totalFee;
            }
        }
    }

    //查询地址列表
        $scope.findAddressList=function () {
            cartService.findAddressList().success(function (data) {
                $scope.addressList=data;
                for (var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.addressSelected=$scope.addressList[i];
                        break;
                    }
                }

            });
        }
        //点击选择地址
        $scope.selectedAddress=function (addressSelected) {
            $scope.addressSelected=addressSelected;
        }

        $scope.isSelectedAddress=function (address) {
            if(address==$scope.addressSelected){
                return true;
            }else{
                return false;
            }
        }


        $scope.order={};
        $scope.genOrder=function () {
            //生成订单
            $scope.order.mobile=$scope.addressSelected.mobile;
            $scope.order.contact=$scope.addressSelected.contact;
            $scope.order.address=$scope.addressSelected.address;
            $scope.order.paymentType='1';
            cartService.genOrder($scope.order).success(function (data) {
                if(data.success){
                    location.href="pay.html";
                }else{
                    alert(data.message);
                }
            });

            }
});
