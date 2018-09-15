 //控制层 
app.controller('userController' ,function($scope,userService){
	$scope.register=function (entity,code) {

        if($scope.password!=$scope.entity.password){
            alert("密码输入不一致");
            return ;
        }
        if(phone==""||phone==null){
            alert("手机号没有填写");
            return ;
        }
        userService.add(entity,code).success(function (data) {
			    alert(data.message);
        });
    }

    $scope.sendCode=function (phone) {
        if(phone==""||phone==null){
            alert("手机号没有填写");
            return ;
        }
        userService.sendCode(phone).success(function (data) {
                alert(data.message);
        });
    }

    $scope.checkPw=function () {

        if($scope.password!=$scope.entity.password){
            alert("密码输入不一致");
        }
    }

    $scope.getUsername=function () {
        userService.getUsername().success(function (data) {
            $scope.username=data['username'];
        });
    }
});	
