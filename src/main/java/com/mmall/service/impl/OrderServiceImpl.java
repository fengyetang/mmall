package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.PersonVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PersonMapper personMapper;

    //创建订单
    public ServerResponse createOrder(Integer userId, Integer personId) {
        //从购物车中选取选中的数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //计算这个订单总价
         ServerResponse serverResponse=this.getCartOrderItem(userId,cartList);
         if (!serverResponse.isSuccess()){
             return serverResponse;
        }
       List<OrderItem> orderItemList=(List<OrderItem>)serverResponse.getData();
       BigDecimal payment=this.getOrderTotalPrice(orderItemList);
       //生成订单
        Order order=this.assembleOrder(userId,personId,payment);
        if (order==null){
            return ServerResponse.createByErrorMessage("生成订单错误");
        }
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);
        //生成成功,减少产品库存
       this.reduceProductStock(orderItemList);
        //清空购物车
        this.cleanCart(cartList);
        //返回前端订单明细
        OrderVo orderVo=assembleOrderVo(order,orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }


    //返回前端的订单详情封装
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo=new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setPersonId(order.getpersonId());
        Person person=personMapper.selectByPrimaryKey(order.getpersonId());
        if (person!=null){
            orderVo.setReceiverName(person.getName());
            orderVo.setPersonVo(assemblePersonVo(person));
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setStartTime(DateTimeUtil.dateToStr(order.getStartTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCreatTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        for (OrderItem orderItem:orderItemList){
            OrderItemVo orderItemVo=assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
    OrderItemVo orderItemVo=new OrderItemVo();
    orderItemVo.setOrderNo(orderItem.getOrderNo());
    orderItemVo.setProductId(orderItemVo.getProductId());
    orderItemVo.setProductName(orderItem.getProductName());
    orderItemVo.setProductImage(orderItem.getProductImage());
    orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
    orderItemVo.setQuantity(orderItem.getQuantity());
    orderItemVo.setTotalPrice(orderItem.getTotalPrice());
    orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
    return orderItemVo;
    }


    //组装一个personVo
    private PersonVo assemblePersonVo(Person person){
        PersonVo personVo=new PersonVo();
        personVo.setName(person.getName());
        personVo.setLiveAddress(person.getLiveAddress());
        personVo.setLiveProvince(person.getLiveProvince());
        personVo.setLiveCity(person.getLiveCity());
        personVo.setLiveDistrict(person.getLiveDistrict());
        personVo.setMobile(person.getMobile());
        personVo.setIdCard(person.getIdCard());
        personVo.setPhone(person.getPhone());
        return personVo;
    }

    //清空购物车的方法
    private void cleanCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    //减少产品库存的方法
    private void reduceProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem:orderItemList){
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    //组装订单方法
    private Order assembleOrder(Integer userId,Integer personId,BigDecimal payment){
        Order order=new Order();
        //订单号
        long orderNo=this.generateOrderNo();
        order.setOrderNo(orderNo);
        order.setPayment(payment);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setUserId(userId);
        order.setpersonId(personId);
        int rowCount=orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;

    }

    //生成订单号,用时间戳取余组装订单号
    private long generateOrderNo(){

        long currentTime=System.currentTimeMillis();
        //下面方法代表随机生成数字0-9
        return currentTime+new Random().nextInt(100);
    }

   //计算总价的方法
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
           payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }



    //封装订单list
    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数据，包括产品的状态和数量

        for (Cart cartItem : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
            //校验商品是否下架
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在线销售状态");
            }
            //校验商品库存是否充足
            if (cartItem.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }
            orderItem.setSharePersonId(cartItem.getSharePersonId());
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartItem.getQuantity()));
            orderItemList.add(orderItem);
        }
           return ServerResponse.createBySuccess(orderItemList);

    }

    public ServerResponse<String> cancel(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }
        if (order.getStatus()!=Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已付款，无法取消订单");
        }
        Order updateOrder=new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int row=orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

     //获取购物车产品
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo=new OrderProductVo();
        //从购物车中获取数据
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse=this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
         List<OrderItem> orderItemList=(List<OrderItem>)serverResponse.getData();
        List<OrderItemVo> orderItemVoList=Lists.newArrayList();
        BigDecimal payment=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);

    }

    //前台个人中心，查看订单详情

    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order=orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNoUserId(orderNo,userId);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到对应订单");
    }
    //前端用户查看订单列表
    public ServerResponse<PageInfo> getOrderList(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList=assembleOrderVoList(orderList,userId);
        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);

    }
    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){

        List<OrderVo> orderVoList=Lists.newArrayList();
        for(Order order:orderList){
            List<OrderItem> orderItemList=Lists.newArrayList();
            if (userId==null){
                //todo管查询的时候，不需要传userId
                orderItemList=orderItemMapper.getByOrderNo(order.getOrderNo());
            }else {
                orderItemList=orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }


    //backend
    //后端管理订单列表
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList=orderMapper.selectAllOrder();
        List<OrderVo> orderVoList=this.assembleOrderVoList(orderList,null);
        PageInfo pageResult=new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    //后端查看订单详情
    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    //后端搜索订单
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList=orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo=assembleOrderVo(order,orderItemList);
            PageInfo pageResult=new PageInfo(Lists.newArrayList(order));
            pageResult.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }


    //发团成功业务逻辑
    public ServerResponse<String> manageStartTeam(Long orderNo){
        Order order=orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
             if (order.getStatus()==Const.OrderStatusEnum.PAID.getCode()){
                 order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
                 order.setSendTime(new Date());
                 orderMapper.updateByPrimaryKeySelective(order);
                 return ServerResponse.createBySuccess("发团成功");
             }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }




}