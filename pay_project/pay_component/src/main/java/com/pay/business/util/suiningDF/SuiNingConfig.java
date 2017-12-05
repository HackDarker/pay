package com.pay.business.util.suiningDF;

/**
 * @Title: SuiNingConfig.java
 * @Package com.pay.business.util.suining
 * @Description: 遂宁银行代付配置文件
 * @author ZHOULIBO
 * @date 2017年10月11日 下午5:27:44
 * @version V1.0
 */
public class SuiNingConfig {
	// 代付URL
	public static String payUrl = "http://36.110.43.28:7031/paybank/api/bankpay";
	// 密钥存放地址-pfx
	public static String key_pfx_path = "E:\\sign\\BKT0982010062819.pfx";
	// 密钥存放地址-cer
	public static String key_cer_path = "E:\\sign\\BKT0982010062819.cer";
	//密钥密码
	public static String key_password="111111";
	//交易标志
	public static String msgtype="IPOS";
	//本行清算行号
	public static String hvbrno="313662000015";
	//渠道
	public static String sw_tx_code="IPOS";
	//业务类型
	public static String txtpcd="C200";
	//付款账户类型
	public static String dbtrtype="AT00";
	//付款行所属网银系统行号
	public static String dbtrbrnchid="313662000015";
	//业务种类
	public static String ctgypurpcd="02001";
}
