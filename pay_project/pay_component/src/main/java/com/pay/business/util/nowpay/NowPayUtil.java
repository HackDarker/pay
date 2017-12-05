package com.pay.business.util.nowpay;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.core.teamwork.base.util.properties.PropertiesUtil;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.StringHandleUtil;
import com.pay.business.util.httpsUtil.HttpsUtil;

public class NowPayUtil {
	private static String payUrl = "https://pay.ipaynow.cn";
	private static String version = "1.0.0";//功能码 定值：WP001
	private static String mhtOrderType = "01";//商户交易类型 定值01（普通消费）
	private static String mhtCurrencyType="156";//商户订单币种类型 定值156（人民币）
	private static String NOTIFY_URL = PropertiesUtil.getProperty("rate", "now_notify_url");
	private static String RETURN_URL = PropertiesUtil.getProperty("rate", "now_return_url");;
	
	
	/**
	 * wap支付
	 * @param orderNum 订单号
	 * @param payMoney 订单金额
	 * @param payPlatform 支付平台1支付宝2微信
	 * @param ip 用户真实ip
	 * @param appId 现在支付appId
	 * @param key	现在支付key
	 * @return
	 */
	public static Map<String,String> wayPay(String orderNum,String payMoney,String payPlatform
			,String ip,String appId,String key,String mhtOrderName){
		Map<String,String> result = new HashMap<>();
		try {
			Map<String,Object> param = new HashMap<>();
			//功能码 定值：WP001
			param.put("funcode", "WP001");
			//接口版本号 定值：1.0.0
			param.put("version", version);
			//商户应用唯一标识
			param.put("appId", appId);
			//订单号
			param.put("mhtOrderNo", orderNum);
			//商户商品名称
			param.put("mhtOrderName", mhtOrderName);
			//商户交易类型 定值01（普通消费）
			param.put("mhtOrderType", mhtOrderType);
			//商户订单币种类型 定值156（人民币）
			param.put("mhtCurrencyType", mhtCurrencyType);
			//商户订单交易金额   单位(人民币)：分 整数，无小数点
			param.put("mhtOrderAmt", payMoney);
			//商户订单详情
			param.put("mhtOrderDetail", mhtOrderName);
			//商户订单开始时间 yyyyMMddHHmmss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			param.put("mhtOrderStartTime", sdf.format(new Date()));
			//商户后台通知URL
			param.put("notifyUrl", NOTIFY_URL);
			//商户前台通知URL
			param.put("frontNotifyUrl", RETURN_URL);
			//商户字符编码
			param.put("mhtCharset", "UTF-8");
			//设备类型 定值：0601（手机网页）
			param.put("deviceType", "0601");
			//用户所选渠道类型   银联：11支付宝：12微信：13 手Q：25(暂只支持12、13)
			if("1".equals(payPlatform)){
				param.put("payChannelType", "12");
				//输出格式   outputType=0(默认取值)outputType=1outputType=2(注：微信deeplink)，outputType=3
				param.put("outputType", "1");//1是https链接，0是form表单
//				param.put("outputType", "0");//1是https链接，0是form表单
			}else{
				param.put("payChannelType", "13");
				//输出格式   outputType=0(默认取值)outputType=1outputType=2(注：微信deeplink)，outputType=3
				param.put("outputType", "2");//2是微信官方链接 ;1或0是现在支付链接但是必须传真实ip
//				param.put("outputType", "1");//2是微信官方链接 ;1或0是现在支付链接但是必须传真实ip
//				param.put("consumerCreateIp", "113.116.89.229");//用户支付ip
			}
			//是否支持信用卡支付 1：允许使用信用卡 0：禁用信用卡
			param.put("mhtLimitPay", "1");
			//商户签名方法  定值：MD5
			param.put("mhtSignType", "MD5");
			
			
			String paramStr = PaySignUtil.getParamStr(param);
			String keyMd5 = PaySignUtil.getSign(key,"MD5");
			System.out.println("现在支付签名串："+paramStr+"&"+keyMd5);
			String mhtSignature = PaySignUtil.getSign(paramStr+"&"+keyMd5, "MD5");
			//商户数据签名
			param.put("mhtSignature", mhtSignature);
		
			String resultStr =HttpsUtil.doPostString(payUrl, param, "UTF-8");
			
			resultStr = URLDecoder.decode(resultStr);
			System.out.println("现在支付返回结果："+resultStr);
			if(resultStr.startsWith("responseCode=A001&tn=")){
				result.put("code", "10000");
				String webStr = resultStr.substring(resultStr.indexOf("&tn=")+"&tn=".length()
						,resultStr.indexOf("&appId="));
				result.put("webStr", webStr);
			}else{
				result.put("code", "10001");
				String msgStr= resultStr.substring(resultStr.indexOf("&responseMsg=")+"&responseMsg=".length());
				String msg = msgStr.substring(0,msgStr.indexOf("&"));
				result.put("msg", msg+"(上游问题)");
			}
			
		} catch (Exception e) {
			result.put("code", "10001");
			result.put("msg", "请求现在支付异常");
			e.printStackTrace();
		}
		System.out.println("现在支付返回："+result.toString());
		return result;
	}
	
