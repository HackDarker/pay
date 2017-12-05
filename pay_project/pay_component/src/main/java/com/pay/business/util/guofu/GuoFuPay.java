package com.pay.business.util.guofu;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.core.teamwork.base.util.encrypt.MD5;
import com.pay.business.util.xyShenzhen.XYSZBankPay;

public class GuoFuPay {
	private static Logger log = Logger.getLogger(GuoFuPay.class);
	
	private static Map<String, String> resultMap = null;
	
	/**
	 * 国付QQ被扫接口
	 * @param merchno 商户号
	 * @param amount 交易金额(元)
	 * @param traceno 商户流水号
	 * @param payType 支付方式(1支付宝、2微信、8-QQ)
	 * @param notifyUrl 通知地址(null)
	 * @param settleType 结算方式
	 * @param key 秘钥
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, String> passivePay(String merchno, String amount, String traceno, 
			String payType, String settleType, String key,String goodsName) {
		resultMap = new HashMap<String, String>();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merchno", merchno);
		param.put("amount", amount);
		param.put("traceno", traceno);
		param.put("payType", payType);
		param.put("notifyUrl", GuoFuConfig.NOTIFY_URL);
		//商品名称
		param.put("goodsName", goodsName);
		param.put("settleType", settleType);
		try {
			//签名加密
			param.put("signature", signature(param, key, "GBK"));
			
			System.out.println("param：" + param.toString());
			log.info("=====>国付QQ被扫提交参数：\n" + param.toString());
			String url = GuoFuConfig.BASE_URL + GuoFuConfig.PASSIVEPAY;
		
			String doPost = doPost(url, param);
			Map<String, String> maps = (Map)JSON.parse(doPost);
			log.info("=====>国付QQ被扫返回的预支付订单信息：\n" + param.toString());
			if("00".equals(maps.get("respCode"))){
				resultMap.put("qr_code", maps.get("barCode"));
				resultMap.put("code","10000");
				log.info("=====>国付QQ被扫支付调起成功:qr_code : " + maps.get("barCode"));
			}else {
				resultMap.put("code","10001");
				resultMap.put("msg", maps.get("message")+"(上游问题)");
				log.error("=====>国付QQ被扫支付失败原因:" + maps.get("message"));
			}
			
		} catch (Exception e) {
			resultMap.put("code","10001");
			resultMap.put("msg", e.getMessage());
			log.error("=====>国付QQ被扫支付失败原因:" + e.getMessage());
			e.printStackTrace();
		}
		
		return resultMap;
	 }
	
	/**
	 * 国付订单查询
	 * @param merchno 
	 * @param traceno 订单号
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> qrcodeQuery(String merchno, String traceno, String key) {
		resultMap = new HashMap<String, String>();
		Map<String, String> param = new HashMap<String, String>();
		param.put("merchno", merchno);
		param.put("traceno", traceno);
		try {
			//签名加密
			param.put("signature", signature(param, key, "GBK"));
			log.info("=====>国付订单查询参数：\n" + param.toString());
			String url = GuoFuConfig.BASE_URL + GuoFuConfig.QRCODEQUERY;
			String doPost = doPost(url, param);
			log.info("=====>国付订单查询结果：\n" + doPost);
			Map<String, String> maps = (Map)JSON.parse(doPost);
			if("1".equals(maps.get("respCode"))){
				resultMap.put("code","10000");
				resultMap.put("out_trade_no", traceno);
				resultMap.put("total_fee", maps.get("amount"));
				resultMap.put("transaction_id", maps.get("orderno"));
			}else {
				resultMap.put("code", "500");
				resultMap.put("msg", maps.get("message"));
				log.error("=====>国付订单查询失败原因:" + maps.get("message"));
			}
		} catch (Exception e) {
			resultMap.put("code", "500");
			log.error("=====>国付订单查询异常:" + e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 代付余额查询
	 * @param cardno 虚拟账户号
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> balanceQuery(String cardno, String key) {
		resultMap = new HashMap<String, String>();
		Map<String, String> param = new HashMap<String, String>();
		param.put("cardno", cardno);
		try {
			//签名加密
			param.put("signature", XYSZBankPay.signMd5(key, param));
			String url = GuoFuConfig.BALANCEQUERY;
			String doPost = doPost(url, param);
			log.info("=====>国付余额查询结果：\n" + doPost);
			Map<String, String> maps = (Map)JSON.parse(doPost);
			if("00".equals(maps.get("respCode"))){
				resultMap.put("code","10000");
				resultMap.put("money", maps.get("amount"));
			}else {
				resultMap.put("code","10001");
				resultMap.put("msg", maps.get("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("code","10001");
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 实时代付
	 * @param cardno 虚拟账号*
	 * @param traceno 商户流水号*
	 * @param amount 代付金额*
	 * @param accountno 结算账号*
	 * @param accountName 结算户名
	 * @param mobile 手机号码*
	 * @param bankno 联行号*
	 * @param bankName 支行名称
	 * @param bankType 银行类型
	 * @param remark 备注信息
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> virtPay(String cardno, String traceno, String amount, String accountno, String accountName, 
			String mobile, String bankno, String bankName, String bankType, String key){
		resultMap = new HashMap<String, String>();
		Map<String, String> param = new HashMap<String, String>();
		try {
			//cardno=xxx&traceno=xxx&amount=xxx&accountno=xxx&mobile=xxx&bankno=xxx&key=xxx
			param.put("cardno", cardno);
			param.put("traceno", traceno);
			param.put("amount", amount);
			param.put("accountno", accountno);
			param.put("mobile", mobile);
			param.put("bankno", bankno);
			String sign = "cardno=" + cardno + "&traceno=" + traceno + "&amount=" + amount + "&accountno=" + accountno + "&mobile=" + mobile + "&bankno=" + bankno + "&key=" + key;
			param.put("signature", MD5.GetMD5Code(sign).toUpperCase());
			param.put("accountName", accountName);
			param.put("bankName", bankName);
			param.put("bankType", bankType);
			param.put("remark", new String("实时提现".getBytes("GBK")));
			String doPost = doPost(GuoFuConfig.VIRTPAY, param);
			System.out.println(doPost);
			log.info("=====>国付实时代付结果：\n" + doPost);
			Map<String, String> maps = (Map)JSON.parse(doPost);
			System.out.println(maps.toString());
			if("00".equals(maps.get("respCode")) && "2".equals(maps.get("transStatus"))){
				resultMap.put("code","10000");
				resultMap.put("msg", "提现成功！");
			}else {
				resultMap.put("code","10001");
				resultMap.put("msg", maps.get("message"));
				resultMap.put("payMsg", maps.get("payMsg"));
				resultMap.put("transStatus", maps.get("transStatus"));
				resultMap.put("payStatus", maps.get("payStatus"));
			}
		} catch (Exception e) {
			resultMap.put("code","10001");
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
	}
	
	/**
	 * 代付查询
	 * @param cardno
	 * @param traceno 商户流水号
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, String> virtOrder(String cardno, String traceno, String key) {
		resultMap = new HashMap<String, String>();
		Map<String, String> param = new HashMap<String, String>();
		try {
			param.put("cardno", cardno);
			param.put("traceno", traceno);
			String sign = "cardno=" + cardno + "&traceno=" + traceno + "&key=" + key;
			param.put("signature", MD5.GetMD5Code(sign).toUpperCase());
			String doPost = doPost(GuoFuConfig.VIRTORDER, param);
			log.info("=====>国付代付查询结果：\n" + doPost);
			Map<String, String> maps = (Map)JSON.parse(doPost);
			if("00".equals(maps.get("respCode")) && "2".equals(maps.get("transStatus"))){
				resultMap.put("code","10000");
				resultMap.put("msg", "代付成功！");
				resultMap.put("money", maps.get("amount"));
			}else {
				resultMap.put("code","10001");
				resultMap.put("msg", maps.get("message"));
				if ("2".equals("transStatus")) {
					resultMap.put("transStatus", "代付失败！");
				}
				if ("1".equals("transStatus")) {
					resultMap.put("transStatus", "代付处理中！");
				}else if ("2".equals("transStatus")) {
					resultMap.put("transStatus", "付款成功！");
				}else if ("3".equals("transStatus")) {
					resultMap.put("transStatus", "付款失败！");
				}else if ("4".equals("transStatus")) {
					resultMap.put("transStatus", "付款异常！");
				}
			}
		} catch (Exception e) {
			resultMap.put("code","10001");
			resultMap.put("msg", e.getMessage());
		}
		return resultMap;
	}
	
	public static void main(String[] args) throws Exception {
		Map<String, String> passivePay = passivePay("820440348160002", "8.0", Long.toString(new Date().getTime()), "8", "1", "3B78D375CC78558A5BF71206C8A6DC90",Long.toString(new Date().getTime()));
		System.out.println(passivePay);
		//qrcodeQuery("820440348160001", "1503310296672", "FBCA4448139ADBFC1E61E9556331DBAA");
		/*Map<String, String> balanceQuery = balanceQuery("820044130200000010", "3B78D375CC78558A5BF71206C8A6DC90");
		System.out.println(balanceQuery);*/
		/*Map<String, String> virtPay = virtPay("820044130200000010", Long.toString(new Date().getTime()), "6.94", "6214837829176258", "杨逸", "18680306221", "308584001137", new String("招商银行深圳深南中路支行".getBytes("GBK")), new String("招商银行".getBytes("GBK")), "3B78D375CC78558A5BF71206C8A6DC90");
		System.out.println(virtPay.toString());*/
		/*Map<String, String> virtOrder = virtOrder("820044130200000005", "1503709175518", "FBCA4448139ADBFC1E61E9556331DBAA");
		System.out.println(virtOrder.toString());*/
	}
	
	public static String signature(Map<String, String> param, String keyInfo, String encoding) throws Exception {
		Set<String> set = param.keySet();
		List<String> keys = new ArrayList<String>(set);
		Collections.sort(keys);
		boolean start = true;
		StringBuffer sb = new StringBuffer();
		for (String key : keys) {
			String value = param.get(key);
			if (!key.equals("signature") && !isEmpty(value) ) {
				if (!start) {
					sb.append("&");
				}
				sb.append(key + "=" + value);
				start = false;
			}
		}
		sb.append("&");
		sb.append(keyInfo);
		String src = sb.toString();
		return getMD5ofStr(src, encoding).toUpperCase();
	}
	
	private static String getMD5ofStr(String str, String encoding) throws Exception {
		MessageDigest alga = MessageDigest.getInstance("MD5");
		byte[] b = str.getBytes(encoding);
		alga.update(b);
		byte[] digesta = alga.digest();
		return byte2hex(digesta);
	}
	
	private static String byte2hex(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}
	
	public static boolean isEmpty(Object obj){
		if(obj == null) return true;
		String str = obj.toString().trim();
		return "".equals(str) || "null".equalsIgnoreCase(str);
	}
	
	/**
	 * post传递多个参数
	 */
	public static String doPost(String url,Map<String, String> params) throws Exception{
		String result=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		if (params != null) {
			// 设置2个post参数
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				parameters.add(new BasicNameValuePair(key, (String) params
						.get(key)));
			}
			System.out.println("list：" + parameters);
			// 构造一个form表单式的实体
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, "GBK");
			// 将请求实体设置到httpPost对象中
			httpPost.setEntity(formEntity);
		}
		try {
			response = httpClient.execute(httpPost);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "GBK");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.close();
		}
		return result;
	}
}
