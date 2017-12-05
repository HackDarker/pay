package com.pay.business.util.shande;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.pay.business.util.shande.Shande.Body;
import com.pay.business.util.shande.Shande.Head;

/**
 * Title: 杉德支付
 * Description: 
 * @author yy
 * 2017年9月25日
 */
public class ShandePay {
	
	/**
	 * @param mid
	 * @param plMid
	 * @param orderCode
	 * @param totalAmount
	 * @param payType 1支付宝、2微信
	 * @return
	 */
	public static Map<String, String> shandePay(String mid, String plMid, String orderCode
			, String totalAmount, String payType,String privateKey,String pubKey,String subject){
		System.out.println("------------衫德扫码下单------------");
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		Head head = new Head();
		head.setMid(mid);
//		head.setPlMid(plMid);
		head.setMethod(ShandeConfig.QR_ORDERCREATE);
		Body body = new Body();
		body.setOrderCode(orderCode);
		body.setSubject(subject);
		body.setNotifyUrl(ShandeConfig.NOTIFYURL);
		body.setTotalAmount(getTotalAmount(totalAmount));
		if("2".equals(payType)){
			head.setProductId(ShandeConfig.WXPAY_PRODUCTID);
			body.setPayTool(ShandeConfig.WXPAY_PAYTOOL);
		}else if ("1".equals(payType)) {
			head.setProductId(ShandeConfig.ALIPAY_PRODUCTID);
			body.setPayTool(ShandeConfig.ALIPAY_PAYTOOL);
		}
		JSONObject json = (JSONObject) JSONObject.toJSON(new Shande(head, body));
		String data = json.toString();
		try {
			String respData = ShandeSign.execute(data, ShandeConfig.QR_URL, privateKey,pubKey);
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject headObject = respJson.getJSONObject("head");
			String respCode = headObject.getString("respCode");
			if("000000".equals(respCode)){
				JSONObject bodyObject = respJson.getJSONObject("body");
				String qrCode = bodyObject.getString("qrCode");
				result.put("code", "10000");
				result.put("webStr", qrCode);
			}else{
				result.put("msg", headObject.getString("respMsg")+"(上游问题)");
			}
		} catch (Exception e) {
			result.put("msg", "衫德下单异常");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @param mid
	 * @param plMid
	 * @param orderCode
	 * @param totalAmount
	 * @param payType 1支付宝、2微信
	 * @return
	 */
	public static Map<String, String> wapPay(String mid, String orderCode, String totalAmount
			, String payType,String privateKey,String pubKey){
		System.out.println("------------衫德wap下单------------");
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		Head head = new Head();
		head.setMid(mid);
		head.setMethod(ShandeConfig.ORDERCREATE);
		Body body = new Body();
		body.setOrderCode(orderCode);
		body.setSubject(orderCode);
		body.setNotifyUrl(ShandeConfig.NOTIFYURL);
		body.setTotalAmount(getTotalAmount(totalAmount));
		if("2".equals(payType)){
			head.setProductId(ShandeConfig.WXPAY_PRODUCTID);
			body.setPayTool(ShandeConfig.WXPAY_PAYTOOL);
		}else if ("1".equals(payType)) {
			head.setProductId(ShandeConfig.ALIPAY_PRODUCTID);
			body.setPayTool(ShandeConfig.ALIPAY_PAYTOOL);
		}
		JSONObject json = (JSONObject) JSONObject.toJSON(new Shande(head, body));
		String data = json.toString();
		try {
			String respData = ShandeSign.execute(data, ShandeConfig.URL, privateKey,pubKey);
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject headObject = respJson.getJSONObject("head");
			String respCode = headObject.getString("respCode");
			if("000000".equals(respCode)){
				JSONObject bodyObject = respJson.getJSONObject("body");
				String qrCode = bodyObject.getString("credential");
				result.put("code", "10000");
				result.put("webStr", qrCode);
			}else{
				result.put("msg", headObject.getString("respMsg")+"(上游问题)");
			}
		} catch (Exception e) {
			result.put("msg", "衫德下单异常");
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 查询订单
	 * @param orderNum
	 * @param payType	1支付宝、2微信
	 * @param mid
	 * @param privateKey
	 * @param pubKey
	 * @return
	 */
	public static Map<String,String> query(String orderNum,String payType,String mid,String privateKey,String pubKey){
		System.out.println("------------衫德查询订单------------");
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		Head head = new Head();
		head.setMid(mid);
		head.setMethod(ShandeConfig.QR_ORDERQUERY);
		if("2".equals(payType)){
			head.setProductId(ShandeConfig.WXPAY_PRODUCTID);
		}else if ("1".equals(payType)) {
			head.setProductId(ShandeConfig.ALIPAY_PRODUCTID);
		}
		Body body = new Body();
		body.setOrderCode(orderNum);
		JSONObject json = (JSONObject) JSONObject.toJSON(new Shande(head, body));
		String data = json.toString();
		try {
			String respData = ShandeSign.execute(data, ShandeConfig.QEURY_URL, privateKey,pubKey);
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject headObject = respJson.getJSONObject("head");
			String respCode = headObject.getString("respCode");
			if("000000".equals(respCode)){
				JSONObject bodyObject = respJson.getJSONObject("body");
				String orderStatus = bodyObject.getString("orderStatus");
				if("00".equals(orderStatus)||"04".equals(orderStatus)||"05".equals(orderStatus)){
					result.put("code", "10000");
				}else{
					result.put("msg", bodyObject.getString("orderMsg"));
				}
			}else{
				result.put("msg", headObject.getString("respMsg"));
			}
		}catch (Exception e) {
			result.put("msg", "衫德查询订单异常");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 退款
	 * @param orderNum
	 * @param payType	1支付宝、2微信
	 * @param mid
	 * @param privateKey
	 * @param pubKey
	 * @return
	 */
	public static Map<String,String> refund(String refundNum,String orderNum,String refundAmount
			,String payType,String mid,String privateKey,String pubKey){
		System.out.println("------------衫德查询订单------------");
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		Head head = new Head();
		head.setMid(mid);
		head.setMethod(ShandeConfig.QR_ORDERREFUND);
		if("2".equals(payType)){
			head.setProductId(ShandeConfig.WXPAY_PRODUCTID);
		}else if ("1".equals(payType)) {
			head.setProductId(ShandeConfig.ALIPAY_PRODUCTID);
		}
		Body body = new Body();
		body.setOrderCode(refundNum);
		body.setOriOrderCode(orderNum);
		body.setRefundAmount(getTotalAmount(refundAmount));
		body.setNotifyUrl(ShandeConfig.NOTIFYURL);
		body.setNotifyUrl("https://testpayapi.aijinfu.cn/payDetail/sdCallBack.do");
		JSONObject json = (JSONObject) JSONObject.toJSON(new Shande(head, body));
		String data = json.toString();
		try {
			String respData = ShandeSign.execute(data, ShandeConfig.REFUND_URL, privateKey,pubKey);
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject headObject = respJson.getJSONObject("head");
			String respCode = headObject.getString("respCode");
			if("000000".equals(respCode)){
				JSONObject bodyObject = respJson.getJSONObject("body");
				if(bodyObject.containsKey("refundAmount")){
					result.put("code", "10000");
				}else{
					result.put("msg", "退款失败");
				}
			}else{
				result.put("msg", headObject.getString("respMsg"));
			}
		}catch (Exception e) {
			result.put("msg", "衫德查询订单异常");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 对账
	 * @param orderNum
	 * @param payType	1支付宝、2微信
	 * @param mid
	 * @param privateKey
	 * @param pubKey
	 * @return
	 */
	public static Map<String,String> download(String date,String payType,String mid,String privateKey,String pubKey){
		System.out.println("------------衫德查询订单------------");
		Map<String,String> result = new HashMap<>();
		result.put("code", "10001");
		Head head = new Head();
		head.setMid(mid);
		head.setMethod(ShandeConfig.QR_ORDERREFUND);
		if("2".equals(payType)){
			head.setProductId(ShandeConfig.WXPAY_PRODUCTID);
		}else if ("1".equals(payType)) {
			head.setProductId(ShandeConfig.ALIPAY_PRODUCTID);
		}
		Body body = new Body();
		body.setFileType("1");
		body.setClearDate(date);
		JSONObject json = (JSONObject) JSONObject.toJSON(new Shande(head, body));
		String data = json.toString();
		try {
			String respData = ShandeSign.execute(data, ShandeConfig.DOWNLOAD_URL, privateKey,pubKey);
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject headObject = respJson.getJSONObject("head");
			String respCode = headObject.getString("respCode");
			if("000000".equals(respCode)){
				JSONObject bodyObject = respJson.getJSONObject("body");
				/*if(bodyObject.containsKey("refundAmount")){
					result.put("code", "10000");
				}else{
					result.put("msg", "退款失败");
				}*/
			}else{
				result.put("msg", headObject.getString("respMsg"));
			}
		}catch (Exception e) {
			result.put("msg", "衫德查询订单异常");
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getTotalAmount(String totalAmount){
		String moneyStr = totalAmount.replace(".", "");
		int moneyStrLn = moneyStr.length();
		if(moneyStrLn<=12){
			for (int i = 0; i < 12-moneyStrLn; i++) {
				moneyStr = "0"+moneyStr;
			}
		}
		return moneyStr;
	}
	
	public static void main(String[] args) throws Exception {
		String privateKey ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmk7X845mgBmHe5cLZDsDbRZYBvjgtQzcA9cc127HvWWVIxkeKOgdv5XnRVDPNZlEPPj9hPxsrRBfGh/7rwO+h7BRPCDElaCROZ/EgY3d2gLUKoWVfFpI/D7z2D9B3Zo0yKZv3Qq2ost9mdyDpB7ZeIYtNC7d09yaJovCuU4mhQRBNIZzfEh9gRlQRTgLJ0ZgHZzd05YeUZD5oimO12t6NF7BTUTvk8rpptE6WeAj1EKVXR9k4Oq8GFL0pTxjl/8Fc/6PqsmLEZY/OpOQOcCmKhw8EKM5A93WcZlCk+NKuyeVlnAbwGdB5TyLFZ1CmsyU9r7nP4CQi9xoAshIJzpXrAgMBAAECggEASgpZ1+AqhrCPjA3AP86I9m3FsVylh6jap/iW5V+ThXnMxT4syknkyF3Nebfdlqyl1wqqDUWJg/+uObZ1HsERQn22wTI49hGGIbudt/G+RdQLpH/8mPllY9ZQ/WYmGQxiMivAgXUHz3ZCSj8lR67/5nMugb51eu3oGUFtjW7gO+YAyCtnzQHI9sWLpDoZWXtxf88SCncyCUtgLHpUaX6Qa4fcfDkk3CA01hgQBOP1ZMQud/8Jiey4OEqABlcYjNxkwnzNX22mbYA7uCcmMRecBaoQpT12z4Mw071FzwLnejU5QQFMJ35kX3k8ee6u9U2ugRDu0YQ2+IFwgbu6zdT9MQKBgQDoWGhbiyykYbYgjAu4N4iNbLvfU/r1nlBKANnL85AsyG6X1RNdfnLKt8Vh7uKNjwPJ5I+6r1nuBURTLoMQkzEWb2aZJuUTawhIuSMHGXEC+iB12t090qS9pJyO+/3RNU89mlZSp7ztAM5zof8/IrZuVp9xx1E40dzbUMRBrWDMLwKBgQC3iTCQXXYrYtUCWAUGFHdzt67TYNSqY1sAzDRoaOPEkvg5crb+41FeNnOJiHTSlrST6SK5X8g3TQlb0sQHBe/+73t61HAWCjrMtr7QokQyzblYXQEBNcMm2WFN1EhNaYx7jWGI9661MXrd5hcEEdusLKdH9YyTx7RPf10D1Bu3BQKBgQCVCRB3mABcuOiQXASwd765qMOh70aNAq3RqlYKQwN6Fl8KOX0gfZm4esS7mvXrg48Uk+21/ACdcvduu+kKWsAj+fU/y/koyYEY1NipFyKsnDDyKB++Jj3R/6ME/JLE/YMEqtkwT7QZ+mhzN7h4kRWgFSRVYVqyLbuTWm1pC4OJtQKBgC5NqDjju2pats6ukiXkh/ZjwvGFHmIWSuE6o4Yr4Dq5sXIc5NoNU3vnVOK/0GlIqCogySfnaEW7Y78Mv3PeJZw1ovtsGRMWZZs8B4vvRDFlAXuviw9ZFvD4KrpN8ifFlzGci5zYj/UalJri76+RKdCYO2PGKT8nRPBBe5/5pTxZAoGBAKH/X3TVEkOHMFZ0pwXHdthpNFHAKJykKb0rk8L4ej9JZnW8G/vYWYv2UpMFWdQ2/iIGMRWI9mdT/ruiXV0UFNBQrhVZqxiZAjPfQ83VQSVvU3+nE2uaDG1g4jDo+0DdzXjX0Y1r+PBhDdUVahsdI2SCQI/YbhuHKyZfcvaJYXUD";
		String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyIwo8Jq6XiUSY8cMrDfTRb65QaWcPH2hITZrei3jgLIdHP3kTQjZueWhp2nQ7H9s6nD99MYSydB4YKZ5qVAoVxuwRE1fnNKOx8M3npIcr/JKtvCN5TrE1XIUyxWG3F7sPbsafN+7Gwxqh5gT4/u/zq5busBztvXh+/woiqi3EGQ1WO9+P4AtYA6nr3KoVU7hdO8Aj+6aXMjQQTtDrgH/oiAHkEMJfrQmZ6irdnxzRwQ53D/GzVieAqME/sUMeIBWiy/Uj7d2TVJZkLLlC76lg6AVo/z9Wl26T0wyttxlCzjfZt1naT3B5IIp8k6lYrOdj3SX1gMD3ej0NGnnrQuuvwIDAQAB";
		String mid = "16710885";
		String pid="P57440";
		shandePay(mid, pid, System.currentTimeMillis()+"", "0.01", "2", privateKey,pubKey,System.currentTimeMillis()+"");
//		query("DD20171111114610535192720","2", mid, privateKey, pubKey);
//		refund(System.currentTimeMillis()+"","DD20171111114610535192720","0.01","2", mid, privateKey, pubKey);
//		download("20171109", "2", mid, privateKey, pubKey);
	}
}
