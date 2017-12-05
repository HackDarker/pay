package com.pay.business.util.pfSZ;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class PFSZConfig {
	protected static String URL = "http://spdbweb.chinacardpos.com/payment-gate-web/gateway/api/backTransReq";
	//protected static String URL = "http://121.201.111.67:9080/payment-gate-web/gateway/api/backTransReq";
	
	/**
	 * 版本号
	 */
	protected static String VERSION = "v1.1";
	protected static String VERSION2 = "v1.2";
	/**
	 * 产品类型(微信扫码)
	 */
	protected static String PRODUCTID_WX_SCAN = "0108";
	/**
	 * 产品类型(微信H5)
	 */
	protected static String PRODUCTID_WX_H5 = "0109";
	/**
	 * 产品类型(微信H5)
	 */
	protected static String PRODUCTID_WX_APP = "0104";
	/**
	 * 产品类型(支付宝扫码)
	 */
	protected static String PRODUCTID_ALI_SCAN = "0119";
	
	/**
	 * 交易类型(扫码)
	 */
	protected static String TRANID_SCAN = "10";
	
	/**
	 * 交易类型(APP)
	 */
	protected static String TRANID_APP= "11";
	/**
	 * 交易类型(H5)
	 */
	protected static String TRANID_H5= "12";
	/**
	 * 交易类型(订单状态查询)
	 */
	protected static String TRANID_ORDER= "04";
	/**
	 * 交易类型(余额查询)
	 */
	protected static String TRANID_BALANCE= "09";
	/**
	 * 交易类型(退款)
	 */
	protected static String TRANID_REFUND= "02";
	/**
	 * 交易类型(对账)
	 */
	protected static String TRANID_DZ= "21";
	
	/**
	 * 异步通知地址
	 */
	protected static String NOTIFYURL = PropertiesUtil.getProperty("rate", "pfsz_notify_url");
}
