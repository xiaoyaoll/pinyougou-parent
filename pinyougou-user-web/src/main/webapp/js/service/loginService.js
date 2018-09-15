app.service("loginService",function ($http) {

    this.getUser=function () {
        return $http.get("/login/getUsername.do");
    }
})