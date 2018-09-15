app.service('payService',function ($http) {
    this.createNative=function () {
        return $http.get("pay/createNative.do");
    }

    this.orderQuery=function (out_trade_no) {
        return $http.get("pay/orderQuery.do?out_trade_no="+out_trade_no);
    }
})