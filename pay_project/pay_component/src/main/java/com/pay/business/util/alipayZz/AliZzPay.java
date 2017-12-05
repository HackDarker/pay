package com.pay.business.util.alipayZz;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.core.teamwork.base.util.encrypt.MD5;
import com.core.teamwork.base.util.properties.PropertiesUtil;

public class AliZzPay {
	private static String NOTIFY_URL = PropertiesUtil.getProperty("rate", "aliZz_notify_url");
	private static String RETURN_URL = PropertiesUtil.getProperty("rate", "aliZz_return_url");
	private static String URL = "http://47.95.48.154/form/pay";
	
	/**
	 * 支付宝转账模式封装wap
	 * @param orderNum	订单号
	 * @param payMoney	金额
	 * @param ip		ip
	 * @param mchId		商户号
	 * @param key		密钥
	 * @return
	 */
	public static String pay(String orderNum,String payMoney,String ip,String mchId,String key
			,String orderName){
		String result = "";
		RequestBean rbean = new RequestBean();
		//分配的商户号
		rbean.setP1_usercode(mchId);
		//用户订单号，建议8 位商户号+14 必填位时间yyyymmddhhmmss+5 位流水号，中间用“-”分隔。例如：
		//12345678-20150728132430-12345。（只是建议，商户的订单号也可以不采用这种格式）
		rbean.setP2_order(orderNum);
		//订单金额，精确到分。例如99.99
		rbean.setP3_money(payMoney);
		//用户明文跳转地址，用于告知付款必填人支付结果。必须包含http://。注意：该URL 建议不包
		//含GET 参数，即形如“？name=value”的内容，支付网关不确保这些GET 参数通过后台方式向商户反馈时能被保留
		rbean.setP4_returnurl(RETURN_URL);//
		//服务器异步通知地址，用于通知商必填户系统处理业务（数据库更新等）。必须包含http://。注意：该URL 建议不包含GET 参数，
		//即形如“？name=value”的内容，支付网关不确保这些GET 参数通过后台方式向商户反馈时能被保留
		rbean.setP5_notifyurl(NOTIFY_URL);
		//商户订单开始时间 yyyyMMddHHmmss
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		rbean.setP6_ordertime(sdf.format(new Date()));
		//商户支付方式：固定值4。如果用必填户传递参数为空，则默认网银支付。
		rbean.setP9_paymethod("4");
		//客户、或者玩家所在平台账号。请必填务必填写真实信息，否则将影响后续查单结果
		rbean.setP14_customname(orderName);
		//客户ip 地址，规定以必填192_168_0_253 格式，如果以“192.168.0.253”可能会发生签名错误
		rbean.setP17_customip(ip.replace(".", "_"));
		//商品名称
		rbean.setP18_product(orderName);
		//终端设备固定值2
		rbean.setP25_terminal("2");
		//支付场景固定值3
		rbean.setP26_iswappay("2");
		
		// 生成
		rbean.setP7_sign(GetSign(rbean,key));

		/*String s = HttpsUtil.doGetString("http://47.95.48.154/form/pay" +GetUrlCS(rbean));
		System.out.println(s);*/
		result = URL +GetUrlCS(rbean);
		System.out.println(result);
		return result;
	}
	
	// 获取签名
	private static String GetSign(RequestBean bean,String compKey) {
		String rawString = bean.p1_usercode + "&" + bean.p2_order + "&"
				+ bean.p3_money + "&" + bean.p4_returnurl + "&"
				+ bean.p5_notifyurl + "&" + bean.p6_ordertime + compKey;

		// return
		// FormsAuthentication.HashPasswordForStoringInConfigFile(rawString,
		// "MD5");
		return MD5.GetMD5Code(rawString).toUpperCase();
	}
	
	private static String GetUrlCS(RequestBean bean) {
		String khsbm = "100101";// 客户识别码 【快捷支付的时候需要传递此内容】 【 在您系统里对应用户的唯一标示】
		// String rawString = bean.p1_usercode + "&" + bean.p2_order + "&" +
		// bean.p3_money + "&" + bean.p4_returnurl + "&" + bean.p5_notifyurl +
		// "&" + bean.p6_ordertime + compKey;
		String rawString = "?p1_usercode=" + bean.p1_usercode + "&p2_order="
				+ bean.p2_order + "&p3_money=" + bean.p3_money
				+ "&p4_returnurl=" + bean.p4_returnurl + "&p5_notifyurl="
				+ bean.p5_notifyurl + "&p6_ordertime=" + bean.p6_ordertime
				+ "&p9_paymethod=" + bean.p9_paymethod + "&p14_customname="
				+ bean.p14_customname + "&p17_customip=" + bean.p17_customip
				+ "&p25_terminal=" + bean.p25_terminal + "&p26_iswappay="
				+ bean.p26_iswappay + "&p7_sign=" + bean.getP7_sign();
		/*
		 * "&p19_productcat" + bean.p19_productcat + "&p20_productnum" +
		 * bean.p20_productnum + 卡类
		 */
		// return
		// FormsAuthentication.HashPasswordForStoringInConfigFile(rawString,
		// "MD5");
		return rawString;
	}
	
	public static String GetSign1(ResponseBean bean,String compKey) {
		 String rawString = bean.p1_usercode + "&" + bean.p2_order + "&" +
			 bean.p3_money + "&" + bean.p4_status +"&" + bean.p5_jtpayorder + "&"
			 + bean.p6_paymethod
			 +"&"+bean.p7_paychannelnum+"&"+bean.p8_charset+"&"+bean.p9_signtype+"&"+
			 compKey;
			
		 return MD5.GetMD5Code(rawString).toUpperCase();
	}
	
	public static void main(String[] args) {
		// 基础数据
		String mchId = "28C85D0FB0E5475B9498C86971FC851F"; // 商户ID
		String key = "8A3F1E76C5A7BB0E1B26CBE1928AE17899C25B2651E38FE279B0F49F23CAFA9C";// 商户密钥
		pay(System.currentTimeMillis()+"", "1", "192.168.1.172", mchId, key,System.currentTimeMillis()+"");
	}
}
