package com.pay.business.util.minshengbank.xm;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class MinShengXMConfig {
	/**
	 * 支付回调
	 */
	protected static final String NOTIFY_URL = PropertiesUtil.getProperty("rate", "xm_notify_url");
	
	/**
	 * 支付地址
	 */
	protected static final String PAY_URL = "http://rkt.ruikafinance.com:18882/scanPosp/cashierDesk/qrcodePay";

	/**
	 * 提现地址
	 */
	protected static final String DRAW_URL = "http://rkt.ruikafinance.com:18882/scanPosp/cashierDesk/draw";
	
	/**
	 * 提现回调
	 */
	protected static final String DRAW_NOTIFY = PropertiesUtil.getProperty("rate", "xm_enchashment_url");
}
