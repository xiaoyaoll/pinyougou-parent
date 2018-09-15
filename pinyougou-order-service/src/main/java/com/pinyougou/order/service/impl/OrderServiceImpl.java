package com.pinyougou.order.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojogroup.Cart;
import org.apache.commons.collections.OrderedMap;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderExample.Criteria;

import entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import utils.IdWorker;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 增加保存订单到数据库
     * 从Redis中读取数据
     * 保存订单数据到支付日志
     */
    @Override
    public void add(TbOrder order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        List<String> list = new ArrayList<>();
        double total_fee = 0;
        for (Cart cart : cartList) {
            TbOrder tborder = new TbOrder();
            long orderId = idWorker.nextId();
            tborder.setOrderId(orderId);
            tborder.setUserId(order.getUserId());//用户名
            tborder.setPaymentType(order.getPaymentType());//支付类型
            tborder.setStatus("1");//状态：未付款
            tborder.setCreateTime(new Date());//订单创建日期
            tborder.setUpdateTime(new Date());//订单更新日期
            tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
            tborder.setReceiverMobile(order.getReceiverMobile());//手机号
            tborder.setReceiver(order.getReceiver());//收货人
            tborder.setSourceType(order.getSourceType());//订单来源
            tborder.setSellerId(cart.getSellerId());//商家ID
            //循环购物车明细
            double money = 0;
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);//订单ID
                orderItem.setSellerId(cart.getSellerId());
                money += orderItem.getTotalFee().doubleValue();//金额累加
                orderItemMapper.insert(orderItem);
            }
            total_fee += money;
            tborder.setPayment(new BigDecimal(money));
            list.add(tborder.getOrderId() + "");
            orderMapper.insert(tborder);

        }
        //添加到支付日志
        TbPayLog payLog = new TbPayLog();
        payLog.setOutTradeNo(idWorker.nextId() + "");
        payLog.setCreateTime(new Date());
        payLog.setTotalFee((long) (total_fee * 100));
        payLog.setOrderList(list.toString().replace("[", "").replace("]", ""));
        payLog.setTradeState("0");//未支付
        payLog.setPayType("1");
        payLog.setUserId(order.getUserId());
        payLogMapper.insert(payLog);
        //保存到Redis
        redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);

        //清空Redis缓存
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 从缓存中获取用户支付日志
     *
     * @param userName
     * @return
     */
    @Override
    public TbPayLog getpayLog(String userName) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userName);
    }

    /**
     * 更新订单状态
     *
     * @param out_trade_no
     * @param transaction_id
     */
    @Override
    public void updateOrder(String out_trade_no, String transaction_id) {
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTradeState("1");//支付状态
        payLog.setTransactionId(transaction_id);//支付流水号
        payLogMapper.updateByPrimaryKey(payLog);
        String orderListStr = payLog.getOrderList();
        String[] orderIdList = orderListStr.split(",");
        for (String orderId : orderIdList) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setEndTime(new Date());
            tbOrder.setStatus("2");//支付状态
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //清除缓存
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
