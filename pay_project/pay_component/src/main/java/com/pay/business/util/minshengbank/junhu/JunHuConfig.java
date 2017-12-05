package com.pay.business.util.minshengbank.junhu;

import com.core.teamwork.base.util.properties.PropertiesUtil;

public class JunHuConfig {
	/**
	 * 支付回调
	 */
	protected static final String NOTIFY_URL = PropertiesUtil.getProperty("rate", "jh_notify_url");
	
	/**
	 * 支付地址
	 */
	protected static final String PAY_URL = "http://www.johutech.com:8682/johuPosp/cashierDesk/qrcodePay";

	/**
	 * 提现地址
	 */
	protected static final String DRAW_URL = "http://www.johutech.com:8682/johuPosp/cashierDesk/draw";
	
	/**
	 * 提现回调
	 */
	protected static final String DRAW_NOTIFY = PropertiesUtil.getProperty("rate", "xm_enchashment_url");
}