	/**
	 * 网页支付
	 * @param orderNum 订单号
	 * @param payMoney 订单金额
	 * @param payPlatform 支付平台1支付宝2微信
	 * @param ip 用户真实ip
	 * @param appId 现在支付appId
	 * @param key	现在支付key
	 * @return
	 */
	public static Map<String,String> scanPay(String orderNum,String payMoney,String ip
			,String appId,String key,String mhtOrderName){
		Map<String,String> result = new HashMap<>();
		try {
			Map<String,Object> param = new HashMap<>();
			//功能码 定值：WP001
			param.put("funcode", "WP001");
			//接口版本号 定值：1.0.0
			param.put("version", version);
			//商户应用唯一标识
			param.put("appId", appId);
			//订单号
			param.put("mhtOrderNo", orderNum);
			//商户商品名称
			param.put("mhtOrderName", mhtOrderName);
			//商户交易类型 定值01（普通消费）
			param.put("mhtOrderType", mhtOrderType);
			//商户订单币种类型 定值156（人民币）
			param.put("mhtCurrencyType", mhtCurrencyType);
			//商户订单交易金额   单位(人民币)：分 整数，无小数点
			param.put("mhtOrderAmt", payMoney);
			//商户订单详情
			param.put("mhtOrderDetail", mhtOrderName);
			//商户订单开始时间 yyyyMMddHHmmss
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			param.put("mhtOrderStartTime", sdf.format(new Date()));
			//商户后台通知URL
			param.put("notifyUrl", NOTIFY_URL);
			//商户字符编码
			param.put("mhtCharset", "UTF-8");
			//定值：04
			param.put("deviceType", "04");
			//用户所选渠道类型   银联：11支付宝：12微信：13 银联20 手Q：25(暂只支持12、20)
			param.put("payChannelType", "12");
			//0  返回支付链接1 redirect支付页面
			param.put("outputType", "0");
//			param.put("outputType", "0");
			//是否支持信用卡支付 1：允许使用信用卡 0：禁用信用卡
//			param.put("mhtLimitPay", "1");
			//商户签名方法  定值：MD5
			param.put("mhtSignType", "MD5");
			
			
			String paramStr = PaySignUtil.getParamStr(param);
			String keyMd5 = PaySignUtil.getSign(key,"MD5");
			System.out.println("现在支付签名串："+paramStr+"&"+keyMd5);
			String mhtSignature = PaySignUtil.getSign(paramStr+"&"+keyMd5, "MD5");
			//商户数据签名
			param.put("mhtSignature", mhtSignature);
		
			String resultStr =HttpsUtil.doPostString(payUrl, param, "UTF-8");
			
			resultStr = URLDecoder.decode(resultStr);
			System.out.println("现在支付返回结果："+resultStr);
			if(resultStr.startsWith("responseCode=A001&tn=")){
				result.put("code", "10000");
				String webStr = resultStr.substring(resultStr.indexOf("&tn=")+"&tn=".length()
						,resultStr.indexOf("&appId="));
				result.put("webStr", webStr);
			}else{
				result.put("code", "10001");
				String msgStr= resultStr.substring(resultStr.indexOf("&responseMsg=")+"&responseMsg=".length());
				String msg = msgStr.substring(0,msgStr.indexOf("&"));
				result.put("msg", msg+"(上游问题)");
			}
			
		} catch (Exception e) {
			result.put("code", "10001");
			result.put("msg", "请求现在支付异常");
			e.printStackTrace();
		}
		System.out.println("现在支付返回："+result.toString());
		return result;
	}
	
