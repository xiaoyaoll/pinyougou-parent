app.controller("registerController",function ($scope,sellerService) {
    $scope.register=function () {
        sellerService.register($scope.entity).success(function (data) {
            if(data.success){
                location.href="/shoplogin.html";
            }else{
                alert(data.message);
            }
        });
    }

});