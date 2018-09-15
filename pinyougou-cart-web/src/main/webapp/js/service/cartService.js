app.service('cartService',function($http){
    this.getCartList=function () {
        return $http.get("cart/getCartList.do");
    };
    this.addGoodsToCartList=function (itemId, num) {
        return $http.get("cart/addToCart.do?itemId="+itemId+"&num="+num);
    };

    this.findAddressList=function () {
        return $http.get("address/findAddressList.do");
    };

    this.genOrder=function (order) {
        return $http.post("cart/genOrder.do",order);
    };
});