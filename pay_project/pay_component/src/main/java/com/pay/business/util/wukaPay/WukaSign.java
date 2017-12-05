package com.pay.business.util.wukaPay;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.core.teamwork.base.util.encrypt.MD5;

public class WukaSign {
	//支付签名
	public static String getSign(Map<String,Object> map,String key){
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("requestNo").toString());
		sb.append(map.get("productId").toString());
		sb.append(map.get("transId").toString());
		sb.append(map.get("merNo").toString());
		sb.append(map.get("orderNo").toString());
		sb.append(map.get("transAmt").toString());
		sb.append(key);
		return MD5.GetMD5Code(sb.toString());
	}
	
	//回调签名
	public static String getCallSign(Map<String,Object> map,String key){
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("merNo").toString());
		sb.append(map.get("orderNo").toString());
		sb.append(map.get("transAmt").toString());
		sb.append(map.get("respCode").toString());
		sb.append(map.get("payId").toString());
		sb.append(map.get("payTime").toString());
		sb.append(key);
		return MD5.GetMD5Code(sb.toString());
	}
	
	//支付返回参数签名
	public static String getPaySign(JSONObject json,String key){
		StringBuffer sb = new StringBuffer();
		sb.append(json.getString("requestNo"));
		sb.append(json.getString("productId"));
		sb.append(json.getString("transId"));
		sb.append(json.getString("merNo"));
		sb.append(json.getString("orderNo"));
		sb.append(json.getString("transAmt"));
		sb.append(json.getString("respCode"));
		sb.append(json.getString("codeUrl"));
		sb.append(key);
		return MD5.GetMD5Code(sb.toString());
	}
	
	//查询接口参数签名
	public static String getQuerySign(Map<String,Object> map,String key){
		StringBuffer sb = new StringBuffer();
		sb.append(map.get("requestNo").toString());
		sb.append(map.get("transId").toString());
		sb.append(map.get("merNo").toString());
		sb.append(map.get("orderNo").toString());
		sb.append(key);
		return MD5.GetMD5Code(sb.toString());
	}
	
	//查询返回参数签名
	public static String getQuerySign(JSONObject json,String key){
		StringBuffer sb = new StringBuffer();
		sb.append(json.getString("requestNo"));
		sb.append(json.getString("transId"));
		sb.append(json.getString("merNo"));
		sb.append(json.getString("orderNo"));
		sb.append(json.getString("origRespCode"));
		sb.append(json.getString("respCode"));
		sb.append(key);
		return MD5.GetMD5Code(sb.toString());
	}
}
