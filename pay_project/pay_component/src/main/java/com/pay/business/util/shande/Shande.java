package com.pay.business.util.shande;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Shande {
	private Head head;
	private Body body;
	
	public Shande(){}
	
	public Shande(Head head, Body body){
		this.head = head;
		this.body = body;
	}
	
	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	protected static class Head{
		/**
		 * 版本号
		 */
		private String version;
		/**
		 * 接口名称
		 */
		private String method;
		/**
		 * 产品编码
		 */
		private String productId;
		/**
		 * 接入类型	1-普通商户接入、2-平台商户接入
		 */
		private String accessType;
		/**
		 * 商户ID
		 */
		private String mid;
		/**
		 * 平台ID
		 */
		private String plMid;
		/**
		 * 渠道类型	07-互联网、08-移动端
		 */
		private String channelType;
		/**
		 * 请求时间	yyyyMMddhhmmss
		 */
		private String reqTime;
		
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public String getVersion() {
			return "1.0";
		}
		public String getProductId() {
			return productId;
		}
		public void setProductId(String productId) {
			this.productId = productId;
		}
		public String getAccessType() {
			return "1";
		}
		public String getMid() {
			return mid;
		}
		public void setMid(String mid) {
			this.mid = mid;
		}
		public String getPlMid() {
			return plMid;
		}
		public void setPlMid(String plMid) {
			this.plMid = plMid;
		}
		public String getChannelType() {
			return "07";
		}
		public String getReqTime() {
			return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		}
	}
	
	protected static class Body{
		/**
		 * 支付工具	0401：支付宝扫码、0402：微信扫码
		 */
		private String payTool;
		/**
		 * 商户订单号
		 */
		private String orderCode;
		/**
		 * 订单金额
		 */
		private String subject;
		/**
		 * 订单金额
		 */
		private String totalAmount;
		/**
		 * 异步通知地址
		 */
		private String notifyUrl;
		/**
		 * 原商户订单号(要退货的订单)
		 */
		private String oriOrderCode;
		/**
		 * 退货金额
		 */
		private String refundAmount;
		/**
		 * 交易日期
		 */
		private String clearDate;
		/**
		 * 文件返回类型(文件下载链接)
		 */
		private String fileType;
		
		
		public String getPayTool() {
			return payTool;
		}
		public void setPayTool(String payTool) {
			this.payTool = payTool;
		}
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getTotalAmount() {
			return totalAmount;
		}
		public void setTotalAmount(String totalAmount) {
			this.totalAmount = totalAmount;
		}
		public String getNotifyUrl() {
			return notifyUrl;
		}
		public void setNotifyUrl(String notifyUrl) {
			this.notifyUrl = notifyUrl;
		}
		public String getOriOrderCode() {
			return oriOrderCode;
		}
		public void setOriOrderCode(String oriOrderCode) {
			this.oriOrderCode = oriOrderCode;
		}
		public String getRefundAmount() {
			return refundAmount;
		}
		public void setRefundAmount(String refundAmount) {
			this.refundAmount = refundAmount;
		}
		public String getClearDate() {
			return clearDate;
		}
		public void setClearDate(String clearDate) {
			this.clearDate = clearDate;
		}
		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public String getSubject() {
			return subject;
		}
		public void setSubject(String subject) {
			this.subject = subject;
		}
	}
}
