package com.mmall.pojo;

import java.math.BigDecimal;

public class TeamproductKey {
    private Integer id;

    private BigDecimal price;

    public TeamproductKey(Integer id, BigDecimal price) {
        this.id = id;
        this.price = price;
    }

    public TeamproductKey() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}