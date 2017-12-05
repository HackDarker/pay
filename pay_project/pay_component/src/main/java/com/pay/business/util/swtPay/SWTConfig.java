package com.pay.business.util.swtPay;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class SWTConfig {
	//正式地址：
	protected static String URL = "https://www.56zhifu.com/user/MerchantmerchantWechatPay.do";
	//protected static String URL = "http://59.41.60.154:8090/user/MerchantmerchantWechatPay.do";
	
	//正式地址：https://www.56zhifu.com/user/MerchantmerchantTransQuery.do
	protected static String QUERY_URL = "https://www.56zhifu.com/user/MerchantmerchantTransQuery.do";
	//protected static String QUERY_URL = "http://59.41.60.154:8090/user/MerchantmerchantTransQuery.do";
	
	//正式地址：
	protected static String FILE_URL = "https://www.56zhifu.com/user/MerchantAccFileDown.do";
	//protected static String FILE_URL = "http://59.41.60.154:8090/user/MerchantAccFileDown.do";
	
	protected static String VERSION = "2.0";
	
	protected static String SIGNTYPE = "MD5";
	
	protected static String ORDTYPE = "0";
	
	/**
	 * 异步通知地址
	 */
	protected static String NOTIFYURL = PropertiesUtil.getProperty("rate", "swt_notify_url");
}
