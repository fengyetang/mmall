package com.mmall.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
    private Integer id;

    private Integer sharePersonId;

    private Long orderNo;

    private Integer userId;

    private Integer personId;

    private BigDecimal payment;

    private Integer paymentType;

    private Date startTime;

    private Integer status;

    private Date paymentTime;

    private Date sendTime;

    private Date closeTime;

    private Date createTime;

    private Date updateTime;

    public Order(Integer id,Integer sharePersonId, Long orderNo, Integer userId, Integer personId, BigDecimal payment, Integer paymentType, Date startTime, Integer status, Date paymentTime, Date sendTime, Date closeTime, Date createTime, Date updateTime) {
        this.id = id;
        this.sharePersonId=sharePersonId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.personId = personId;
        this.payment = payment;
        this.paymentType = paymentType;
        this.startTime = startTime;
        this.status = status;
        this.paymentTime = paymentTime;
        this.sendTime = sendTime;
        this.closeTime = closeTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getSharePersonId() {
        return sharePersonId;
    }

    public void setSharePersonId(Integer sharePersonId) {
        this.sharePersonId = sharePersonId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Order() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getpersonId() {
        return personId;
    }

    public void setpersonId(Integer personId) {
        this.personId = personId;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}