app.service("contentService",function ($http) {

    this.findAll=function (categoryId) {
        return $http.get("content/findAll.do?categoryId="+categoryId);
    }

})