package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 微信支付接口
 */
public interface WeixinPayService {
    /**
     * 生成二维码
     * @param out_trade_no 订单号
     * @param total_fee 总金额
     * @return
     */
    public Map createNative(String out_trade_no, String total_fee);

    /**
     * 订单状态查询
     * @param out_trade_no
     * @return
     */
    public Map orderQuery(String out_trade_no);


}
