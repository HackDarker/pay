package com.pay.business.order.entity;

import java.io.Serializable;
import java.util.Date;

public class Payv2PayOrderChannlVO implements Serializable {
	private static final long serialVersionUID = -5891783317189061354L;

	// 支付集订单号
	private String orderNum;
	// 商户订单号
	private String merchantOrderNum;
	// 来源商户
	private String companyName;
	// 来源应用
	private String appName;
	// 支付平台
	private String wayName;
	// 订单名称
	private String orderName;
	// 订单金额
	private Double payMoney;
	// 手续费
	private Double payWayMoney;
	// 支付状态
	private String payStatusName;
	// 创建时间
	private Date createTime;
	
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getMerchantOrderNum() {
		return merchantOrderNum;
	}
	public void setMerchantOrderNum(String merchantOrderNum) {
		this.merchantOrderNum = merchantOrderNum;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getWayName() {
		return wayName;
	}
	public void setWayName(String wayName) {
		this.wayName = wayName;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public Double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(Double payMoney) {
		this.payMoney = payMoney;
	}
	public Double getPayWayMoney() {
		return payWayMoney;
	}
	public void setPayWayMoney(Double payWayMoney) {
		this.payWayMoney = payWayMoney;
	}
	public String getPayStatusName() {
		return payStatusName;
	}
	public void setPayStatusName(String payStatusName) {
		this.payStatusName = payStatusName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
