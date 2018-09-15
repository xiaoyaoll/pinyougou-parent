app.controller('payController' ,function($scope ,payService,$location){
    //本地生成二维码
    $scope.createNative=function(){
        payService.createNative().success(
            function(response){
                $scope.money=  (response.total_fee/100).toFixed(2) ;	//金额
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                $scope.orderQuery($scope.out_trade_no);
            }
        );
    }


    $scope.orderQuery=function (out_trade_no) {
        payService.orderQuery(out_trade_no).success(function (data) {
            if(data.success){
                location.href="paysuccess.html#?money="+$scope.money;
            }else{
               if(data.message=="支付失败"){
                   location.href="paysuccess.html";
               }else{
                   $scope.createNative();
               }
            }
        })
    }

    $scope.showMoney=function () {
        $scope.payMoney=$location.search()['money'];
    }

});

