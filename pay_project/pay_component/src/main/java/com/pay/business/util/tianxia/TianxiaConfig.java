package com.pay.business.util.tianxia;

/**
 * Title: 天付宝代付配置参数
 * @author yy
 * 2017年8月28日
 */
public class TianxiaConfig {
	protected static String version = "1.0";
	
	/**
	 * 测试环境
	 */
	protected static String TEST_URL = "http://apitest.tfb8.com/cgi-bin/v2.0/";
	
	/**
	 * 正式环境
	 */
	protected static String URL = "http://api.tfb8.com/cgi-bin/v2.0/";
	
	/**
	 * 单笔代付
	 */
	protected static String SINGLEPAY = "api_pay_single.cgi";
	
	/**
	 * 单笔代付结果查询
	 */
	protected static String SINGLEQUERY = "api_pay_single_query.cgi";
	
	/**
	 * 批量代付
	 */
	protected static String BATCHPAY = "api_pay_batch.cgi";
	
	/**
	 * 批量代付结果查询
	 */
	protected static String BATCHQUERY = "api_pay_batch_query.cgi";
	
	/**
	 * 账户余额查询
	 */
	protected static String BALANCEQUERY = "api_pay_balance_query.cgi";
}
