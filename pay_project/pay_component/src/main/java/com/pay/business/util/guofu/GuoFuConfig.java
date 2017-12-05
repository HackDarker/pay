package com.pay.business.util.guofu;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class GuoFuConfig {
	
	/**
	 * 基础地址
	 */
	protected static final String BASE_URL = "http://api.posp168.com/";
	
	/**
	 * 被扫码地址
	 */
	protected static final String PASSIVEPAY = "passivePay";
	
	/**
	 * 单笔订单查询
	 */
	protected static final String QRCODEQUERY = "qrcodeQuery";
	
	/**
	 * 余额查询
	 */
	protected static final String BALANCEQUERY = "http://settle.posp168.com/balance.do";
	
	/**
	 * 代付
	 */
	protected static final String VIRTPAY = "http://settle.posp168.com/virtPay.do";
	
	/**
	 * 代付查询
	 */
	protected static final String VIRTORDER = "http://settle.posp168.com/virtOrder.do";
	
	/**
	 * 回调通知
	 */
	protected static final String NOTIFY_URL = PropertiesUtil.getProperty("rate", "guofu_notify_url");
}