	public static Map<String,String> query(String orderNum,String appId,String key){
		Map<String,String> result = new HashMap<>();
		try {
			Map<String,Object> param = new HashMap<>();
			//功能码 定值：WP001
			param.put("funcode", "MQ002");
			//接口版本号 定值：1.0.0
			param.put("version", version);
			//设备类型 定值：0601（手机网页）
			param.put("deviceType", "0601");
			//商户应用唯一标识
			param.put("appId", appId);
			//订单号
			param.put("mhtOrderNo", orderNum);
			//商户字符编码
			param.put("mhtCharset", "UTF-8");
			//商户签名方法  定值：MD5
			param.put("mhtSignType", "MD5");
			
			String mhtSignature = getSign(param, key);
			//商户数据签名
			param.put("mhtSignature", mhtSignature);
		
			String resultStr =HttpsUtil.doPostString(payUrl, param, "UTF-8");
			
			resultStr = URLDecoder.decode(resultStr);
			
			
			System.out.println("现在支付返回结果："+resultStr);
			
			Map<String,Object> map = StringHandleUtil.toMap(resultStr);
			String sign = map.remove("signature").toString();
			if(sign.equals(getSign(map, key))){
				if(map.containsKey("transStatus")
						&&map.get("transStatus").toString().equals("A001")){
					System.out.println("-------现在支付查询订单成功---------");
					result.put("code", "10000");
					result.put("msg", "支付成功");
					String payMoney = DecimalUtil.centsToYuan(map.get("mhtOrderAmt").toString());
					result.put("total_fee", payMoney);
					result.put("orderNum", map.get("mhtOrderNo").toString());
				}else{
					System.out.println("---------现在支付查询失败----------");
					result.put("code", "10001");
					result.put("msg", "订单未支付");
				}
				
			}else{
				System.out.println("--------现在支付查询订单验签失败--------");
				result.put("code", "10001");
				result.put("msg", "现在支付查询订单验签失败");
			}
			
		} catch (Exception e) {
			System.out.println("--------现在支付查询异常--------");
			result.put("code", "10001");
			result.put("msg", "请求现在支付异常");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 签名
	 * @param map
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getSign(Map<String,Object> map,String key) throws Exception{
		String paramStr = PaySignUtil.getParamStr(map);
		String keyMd5 = PaySignUtil.getSign(key,"MD5");
		System.out.println("现在支付签名串："+paramStr+"&"+keyMd5);
		String mhtSignature = PaySignUtil.getSign(paramStr+"&"+keyMd5, "MD5");
		return mhtSignature;
	}
	
	public static void main(String[] args) {
//		wayPay(System.currentTimeMillis()+"", "1","2","","151030820304872","J59FRTWCOgHCfgYWAfiRTraUVi6CeaDY");
		scanPay(System.currentTimeMillis()+"", "1","","151116599931184","IKyLYYKDihsJH82H6Pn5hsB2O38xBCCl",System.currentTimeMillis()+"");
//		query("DD201711131523451206317", "151029822977356", "oGyUVU1Pae3NN1kGsMOlvQMDKcmbBisj");
	}
}
