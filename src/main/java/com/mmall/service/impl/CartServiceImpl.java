package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.service.ICategoryService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

   @Autowired
   private CartMapper cartMapper;
   @Autowired
   private ProductMapper productMapper;

    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count,Integer sharePersonId){

        if (productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart==null){
            //这个产品不再这个购物车里，需要新增一个这个产品的记录
            Cart cartItem=new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartItem.setSharePersonId(sharePersonId);
            cartMapper.insert(cartItem);
        }
        else {
            //这个产品已经在购物车里面了，如果存在则数量相加
            count=cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
   }

    //更新购物车
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if (productId==null||count==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart=cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

   //删除购物车指定产品
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        //使用split方法将返回的peoductIDS字符串按照，分割开来，装进list集合里面
        //传过来的peoductIds参数是一个或者多个产品，用“，”分割开来
        List<String> productList= Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return this.list(userId);
    }

    //购物车列表，不需要分页,被封装复用
    //!!!!在这里加了分享者id
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo=this.getCartVoLimit(userId);
       // cartVo.setSharePersonId(sharePersonId);
        return ServerResponse.createBySuccess(cartVo);
    }


    //全选产品以及全不选产品
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked){
   cartMapper.checkedOrUncheckedProduct(userId,productId,checked);
   return this.list(userId);
    }

    //购物车产品总量
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if (userId==null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }



  //购物车封装核心方法
   private CartVo getCartVoLimit(Integer userId){
   CartVo cartVo=new CartVo();
       List<Cart> cartList=cartMapper.selectCartByUserId(userId);
       List<CartProductVo> cartProductVoList= Lists.newArrayList();
       BigDecimal cartTotalPrice=new BigDecimal("0");
       if (CollectionUtils.isNotEmpty(cartList)){
           for (Cart cartItem:cartList){
               CartProductVo cartProductVo=new CartProductVo();
               cartProductVo.setId(cartItem.getId());
               cartProductVo.setUserId(userId);
               cartProductVo.setProductId(cartItem.getProductId());
               Product product=productMapper.selectByPrimaryKey(cartItem.getProductId());
               if (product!=null){
                   cartProductVo.setProductMainImage(product.getMainImage());
                   cartProductVo.setProductName(product.getName());
                   cartProductVo.setProductSubtitle(product.getSubtitle());
                   cartProductVo.setProductStatus(product.getStatus());
                   cartProductVo.setProductPrice(product.getPrice());
                   cartProductVo.setProductStock(product.getStock());
                   //下面判断库存
                   int buyLimitCount=0;//购买限制库存
                   if (product.getStock()>=cartItem.getQuantity()){
                       //库存充足的时候
                       buyLimitCount=cartItem.getQuantity();
                       cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                   }else {
                    buyLimitCount=product.getStock();
                    cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                    //购物车中更新有效库存
                       Cart cartForQuantity=new Cart();
                       cartForQuantity.setId(cartItem.getId());
                       cartForQuantity.setQuantity(buyLimitCount);
                       cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                   }
                   cartProductVo.setQuantity(buyLimitCount);
                   //计算总价
                   cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                   cartProductVo.setProductChecked(cartItem.getChecked());
                   }
               if (cartItem.getChecked()==Const.Cart.CHECKED){
                   //如果是已经勾选的状态，增加到整个购物车的总价中
                   cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
               }
               cartProductVoList.add(cartProductVo);
           }
       }
             cartVo.setCartTotalPrice(cartTotalPrice);
             cartVo.setCartProductVoList(cartProductVoList);
             cartVo.setAllchecked(this.getAllCheckedStatus(userId));
             cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
             return cartVo;
   }
   private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
       return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
   }


}
