package com.pay.business.util.wukaPay;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class WukaConfig {
	public static String notify = PropertiesUtil.getProperty("rate", "wuka_notify_url");
	
	public static String returnUrl = PropertiesUtil.getProperty("rate", "wuka_return_url");
	
	public static String payUrl ="http://payjust.cn/orgReq/qrPay";
	
	public static String h5PayUrl ="http://payjust.cn/orgReq/h5Pay";
	
	public static String queryUrl ="http://payjust.cn/orgReq/trQue";
}
