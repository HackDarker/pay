package com.pay.business.util.wukaPay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.httpsUtil.HttpsUtil;

public class WukaPay {
	
	/**
	 * 扫码支付
	 * @param orderNum
	 * @param payMoney
	 * @param platfrom
	 * @param mchId
	 * @param key
	 * @return
	 */
	public static Map<String,String> pay(String orderNum,String payMoney,String platfrom
			,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		try {
		
			Map<String,Object> param = new HashMap<>();
			//交易请求流水号
			param.put("requestNo", orderNum);
			//版本号
			param.put("version", "V1.0");
			//产品类型  0101：微信扫码；0102：支付宝扫码；0103：QQ扫码
			if("1".equals(platfrom)){
				param.put("productId", "0102");
			}else if("2".equals(platfrom)){
				param.put("productId", "0101");
			}else if("3".equals(platfrom)){
				param.put("productId", "0103");
			}
			//交易类型
			param.put("transId", "01");
			//商户号
			param.put("merNo", mchId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//订单日期
			param.put("orderDate", sdf.format(new Date()));
			//商户订单号
			param.put("orderNo", orderNum);
			//服务器异步地址
			param.put("notifyUrl", WukaConfig.notify);
			//交易金额
			param.put("transAmt", DecimalUtil.yuanToCents(payMoney));
			//商品名称
			param.put("commodityName", orderNum);
			//验签字段
			param.put("signature", WukaSign.getSign(param, key));
			
			System.out.println("无卡支付请求参数:"+param.toString());
			
			String reusltStr = HttpsUtil.doPostString(WukaConfig.payUrl, param, "utf-8");
			System.out.println("无卡支付返回参数:"+reusltStr);
			JSONObject json = JSONObject.parseObject(reusltStr);
			if("Z000".equals(json.getString("respCode"))){
				if(json.getString("signature").equals(WukaSign.getPaySign(json, key))){
					result.put("code", "10000");
					result.put("webStr", json.getString("codeUrl"));
				}else{
					result.put("msg", "返回参数验签失败");
				}
			}else{
				result.put("msg", json.getString("respDesc")+"(上游问题)");
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", "无卡支付下单错误");
		}
		return result;
	}
	
	/**
	 * h5支付
	 * @param orderNum
	 * @param payMoney
	 * @param platfrom
	 * @param mchId
	 * @param key
	 * @return
	 */
	public static Map<String,String> h5Pay(String orderNum,String payMoney,String platfrom
			,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		try {
		
			Map<String,Object> param = new HashMap<>();
			//交易请求流水号
			param.put("requestNo", orderNum);
			//版本号
			param.put("version", "V1.0");
			//产品类型  0101：微信扫码；0102：支付宝扫码；0103：QQ扫码
			if("1".equals(platfrom)){
				param.put("productId", "0302");
			}else if("2".equals(platfrom)){
				param.put("productId", "0301");
			}
			//交易类型
			param.put("transId", "01");
			//商户号
			param.put("merNo", mchId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//订单日期
			param.put("orderDate", sdf.format(new Date()));
			//商户订单号
			param.put("orderNo", orderNum);
			//前端同步地址
			param.put("frontUrl", WukaConfig.returnUrl);
			//服务器异步地址
			param.put("notifyUrl", WukaConfig.notify);
			//交易金额
			param.put("transAmt", DecimalUtil.yuanToCents(payMoney));
			//商品名称
			param.put("commodityName", orderNum);
			//验签字段
			param.put("signature", WukaSign.getSign(param, key));
			
			String webStr = WukaConfig.h5PayUrl+"?"+PaySignUtil.getParamStr(param);
			System.out.println("无卡h5支付请求链接:"+webStr);
			
			result.put("code", "10000");
			result.put("webStr", webStr);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", "无卡h5支付下单错误");
		}
		return result;
	}
	
	public static Map<String,String> query(String orderNum,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		try {
		
			Map<String,Object> param = new HashMap<>();
			//交易请求流水号
			param.put("requestNo", System.currentTimeMillis()+"");
			//版本号
			param.put("version", "V1.0");
			//交易类型
			param.put("transId", "04");
			//商户号
			param.put("merNo", mchId);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//订单日期
			param.put("orderDate", sdf.format(new Date()));
			//商户订单号
			param.put("orderNo", orderNum);
			//验签字段
			param.put("signature", WukaSign.getQuerySign(param, key));
			
			System.out.println("无卡查询请求参数:"+param.toString());
			
			String reusltStr = HttpsUtil.doPostString(WukaConfig.queryUrl, param, "utf-8");
			System.out.println("无卡查询返回参数:"+reusltStr);
			JSONObject json = JSONObject.parseObject(reusltStr);
			if("0000".equals(json.getString("respCode"))&&"0000".equals(json.getString("origRespCode"))){
				if(json.getString("signature").equals(WukaSign.getQuerySign(json, key))){
					result.put("code", "10000");
					result.put("msg", "查询支付成功");
				}else{
					result.put("msg", "返回参数验签失败");
				}
			}else{
				result.put("msg", json.getString("respDesc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", "无卡查询错误");
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		String mchId = "Z00000000001642";
		String key = "B037822F777B4B6D85221C33DB021E9A";
//		pay(System.currentTimeMillis()+"", "10.1", "3",mchId, key);
		
		h5Pay(System.currentTimeMillis()+"", "10.1", "2",mchId, key);
//		query("DD20171124164908534948945",mchId, key);
	}
}
