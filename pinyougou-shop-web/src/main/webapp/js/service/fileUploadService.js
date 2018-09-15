app.service("fileUploadService",function ($http) {
    this.fileUpload=function () {
        var formData=new FormData();//HTML5提供的一个类表单格式的数据封装对象,主要用于文件上传
        formData.append("file",file.files[0]);//file:文件你上传项的name必须是file,append方法就是将数据封装进formData
        return $http({
            method:"POST",//提交方式
            url:"../fileUpload.do",//路径
            data:formData,//上传数据
            headers:{"Content-Type":undefined},//以未知类型上传,默认是转成json格式,设置undefined后默认一multipart/form-data
            transformRequest: angular.identity//angularjs提供的文件上传的方法
        });
    }
})