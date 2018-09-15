app.service("fileUploadService",function ($http) {

    this.fileUpload=function () {
        var formData=new FormData();//数据源
        formData.append("file",file.files[0]);
        return $http({
            method:"POST",
            url:"../fileUpload.do",
            data:formData,
            headers:{"Content-Type":undefined},
            transformRequest:angular.identity
            });
    }

})

