package com.pay.business.util.shande;

import com.core.teamwork.base.util.properties.PropertiesUtil;

/**
 * Title: 
 * Description: 
 * @author yy
 * 2017年9月25日
 */
public class ShandeConfig {
	
	/**
	 * 扫码预下单正式：https://cashier.sandpay.com.cn/qr/api/order/create
	 * 测试：http://61.129.71.103:8003/qr/api/order/create
	 */
	protected static String QR_URL = "http://61.129.71.103:8003/qr/api/order/create";
	
	/**
	 * 线上预下单正式：https://cashier.sandpay.com.cn/gateway/api/order/pay
	 * 测试：http://61.129.71.103:8003/gateway/api/order/pay
	 */
	protected static String URL = "http://61.129.71.103:8003/gateway/api/order/pay";
	
	/**
	 * 查询：https://cashier.sandpay.com.cn/gateway/api/order/query
	 * 测试：http://61.129.71.103:8003/gateway/api/order/query
	 */
	protected static String QEURY_URL = "http://61.129.71.103:8003/gateway/api/order/query";
	
	/**
	 * 退款：https://cashier.sandpay.com.cn/gateway/api/order/refund
	 * 测试：http://61.129.71.103:8003/gateway/api/order/refund
	 */
	protected static String REFUND_URL = "http://61.129.71.103:8003/gateway/api/order/refund";
	
	/**
	 * 对账：https://cashier.sandpay.com.cn/gateway/api/clearFile/download
	 * 测试：http://61.129.71.103:8003/gateway/api/clearFile/download
	 */
	protected static String DOWNLOAD_URL = "http://61.129.71.103:8003/gateway/api/clearFile/download";
	/**
	 * 代付:https://caspay.sandpay.com.cn/agent-main/openapi/agentpay
	 * 测试：http://61.129.71.103:7970/agent-main/openapi/agentpay
	 */
	protected static String AGENT_URL = "http://61.129.71.103:7970/agent-main/openapi/agentpay";
	
	/**
	 * 代付查询:https://caspay.sandpay.com.cn/agent-main/openapi/queryOrder
	 * 测试：http://61.129.71.103:7970/agent-main/openapi/queryOrder
	 */
	protected static String AGENT_QUERY_URL = "http://61.129.71.103:7970/agent-main/openapi/queryOrder";
	
	/**
	 * 代付查询:https://caspay.sandpay.com.cn/agent-main/openapi/queryBalance
	 * 测试：http://61.129.71.103:7970/agent-main/openapi/queryBalance
	 */
	protected static String AGENT_BALANCE_URL = "http://61.129.71.103:7970/agent-main/openapi/queryBalance";
	
	protected static String transCode = "RTPM";	//实时代付
	
	/**
	 * 微信扫码
	 */
	protected static String WXPAY_PRODUCTID = "00000005";
	protected static String WXPAY_PAYTOOL = "0402";
	/**
	 * 支付宝扫码
	 */
	protected static String ALIPAY_PRODUCTID = "00000006";
	protected static String ALIPAY_PAYTOOL = "0401";
	
	/**
	 * 扫码预下单
	 */
	protected static String QR_ORDERCREATE = "sandpay.trade.precreate";
	
	/**
	 * 统一预下单
	 */
	protected static String ORDERCREATE = "sandpay.trade.pay";
	
	/**
	 * 订单查询
	 */
	protected static String QR_ORDERQUERY = "sandpay.trade.query";	
	/**
	 * 退货
	 */
	protected static String QR_ORDERREFUND = "sandpay.trade.refund";
	/**
	 * 对账单下载
	 */
	protected static String QR_CLEARFILEDOWNLOAD = "sandpay.trade.download";
	
	/**
	 * 异步通知地址
	 */
	protected static String NOTIFYURL = PropertiesUtil.getProperty("rate", "sd_notify_url");
}
