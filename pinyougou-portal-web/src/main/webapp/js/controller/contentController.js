app.controller("contentController",function ($scope,contentService) {
    $scope.findAll=function (categoryId) {
        contentService.findAll(categoryId).success(function (data) {
            $scope.bannerList=data;
        });
    }

    $scope.search=function (keywords) {
        location.href="http://localhost:9104/search.html#?keywords="+keywords;
    }

});