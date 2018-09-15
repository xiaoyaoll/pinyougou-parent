package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import utils.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notifyurl;

    /**
     * 生成付款二维码
     *
     * @param out_trade_no 订单号
     * @param total_fee    总金额
     * @return
     */
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map param = new HashMap();
        //创建参数
        param.put("appid", appid);//公众号ID
        param.put("mch_id", partner);//商户号
        param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        param.put("body", "品优购-订单付费");
        param.put("out_trade_no", out_trade_no);
        param.put("total_fee", total_fee);
        param.put("spbill_create_ip", "127.0.0.1");
        param.put("notify_url", "http://test.itcast.cn");
        param.put("trade_type", "NATIVE");
        try {
            //生成要发送的xml,带有电子签名的xml
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);//https请求
            httpClient.setXmlParam(xmlParam);//携带的xml数据
            httpClient.post();//post请求

            //获取返回信息
            String content = httpClient.getContent();
            System.out.println(content);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);//返回的map集合
            Map map = new HashMap();
            map.put("code_url", resultMap.get("code_url"));//二维码生成URL
            map.put("total_fee", total_fee);//总金额
            map.put("out_trade_no", out_trade_no);//订单号
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    /**
     * 支付状态查询
     *
     * @param out_trade_no 支付订单号
     * @return
     */
    @Override
    public Map orderQuery(String out_trade_no) {
        try {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("appid", appid);
            queryMap.put("mch_id", partner);
            //queryMap.put("transaction_id","");
            queryMap.put("out_trade_no", out_trade_no);
            queryMap.put("nonce_str", WXPayUtil.generateNonceStr());
            String xml = WXPayUtil.generateSignedXml(queryMap, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(xml);
            httpClient.setHttps(true);
            httpClient.post();
            String content = httpClient.getContent();
            System.out.println(content);
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
