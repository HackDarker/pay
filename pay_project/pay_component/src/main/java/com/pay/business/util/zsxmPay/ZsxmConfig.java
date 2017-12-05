package com.pay.business.util.zsxmPay;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class ZsxmConfig {
	public static String wx_url = "http://api.cmbxm.mbcloud.com/wechat/orders";
	
	public static String ali_url = "http://api.cmbxm.mbcloud.com/alipay/orders/precreate";
	
	public static String wx_query_url = "http://api.cmbxm.mbcloud.com/wechat/orders/query";
	
	public static String ali_query_url = "http://api.cmbxm.mbcloud.com/alipay/orders/query";
	
	public static String wx_refund_url = "http://api.cmbxm.mbcloud.com/wechat/refunds";
	
	public static String ali_refund_url = "http://api.cmbxm.mbcloud.com/alipay/refunds";
	
	public static String clear_url = "http://api.cmbxm.mbcloud.com/bills";
	
	public static String notifyUrl = PropertiesUtil.getProperty("rate", "zsxm_notify_url");
	
}
