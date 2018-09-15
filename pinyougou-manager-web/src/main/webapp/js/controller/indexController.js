app.controller("indexController",function ($scope,indexService) {
    $scope.getName=function () {
        indexService.getName().success(function (data) {
            $scope.username=data.username;
        });
    }
})