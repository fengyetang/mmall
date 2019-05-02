package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

//总购物车的vo
public class CartVo {

    List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;//购物车总价
    private Boolean allchecked;//是否全部商品勾选
    private String imageHost;
    private Integer sharePersonId;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public Integer getSharePersonId() {
        return sharePersonId;
    }
    public void setSharePersonId(Integer sharePersonId) {
    }
    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllchecked() {
        return allchecked;
    }

    public void setAllchecked(Boolean allchecked) {
        this.allchecked = allchecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }


}
