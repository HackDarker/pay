package com.pay.business.util.pfSZ;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import org.bouncycastle.util.encoders.Base64;

import com.alibaba.fastjson.JSONObject;
import com.core.teamwork.base.util.properties.PropertiesUtil;
import com.pay.business.util.tianxia.TianxiaPay;

/**
 * Title: 浦发深圳
 * Description: 
 * @author yy
 * 2017年9月7日
 */
public class PFSZBankPay {
	private static Logger log = Logger.getLogger(PFSZBankPay.class);
	
	/**
	 * 支付
	 * @param orderNo 商户订单号
	 * @param agentId 代理商号
	 * @param merNo 商户号
	 * @param subMchId 二级商户号
	 * @param clientIp 商户IP
	 * @param transAmt 交易金额(分)
	 * @param payType 1、微信扫码，2、微信H5，3、支付宝扫码
	 * @param privateKey 加签用
	 * @param publicKey 验签用
	 * @return
	 */
	public static Map<String,String> pfszPay(String orderNo, String agentId, String merNo, String subMchId, 
			String clientIp, String transAmt, String payType, String publicKey, String privateKey,String commodityName){
		Map<String, String> resultMap = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			resultMap.put("code", "10001");
			param.put("requestNo", orderNo);
			param.put("version", PFSZConfig.VERSION);
			if("1".equals(payType)){
				param.put("productId", PFSZConfig.PRODUCTID_WX_SCAN);
				param.put("returnUrl", PFSZConfig.NOTIFYURL);	//页面通知地址
				param.put("transId", PFSZConfig.TRANID_SCAN);
			}else if("2".equals(payType)){
				param.put("productId", PFSZConfig.PRODUCTID_WX_H5);
				param.put("transId", PFSZConfig.TRANID_H5);
			}else if("3".equals(payType)){
				param.put("productId", PFSZConfig.PRODUCTID_ALI_SCAN);
				param.put("returnUrl", PFSZConfig.NOTIFYURL);	//页面通知地址
				param.put("transId", PFSZConfig.TRANID_SCAN);
			}else if("4".equals(payType)){
				param.put("productId", PFSZConfig.PRODUCTID_WX_APP);
				param.put("transId", PFSZConfig.TRANID_APP);
			}
			
			param.put("clientIp", clientIp);
			param.put("agentId", agentId);
			param.put("merNo", merNo);
			param.put("subMchId", subMchId);
			param.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
			param.put("orderNo", orderNo);
			param.put("transAmt", transAmt);
			param.put("commodityName", commodityName);
			param.put("notifyUrl", PFSZConfig.NOTIFYURL);	//异步通知地址
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳支付参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳支付返回：" + doPost);
			
			//resultMapScan(doPost, publicKey, resultMap);
			
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			//验签
			if(verify(map, publicKey)){
				if("0000".equals(map.get("respCode").toString())){
					resultMap.put("code", "10000");
					if(map.containsKey("payInfo")){
						resultMap.put("qr_code", new String(Base64.decode(map.get("payInfo").toString())));
					}
					if(map.containsKey("codeUrl")){
						resultMap.put("qr_code", new String(Base64.decode(map.get("codeUrl").toString())));
					}
					if(map.containsKey("formfield")){
						resultMap.put("qr_code", new String(Base64.decode(map.get("formfield").toString())));
					}
				}else {
					resultMap.put("respCode", map.get("respCode").toString());
					resultMap.put("msg", map.get("respDesc").toString());
					log.error("=====>浦发深圳支付失败原因:" + map.get("respCode") + "+++++++++++" + map.get("respDesc")+"(上游问题)");
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳支付异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 订单查询
	 * @param orderNo 商户订单号
	 * @param agentId 
	 * @param merNo
	 * @param orderDate
	 * @param privateKey
	 * @param publicKey
	 * @return
	 */
	public static Map<String,String> queryOrder(String orderNo, String agentId, String merNo, String orderDate, String publicKey, String privateKey) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("code", "10001");
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("requestNo", orderNo);
			param.put("version", PFSZConfig.VERSION2);
			param.put("transId", PFSZConfig.TRANID_ORDER);
			param.put("agentId", agentId);
			param.put("merNo", merNo);
			param.put("orderDate", orderDate);
			param.put("orderNo", orderNo);
			param.put("requestNo", orderNo);
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳订单查询参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳订单查询返回：" + doPost);
			
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			//验签
			if(verify(map, publicKey)){
				if("0000".equals(map.get("origRespCode"))){
					resultMap.put("code","10000");
					resultMap.put("out_trade_no", orderNo);
					resultMap.put("total_fee", map.get("transAmt")+"");
					resultMap.put("transaction_id", map.get("orderId")+"");
					resultMap.put("msg", map.get("origRespDesc")+"");
				}else {
					resultMap.put("msg", map.get("respDesc")+"");
					resultMap.put("respCode", map.get("0000")+"");
					log.error("=====>浦发深圳订单查询失败原因:" + map.get("respCode") + "+++++++++++" + map.get("respDesc"));
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳订单查询异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 余额查询
	 * @param requestNo
	 * @param agentId
	 * @param merNo
	 * @param privateKey
	 * @param publicKey
	 * @return
	 */
	public static Map<String,String> queryBalance(String requestNo, String agentId, String merNo, String publicKey, String privateKey) {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("code", "10001");
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("requestNo", requestNo);
			param.put("version", PFSZConfig.VERSION);
			param.put("transId", PFSZConfig.TRANID_BALANCE);
			param.put("agentId", agentId);
			param.put("merNo", merNo);
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			
			log.info("浦发深圳余额查询参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳余额查询返回：" + doPost);
			
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			if(verify(map, publicKey)){
				if("0000".equals(map.get("respCode"))){
					resultMap.put("code", "10000");
					resultMap.put("avaBal", map.get("avaBal")+"");	//可用余额
					resultMap.put("avaFreezeBal", map.get("avaFreezeBal")+"");	//可用冻结余额
					resultMap.put("cwcBal", map.get("cwcBal")+"");	//已结算余额
					resultMap.put("cwcFreezeBal", map.get("cwcFreezeBal")+"");	//已结算冻结余额
				}else {
					resultMap.put("msg", map.get("respDesc")+"");
					resultMap.put("respCode", map.get("0000")+"");
					log.error("=====>浦发深圳余额查询失败原因:" + map.get("respCode") + "+++++++++++" + map.get("respDesc"));
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳余额查询异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 退款
	 * @param orderNo 退款申请订单号
	 * @param agentId 代理商号
	 * @param merNo 商户号
	 * @param origOrderDate 原商品订单的日期
	 * @param origOrderNo 原交易商户订单号
	 * @param money 退款金额
	 * @param privateKey
	 * @param publicKey
	 * @return
	 */
	public static Map<String,String> refund(String orderNo, String agentId, String merNo, String origOrderDate, String origOrderNo, 
			 String money, String publicKey, String privateKey){
		Map<String, String> resultMap = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("requestNo", orderNo);
			param.put("version", PFSZConfig.VERSION);
			param.put("transId", PFSZConfig.TRANID_REFUND);
			param.put("agentId", agentId);
			param.put("merNo", merNo);
			param.put("merNo", merNo);
			param.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
			param.put("orderNo", orderNo);
			param.put("origOrderDate", origOrderDate);
			param.put("origOrderNo", origOrderNo);
			param.put("transAmt", money);
			param.put("refundReson", orderNo);
			param.put("returnUrl", PFSZConfig.NOTIFYURL);
			param.put("notifyUrl", PFSZConfig.NOTIFYURL);
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳退款参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳退款返回：" + doPost);
			
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			//验签
			if(verify(map, publicKey)){
				if("0000".equals(map.get("respCode").toString())){
					resultMap.put("code", "10000");
				}else {
					resultMap.put("respCode", map.get("respCode").toString());
					resultMap.put("msg", map.get("respDesc").toString());
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳退款异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 对账
	 * @param requestNo
	 * @param agentId 代理商号
	 * @param merNo 商户号(可不传)
	 * @param orderDate (yyyyMMdd)
	 * @param privateKey
	 * @param publicKey
	 * @return
	 */
	public static String[] pfszOrderFind(String requestNo, String agentId, String merNo, String orderDate, String publicKey, String privateKey) {
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("requestNo", requestNo);
			param.put("version", PFSZConfig.VERSION);
			param.put("transId", PFSZConfig.TRANID_DZ);
			param.put("agentId", agentId);
			//param.put("merNo", merNo);
			param.put("orderDate", orderDate);
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳对账参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳对账返回：" + doPost);
			
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			//验签
			if(verify(map, publicKey)){
            	Gzip zip = new Gzip();
            	byte[] unzip = zip.unzip(Base64.decode(map.get("fileContentDetail").toString()));
            	//System.out.println(new String(unzip));
            	return new String(unzip).split("\n");
			}else {
				log.error("msg：验签失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("浦发深圳对账异常：" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 微信报备商户
	 * @param agentId 代理商号
	 * @param merNo 商户号
	 * @param subMechantName 二级商户名称
	 * @param subMerchantShortname 二级商户简称
	 * @param contact 联系人
	 * @param contactPhone 联系人电话
	 * @param contactEmail 联系人邮箱
	 * @param merchantRemark 二级商户备注
	 * @param servicePhone 联系电话
	 * @param privateKey 私钥
	 * @param publicKey 公钥
	 * @return
	 */
	public static Map<String,String> wx_Baobei(String agentId, String merNo, String subMechantName, String subMerchantShortname, String contact, 
			String contactPhone, String contactEmail, String merchantRemark, String servicePhone, String publicKey, String privateKey){
		Map<String, String> resultMap = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			resultMap.put("code", "10001");
			param.put("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			param.put("version", "V1.1");
			param.put("transId", "18");
			param.put("merNo", merNo);
			param.put("agentId", agentId);
			param.put("subMechantName", subMechantName);
			param.put("subMerchantShortname", subMerchantShortname);
			param.put("contact", contact);
			param.put("contactPhone", contactPhone);
			param.put("contactEmail", contactEmail);
			param.put("merchantRemark", merchantRemark);
			param.put("servicePhone", servicePhone);
			param.put("payWay", "WX");
			param.put("business", "53");
			param.put("businessLicense", "911101055960674408");
			param.put("businessLicenseType", "NATIONAL_LEGAL");
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳微信报备商户参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳微信报备商户返回：" + doPost);
			System.out.println("浦发深圳微信报备商户返回：" + doPost);
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			
			//验签
			if(verify(map, publicKey)){
				if("0000".equals(map.get("respCode")+"")){
					resultMap.put("subMchId", map.get("subMchId")+"");
					resultMap.put("code", "10000");
				}else {
					resultMap.put("msg", map.get("respDesc")+"");
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳微信报备异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 支付宝报备商户
	 * @param contactMap 联系人信息
	 * @param agentId
	 * @param merNo
	 * @param subMechantName
	 * @param subMerchantShortname
	 * @param contact
	 * @param contactPhone
	 * @param contactEmail
	 * @param merchantRemark
	 * @param servicePhone
	 * @param privateKey
	 * @param publicKey
	 * @return
	 */
	public static Map<String,String> ali_Baobei(Map<String,String> contactMap, Map<String,String> address, Map<String,String> cardInfo, String agentId, String merNo, String subMechantName, String subMerchantShortname, String contact, 
			String contactPhone, String contactEmail, String merchantRemark, String servicePhone, String publicKey, String privateKey){
		Map<String, String> resultMap = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("requestNo", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
			param.put("version", "V1.1");
			param.put("transId", "18");
			param.put("merNo", merNo);
			param.put("agentId", agentId);
			param.put("subMechantName", subMechantName);
			param.put("subMerchantShortname", subMerchantShortname);
			param.put("contact", contact);
			param.put("contactPhone", contactPhone);
			param.put("contactEmail", contactEmail);
			param.put("merchantRemark", merchantRemark);
			param.put("servicePhone", servicePhone);
			param.put("payWay", "ALIPAY");
			param.put("business", "2015050700000000");
			param.put("contactInfo", JSONObject.toJSONString(contactMap));
			
			param.put("addressInfo", JSONObject.toJSONString(address));
			param.put("bankCardInfo", JSONObject.toJSONString(cardInfo));
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
			
			param.put("signature", RSAUtil.signByAgentID(signInfo, privateKey));
			log.info("浦发深圳支付宝报备商户参数：" + param.toString());
			String doPost = doPost(PFSZConfig.URL, param);
			log.info("浦发深圳支付宝报备商户返回：" + doPost);
			System.out.println("浦发深圳支付宝报备商户返回：" + doPost);
			Map<String, Object> map = TianxiaPay.toMap(doPost);
			
			//验签
			if(verify(map, publicKey)){
				if("0000".equals(map.get("respCode")+"")){
					resultMap.put("subMchId", map.get("subMchId")+"");
					resultMap.put("code", "10000");
				}else {
					resultMap.put("msg", map.get("respDesc")+"");
				}
			}else {
				resultMap.put("msg", "验签失败！");
			}
		} catch (Exception e) {
			log.error("浦发深圳支付宝报备异常：" + e);
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 验签
	 * @param map
	 * @param publicKey
	 * @return
	 */
 	public static boolean verify(Map<String, Object> map, String publicKey){
		TreeMap<String, Object> dopostResult = new TreeMap<String, Object>(map);
		dopostResult.remove("signature");
		String signInfo = "";
		for (String pkey : dopostResult.keySet()) {
			signInfo += pkey+"="+dopostResult.get(pkey)+"&";
		}
		signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
		return RSAUtil.verify(signInfo, map.get("signature").toString(), publicKey, "UTF-8");
	}
	
 	
 	public static void main(String[] args) throws Exception {
//		String privateKey = "MIIEowIBAAKCAQEAxYO+7H+kGDpw8YD7Xwj3dCoklYETPLN1UG1Yt20X7DyrA8w+xC9ws5d23HJJ+/YEH89sME5vVLghQFxoVUZz6NDnVI0IclfEsH0BAFp8Xl1b3ExURYj4lT+a6mNho6Ui+ouiRb1XzW57P6srhKyW7t1wms7c+lgPsXAr5b2KnRVoHWYyaCATLzcEhhr3Mm2WUCD6LJYyswv5NaTOzvshkWcYnZZUcXslhMa1A9A3vlJtPmH1ai3TRK0SwzC/mNe3d+dkYXd77Eyh5JwGFQFWXbxXjxaBEesY0NjjSUDmJmXvI7t5pD48FSOOJQMVTrEACsaZO5hc3dAIaBBFzKIcfQIDAQABAoIBABfESJ8QpOA9eAW3bYf7/jq+L3TF+Vieh4lL/xbjS7OjgTiNxSe6Rad2nFjeb8Sfz9M8FFqjtYXOOkISXIOWXLAxIwTri46mvQY3pH00Zi68sScLEEDlwHPFGZEGsGMOpezcDISzyfLwTmhU4oGueuL3Rmt6ZODC4/CH/OBCNIG+MZFODVt2E8G9/W2z1dcFGo6tjxCCvlYNJ0OhRCsoJyKiKSQmJAk5O7MMVU1+jPvP3x6nCu+Yy9cnr9CH86M2jRafZ3HOKh5Jc7ymDqQwRMsgRqh9TrX6QDuVkJSn12e/hfjuYZQAWSigHjTLLMGjS0yIzwX8N5fSyiT7z8TJF8ECgYEA4RMTu5DQd6No6CX399hd8UfokQHapPpgRezDzIk1kAp25GeSOWkw8HSdlOjObV2/sYmz1LnglzuRTR31oajVqV6ZVcR/2fpFK87GPiri3+U2RkXwM3aJGh88VqdWVzboqgjLQ4eI9A3rbbptepqeQf4Tb0o3KvKaDKqwtDLU1nUCgYEA4KdCNiBewrOtalxUfD12oTVnjgrLsUZKApkLWL05QXLmNnksB1DVxDbKQiHDk6IvTXIgaMy/ZCkyZkNlcVQd1VDuB9JZN7aSVeEOvkww/99D+w51V9LMNKbnytLbK0nmZJRFouoRodphIeFJqA02vtWTK4pNme14iGADJ+IZvOkCgYAJAFdQsAj2T+25IxOYsOmI5cRSUE2rPWwuP7rQ6kffG9wHZHD/pMpVQ4St2OWwkAhDlGtBvbFSuwojmGgjb/ojjOn6+SHX2N99Ugaxo8txAty50MA7fqkbB1bFbGnSkRqa+kEO0VPT1t6sg8EvHxHnN78VO1WbfRpWGVl5y3KhpQKBgF3CcMfWSrZH9yBk2H3hyRkPCOEncEvUYh8jcLDgiHzgT2R8vftvqUfy9gcTwGRlVAimkRAsI9TRvM8hYb0itjDJTg7Fo6a08+4Tt+uEMQ8ZR24IYsD8oW14G1VzGzW96gIgP8/2kNVUJyXUuMECgs6ypHGPj0Om8J86Mxb6LnPBAoGBAKwYXfmsYmwh4mRutOFMHsrs+4SK50ug32Cgakd+nUZx8/+WDm5O/C4ZjRtOIu2wC2a+5dxy56vCJnwwSXt3V9mnRupV9B72S0PJheE54WjdSJhrwASGpnG4k6dmk0pskTSqCEGiNtsHDElNeytPWVoB+HA/sQZMIERUJCi9+LoX";
//		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxYO+7H+kGDpw8YD7Xwj3dCoklYETPLN1UG1Yt20X7DyrA8w+xC9ws5d23HJJ+/YEH89sME5vVLghQFxoVUZz6NDnVI0IclfEsH0BAFp8Xl1b3ExURYj4lT+a6mNho6Ui+ouiRb1XzW57P6srhKyW7t1wms7c+lgPsXAr5b2KnRVoHWYyaCATLzcEhhr3Mm2WUCD6LJYyswv5NaTOzvshkWcYnZZUcXslhMa1A9A3vlJtPmH1ai3TRK0SwzC/mNe3d+dkYXd77Eyh5JwGFQFWXbxXjxaBEesY0NjjSUDmJmXvI7t5pD48FSOOJQMVTrEACsaZO5hc3dAIaBBFzKIcfQIDAQAB";
 		//e:50694756 s:41701662
// 		Map<String, String> pfszWXScan = pfszPay(System.currentTimeMillis()+"", "100003", "800440054111002", "50694756", 
//				"127.0.0.1", "1", "1", privateKey, publicKey);
//		System.out.println(pfszWXScan.toString());
 		String publicKey="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqFW6W24uFq4H/QE+BAJ4GDRRfQDcN9whveaiR3JIggEaopw20+2Vni98e/IhS04emKH9ngBITAvnYAvtSCuXnv1ZDOQpSiJP/viUGTh7qyAeIST0POvfZSJq8QADd4Jap3ExGqDTbAOoLOmq72kVsfDLq71mSMiPnHw51KPoa0u6Lyy9OK35UZcC/jvyOqbvKZXV3WofRmA/gawEKSlbENNVGonpPjn4ZHxm6EcYJAXPlAYQIT6Dc0cSYSRbkNwAkY7Op21EWq6u5Le2LIngnB3jzPMxYlhZFp3x3JN1O4pz2TKo8pUk96DlfPHvYFxtKSXPRVuBsPZrsPPXtH2K5wIDAQAB";
 		String privateKey = "MIIEowIBAAKCAQEAqFW6W24uFq4H/QE+BAJ4GDRRfQDcN9whveaiR3JIggEaopw20+2Vni98e/IhS04emKH9ngBITAvnYAvtSCuXnv1ZDOQpSiJP/viUGTh7qyAeIST0POvfZSJq8QADd4Jap3ExGqDTbAOoLOmq72kVsfDLq71mSMiPnHw51KPoa0u6Lyy9OK35UZcC/jvyOqbvKZXV3WofRmA/gawEKSlbENNVGonpPjn4ZHxm6EcYJAXPlAYQIT6Dc0cSYSRbkNwAkY7Op21EWq6u5Le2LIngnB3jzPMxYlhZFp3x3JN1O4pz2TKo8pUk96DlfPHvYFxtKSXPRVuBsPZrsPPXtH2K5wIDAQABAoIBAFb3647/LpqTERd6w9KIgAlpRkyB3EST227kMCUfeyyHa35lnMIDNlCCkhrrLp5cXtxWnNQ7qx526/QoVU/7DZubop1RZ6+gaJn/TWx2TRZNQqO3FuQ+rKzwijW23xOoajOl/EuYmYJtpT6G/sQwE4BaOq+g665gyCl92NbuU87SeON6R9VbbqIx3FHDwUt+HojfOLgzDkys9MKFJehIpfnf20ZXdwbxuiBVZiVDta/kiDy6pOJq7gYTlExgUbbdaV+3Sb0WQqVuz+qMWV2QjEB+V761BZ9cQEIdsQ6BrqDq4eJLntTwIRNslgXc1oCOKr2T1WUAX/BXQkOkLJvgk8kCgYEA8pUmJLjyzJDggPKDKE+tIGumDKh1DneGLpukKqGLTZIah0b56OSN2ZJV3WOBjzBnfmw8lqyILHzxCIR06OgJ52MWY5Lv1QD8emh1HTk41raGT6f8+V5UWYSb6lZQifeRKbkP6qaTWpPYb8CZr5AbfPtd3SC8Oe1jZ8sXms14uk0CgYEAsaVEcL9HAuJTCN9JYWF+yUC9YaTT/bgXi//WFN72dXOb3Dpp4I/NPls9Bt8CreOUk+hOxg+mHlbxaNFsNZUKcHwXfk3iPqOAN9jilXz/r6qVWLGWFMeVV4HAri1oxGYNyc2H0JEqZjxVYbQwDbCwoZQ7jsnL46lcDxqMR+kWzAMCgYAv+YFGp/EUtd5AgjWgJknF7KQ1QqeLyeWWmB9acwJav/dRI59PCMxo6ADlPMWlrNv666r73Xk82yTMG24HlcRHp5gI70lRJdXI25m1wthT1lsvq7hjFN23qnsvWAyrjHN//eKA9JqVwkF+sGd0ihp1mDzS+6NeDW2oXEj5y4MBzQKBgF8IDTPPb3qAfnEJmeTJVhztUCJTHcDl4VrYcrrZh7jPLrrCWO9znhVUk7IATRue2lXBeZqEtpYWZrBvD9ceMuZ3y82adoSRzL6TfKsBonpj4979qU7WcfLNC7lhu2ENQyUImTYe4jKs5fQNg4NfWS4m8TFyjGrup7vAtPinHbXnAoGBAMXLiv/1j8tuHcta+6tWWYqa39psuhcD0yEWoCG3SeVgUNgm+fEz7APiW0TF1IcKMDl91pgttgI5kGjp/9HPJcY8umOcmkFzsr/H0MGtLjpkiYcqm7UBp/YH1t9AyRhFwL74GHudr5UbosekOv+/eBV5phVUqqjaEIWPOosfoKbB";
 		Map<String, String> pfszWXScan = pfszPay(System.currentTimeMillis()+"", "440451", "310440300113908", "64687402", 
				"113.116.89.229", "100", "2", publicKey, privateKey,System.currentTimeMillis()+"");
 		/*Map<String, String> pfszWXScan = pfszPay(System.currentTimeMillis()+"", "440451", "310440300057885", "310440300057885", 
				"127.0.0.1", "1", "1", privateKey, publicKey);
		System.out.println(pfszWXScan.toString());*/
 		/*Map<String, String> queryOrder = queryOrder("1504919910990", "100003", "800440054111002", "20170909", privateKey, publicKey);
		System.out.println(queryOrder.toString());*/
 		/*Map<String, String> queryBalance = queryBalance(System.currentTimeMillis()+"", "100003", "800440054111002", privateKey, publicKey);
		System.out.println(queryBalance);*/
 		//refund(System.currentTimeMillis()+"", "100003", "800440054111002", "20170909", "1504919910990", "1", privateKey, publicKey);
 		//Map<String, String> resultMapScan = resultMapScan("agentId=100003&clientIp=127.0.0.1&codeUrl=d2VpeGluOi8vd3hwYXkvYml6cGF5dXJsP3ByPVNSRUNlc0Y=&commodityName=1504856737115&imgUrl=http://121.201.111.67:9080/payment-gate-web/gateway/api/getQRCodeImage?d2VpeGluOi8vd3hwYXkvYml6cGF5dXJsP3ByPVNSRUNlc0Y=&merNo=800440054111002&notifyUrl=http://yangyipay.tunnel.echomod.cn/payDetail/test.do&orderDate=20170908&orderNo=1504856737115&productId=0108&requestNo=1504856737115&respCode=0000&respDesc=交易成功&returnUrl=http://yangyipay.tunnel.echomod.cn/payDetail/test.do&subMchId=41701662&transAmt=1&transId=10&version=V1.1&signature=eyCSmnhBi8mvWxdHE3EvQ1hXfiro1MG3IMUA+Zj7RxYcDSpsXsXf31RC65GaGj6oPNqGo46S/hhwrJ8v/tPWbt0segcQfYPyl0TGVbATm/ksf7e7Da/jm91zp/85vWh3OWOG0StJY+6GNKM8ETYeW3o+JtzoTWZbMO77bFqd94z0gRSDTpygVpJXPKCNprab9GMhLEJan5O7H82b32Xqv/oYSqjweIqpxjjei2AkBOSuVgXCvF/QRoR9GVzE27spcXQhNqVv0Rd6AEiHAQxBd40xtjm3YXrxTAnXmVZZ0X6yXJmCLxD6f9T/bOKeAz667u7n5Hw3Vrlihw91pSaamA==", publicKey);
 		/*String[] pfszOrderFind = pfszOrderFind(System.currentTimeMillis()+"AA", "440451", null, "20170920", publicKey, privateKey);
 		for (String str : pfszOrderFind) {
			System.out.println(str);
		}*/
 		/*Map<String, String> wx_Baobei = wx_Baobei("440451", "310440300057885", "北京疯狂体育产业管理有限公司", "疯狂体育", "彭锡涛", "18001161392", "changwei@fengkuang.cn", "疯狂体育", "18001161392", privateKey, publicKey);
 		System.out.println(wx_Baobei.toString());*/
 		/*Map<String,String> contact = new HashMap<>();
        contact.put("name", "彭锡涛");//姓名  必填
        contact.put("mobile", "18001161392");//手机 选填
        contact.put("email", "changwei@fengkuang.cn");//邮件 选填
        contact.put("type", "LEGAL_PERSON");//联系人类型 取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他   必填
        contact.put("id_card_no", "132322197806152271");//身份证号 必填
        
        //**地址信息*//*
        Map<String,String> address = new HashMap<>();
        address.put("province_code", "110000");//省级编码 必填
        address.put("city_code", "110100");//市级编码 必填
        address.put("district_code", "110105");//区县编码 必填
        address.put("address", "广顺南大街16号院1号楼13层");//详细地址 必填

        //**结算信息*//*
        Map<String,String> cardInfo = new HashMap<>();
        cardInfo.put("card_no", "10234000000097630");//卡号 必填
        cardInfo.put("card_name", "北京疯狂体育产业管理有限公司");//户名 必填
 		
 		Map<String, String> ali_Baobei = ali_Baobei(contact, address, cardInfo, "440451", "310440300057885", "北京疯狂体育产业管理有限公司", "疯狂体育", "彭锡涛", "18001161392", "changwei@fengkuang.cn", "疯狂体育", "18001161392", privateKey, publicKey);
 		System.out.println(ali_Baobei.toString());*/
 		/*Map<String, String> pfszPay = pfszPay("AA"+System.currentTimeMillis(), "440451", "310440300057885", "53663881", "183.15.89.51", "101", "2", publicKey, privateKey);
 		System.out.println(pfszPay.toString());*/
 		
 		/*Map<String, String> refund = refund("BB"+System.currentTimeMillis(), "440451", "310440300057885", "20170920", "DD20170920094130516845629", "100", publicKey, privateKey);
 		System.out.println(refund.toString());*/
 	}

	public static String doPost(String url,TreeMap<String, String> param) throws Exception{
		String result=null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpPost httpPost = new HttpPost(url);
		if (param != null) {
			// 设置2个post参数
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			for (String key : param.keySet()) {
				parameters.add(new BasicNameValuePair(key, (String) param
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
