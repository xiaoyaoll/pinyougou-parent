package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map<String, String> createNative() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = orderService.getpayLog(username);
        if (payLog != null) {
            Map<String, String> aNative = weixinPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
            return aNative;
        }
        return new HashMap<>();
    }

    /**
     * 查询订单状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/orderQuery")
    public Result orderQuery(String out_trade_no) {
        int i = 1;
        while (true) {
            Map orderQuery = weixinPayService.orderQuery(out_trade_no);
            if (orderQuery == null) {
                return new Result(false, "支付失败");
            }
            if ("SUCCESS".equals(orderQuery.get("trade_state"))) {
                orderService.updateOrder(out_trade_no, (String) orderQuery.get("transaction_id"));
                return new Result(true, "支付成功");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if (i > 20) {
                return new Result(false, "验证码过期");
            }
        }
    }

}
