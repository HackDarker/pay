package com.pay.business.util.zsxmPay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.core.teamwork.base.util.encrypt.MD5;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.RandomUtil;
import com.pay.business.util.alipay.xyBank.XmlUtils;
import com.pay.business.util.xyBankWeChatPay.SslUtils;

public class ZsxmPay {
	
	/**
	 * 招商厦门微信支付
	 * @param orderNum 订单号
	 * @param payMoney	金额
	 * @param body	订单名称
	 * @param ip	ip
	 * @param payType	类型wap、gzh、scan
	 * @param appId		公众号使用，公众号appid
	 * @param openId	公众号使用，用户openid
	 * @param mchId		商户号
	 * @param key		密钥
	 * @return
	 */
	public static Map<String,String> pay(String orderNum,String payMoney,String body,String ip
			,String payType,String appId,String openId,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		try {
			Map<String, String> sginMap = new HashMap<String, String>();
	        String sign = "";
	        //商户号
	        sginMap.put("mch_id",mchId);
	        
	        //随机字符串
	        sginMap.put("nonce_str",new Date().getTime()+ RandomUtil.generateString(4));
	        // 商品描述
	        sginMap.put("body", body);
	        // 商户订单号
	        sginMap.put("out_trade_no", orderNum);
	        // 总金额，单位是分
	        sginMap.put("total_fee", DecimalUtil.yuanToCents(payMoney));
	        //客户端ip
	        sginMap.put("spbill_create_ip", ip);
	        
	        //异步回调URL
	        sginMap.put("notify_url", ZsxmConfig.notifyUrl);
	        
	        if("WAP".equals(payType)){
	        	//取值如下：JSAPI，NATIVE，APP，MWEB（H5支付）
	            sginMap.put("trade_type", "MWEB");
	            //trade_type=MWEB，此参数必传。WAP网站应用 {“h5_info”: {“type”:“Wap”,“wap_url”: “WAP网站URL地址”,“wap_name”: “WAP网站名”}}，IOS移动应用 {“h5_info”: {“type”:“IOS”,“app_name”: “应用名”,“bundle_id”: “包名”}}，安卓移动应用 {“h5_info”: {“type”:“Android”,“app_name”: “应用名”,“package_name”: “包名”}}。H5支付建议只在Wap场景上使用。
	            sginMap.put("scene_info", "{\"h5_info\": {\"type\":\"Wap\",\"wap_url\": \"https://www.aijinfu.cn/\",\"wap_name\": \"全民金服官网\"}}");
//	            sginMap.put("scene_info", "{“h5_info”: {“type”:“Wap”,“wap_url”: “WAP网站URL地址”,“wap_name”: “WAP网站名”}}");
	        }else if("GZH".equals(payType)){
	        	//取值如下：JSAPI，NATIVE，APP，MWEB（H5支付）
	            sginMap.put("trade_type", "JSAPI");
	            //trade_type=JSAPI，此参数必传，用户在子商户appid下的唯一标识。openid和sub_openid可以选传其中之一，如果选择传sub_openid,则必须传sub_appid。
	            sginMap.put("sub_openid", openId);
	            //商户微信公众号appid,app支付时,为在微信开放平台上申请的APPID
		        sginMap.put("sub_appid", appId);
	        }else if("SCAN".equals(payType)){
	        	//取值如下：JSAPI，NATIVE，APP，MWEB（H5支付）
	            sginMap.put("trade_type", "NATIVE");
	            //trade_type=NATIVE，此参数必传。此id为二维码中包含的商品ID，商户自行定义。
	            sginMap.put("product_id", "1234abc");
	        }
	        /*//no_credit–指定不能使用信用卡支付
	        sginMap.put("limit_pay", "no_credit");*/
	        
	        sign = signMd5(key, sginMap);
	        	
			sginMap.put("sign", sign);
			
			SortedMap<String, String> xmlMap = new TreeMap<String, String>(sginMap);
			String xmlInfo = parseXML(xmlMap);
			System.out.println("xml字符串："+xmlInfo);
			String info = sendPost(ZsxmConfig.wx_url, xmlInfo);
			System.out.println("=====>招行厦门支付返回的预支付订单信息:\n" + info + " \n");
			Map<String,String> resultMap = XmlUtils.toMap(info);
			System.out.println("=====>转换为map值为：" + resultMap);
			if (!ZsxmSignUtils.checkParam(resultMap,key)) {
				resultMap.put("code","10001");
				resultMap.put("msg","失败原因："+"验证签名不通过");
				System.out.println("=====>招行厦门支付调起失败原因：验证签名不通过");
            } else {
                if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                	result.put("code","10000");
                	if("WAP".equals(payType)){
                		
                	}else if("GZH".equals(payType)){
                		
                	}else if("SCAN".equals(payType)){
                		result.put("webStr",resultMap.get("code_url"));
                	}
                }else{
                	result.put("code","10001");
                	result.put("msg","失败原因："+resultMap.toString()+"(上游问题)");
                	System.out.println("=====>招行厦门支付调起失败原因："+resultMap);
                } 
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code","10001");
        	result.put("msg","失败原因：服务器异常");
		}
		
		return result;
	}
	
	/**
	 * 支付宝扫码
	 * @param orderNum	订单号
	 * @param payMoney	金额
	 * @param body		订单名称
	 * @param ip		ip
	 * @param mchId		商户号
	 * @param key		密钥
	 * @return
	 */
	public static Map<String,String> aliPay(String orderNum,String payMoney,String body,String ip
			,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		try {
			Map<String, String> sginMap = new HashMap<String, String>();
	        String sign = "";
	        //商户号
	        sginMap.put("mch_id",mchId);
	        
	        //随机字符串
	        sginMap.put("nonce_str",new Date().getTime()+ RandomUtil.generateString(4));
	        // 商品描述
	        sginMap.put("body", body);
	        // 商户订单号
	        sginMap.put("out_trade_no", orderNum);
	        // 总金额，单位是分
	        sginMap.put("total_fee", DecimalUtil.yuanToCents(payMoney));
	        //客户端ip
	        sginMap.put("spbill_create_ip", ip);
	        
	        //异步回调URL
	        sginMap.put("notify_url", ZsxmConfig.notifyUrl);
	        
	        sign = signMd5(key, sginMap);
	        	
			sginMap.put("sign", sign);
			
			SortedMap<String, String> xmlMap = new TreeMap<String, String>(sginMap);
			String xmlInfo = parseXML(xmlMap);
			System.out.println("xml字符串："+xmlInfo);
			String info = sendPost(ZsxmConfig.ali_url, xmlInfo);
			System.out.println("=====>招行厦门支付返回的预支付订单信息:\n" + info + " \n");
			Map<String,String> resultMap = XmlUtils.toMap(info);
			System.out.println("=====>转换为map值为：" + resultMap);
			if (!ZsxmSignUtils.checkParam(resultMap,key)) {
				resultMap.put("code","10001");
				resultMap.put("msg","失败原因："+"验证签名不通过");
				System.out.println("=====>招行厦门支付调起失败原因：验证签名不通过");
            } else {
                if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                	result.put("code","10000");
                	result.put("webStr",resultMap.get("qr_code"));
                }else{
                	result.put("code","10001");
                	result.put("msg","失败原因："+resultMap.toString()+"(上游问题)");
                	System.out.println("=====>招行厦门支付调起失败原因："+resultMap);
                } 
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("code","10001");
        	result.put("msg","失败原因：服务器异常");
		}
		
		return result;
	}
	
	/**
	 * 查询订单
	 * @param orderNum	订单号
	 * @param payType	支付类型1支付宝2微信
	 * @param mchId	商户号
	 * @param key	密钥
	 * @return
	 */
	public static Map<String,String> query(String orderNum,String payType
			,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code","10001");
		try {
			Map<String, String> sginMap = new HashMap<String, String>();
	        String sign = "";
	        //商户号
	        sginMap.put("mch_id",mchId);
	        
	        //随机字符串
	        sginMap.put("nonce_str",new Date().getTime()+ RandomUtil.generateString(4));
	        // 商户订单号
	        sginMap.put("out_trade_no", orderNum);
	        
	        sign = signMd5(key, sginMap);
	        	
			sginMap.put("sign", sign);
			
			SortedMap<String, String> xmlMap = new TreeMap<String, String>(sginMap);
			String xmlInfo = parseXML(xmlMap);
			System.out.println("xml字符串："+xmlInfo);
			//微信订单查询
			String url = ZsxmConfig.wx_query_url;
			if("1".equals(payType)){
				//支付宝订单查询
				url=ZsxmConfig.ali_query_url;
			}
			String info = sendPost(url, xmlInfo);
			System.out.println("=====>招行厦门查询返回的订单信息:\n" + info + " \n");
			Map<String,String> resultMap = XmlUtils.toMap(info);
			System.out.println("=====>转换为map值为：" + resultMap);
			if (!ZsxmSignUtils.checkParam(resultMap,key)) {
				resultMap.put("msg","失败原因："+"验证签名不通过");
				System.out.println("=====>招行厦门查询调起失败原因：验证签名不通过");
            } else {
                if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                	if("1".equals(payType)){
                		if("TRADE_SUCCESS".equals(resultMap.get("trade_state"))
                				||"TRADE_FINISHED".equals(resultMap.get("trade_state"))){
                			result.put("code","10000");
                			String out_trade_no = resultMap.get("out_trade_no");//商户订单号
                        	String centsToYuan = DecimalUtil.centsToYuan(resultMap.get("total_fee"));
    						String transaction_id = resultMap.get("transaction_id");//支付金额
                            
    						resultMap.put("out_trade_no", out_trade_no);
    						resultMap.put("total_fee", centsToYuan);
    						resultMap.put("transaction_id", transaction_id);
                		}
                	}else{
                		if("SUCCESS".equals(resultMap.get("trade_state"))
                				||"REFUND".equals(resultMap.get("trade_state"))){
                			result.put("code","10000");
                			String out_trade_no = resultMap.get("out_trade_no");//商户订单号
                        	String centsToYuan = DecimalUtil.centsToYuan(resultMap.get("total_fee"));
    						String transaction_id = resultMap.get("transaction_id");//支付金额
                            
    						resultMap.put("out_trade_no", out_trade_no);
    						resultMap.put("total_fee", centsToYuan);
    						resultMap.put("transaction_id", transaction_id);
                		}
                	}
                }else{
                	result.put("msg","失败原因："+resultMap.toString()+"(上游问题)");
                	System.out.println("=====>招行厦门查询调起失败原因："+resultMap);
                } 
            }
			
		} catch (Exception e) {
			e.printStackTrace();
        	result.put("msg","失败原因：服务器异常");
		}
		
		return result;
	}
	
	/**
	 * 退款
	 * @param orderNum		订单号
	 * @param refundNum		退款订单号
	 * @param payMoney		支付金额
	 * @param refundMoney	退款金额
	 * @param payType		支付类型1支付宝2微信
	 * @param mchId			商户号
	 * @param key			密钥
	 * @return
	 */
	public static Map<String,String> refund(String orderNum,String refundNum,String payMoney
			,String refundMoney,String payType,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code","10001");
		try {
			Map<String, String> sginMap = new HashMap<String, String>();
	        String sign = "";
	        //商户号
	        sginMap.put("mch_id",mchId);
	        
	        //随机字符串
	        sginMap.put("nonce_str",new Date().getTime()+ RandomUtil.generateString(4));
	        // 商户订单号
	        sginMap.put("out_trade_no", orderNum);
	        
	        // 退款订单号
	        sginMap.put("out_refund_no", refundNum);
	        
	        //支付宝退款地址
	        String url=ZsxmConfig.ali_refund_url;
	        if("2".equals(payType)){
	        	// 支付金额
		        sginMap.put("total_fee", payMoney);
		        //微信订单退款地址
				url = ZsxmConfig.wx_refund_url;
	        }
	        
	        // 退款金额
	        sginMap.put("refund_fee", refundMoney);
	        
	        sginMap.put("op_user_id", mchId);
	        
	        sign = signMd5(key, sginMap);
	        	
			sginMap.put("sign", sign);
			
			SortedMap<String, String> xmlMap = new TreeMap<String, String>(sginMap);
			String xmlInfo = parseXML(xmlMap);
			System.out.println("xml字符串："+xmlInfo);
			String info = sendPost(url, xmlInfo);
			System.out.println("=====>招行厦门退款返回的订单信息:\n" + info + " \n");
			Map<String,String> resultMap = XmlUtils.toMap(info);
			System.out.println("=====>转换为map值为：" + resultMap);
			if (!ZsxmSignUtils.checkParam(resultMap,key)) {
				resultMap.put("msg","失败原因："+"验证签名不通过");
				System.out.println("=====>招行厦门退款调起失败原因：验证签名不通过");
            } else {
                if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                	result.put("code","10000");
                	result.put("msg","退款成功");
                }else{
                	result.put("msg","失败原因："+resultMap.toString()+"(上游问题)");
                	System.out.println("=====>招行厦门退款调起失败原因："+resultMap);
                } 
            }
			
		} catch (Exception e) {
			e.printStackTrace();
        	result.put("msg","失败原因：服务器异常");
		}
		
		return result;
	}
	
	/**
	 * 对账
	 * @param date		账单日期yyyyMMdd
	 * @param mchId		商户号
	 * @param key		密钥
	 * @return
	 */
	public static String[] clear(String date,String mchId,String key){
		Map<String,String> result = new HashMap<>();
		result.put("code","10001");
		try {
			Map<String, String> sginMap = new HashMap<String, String>();
	        String sign = "";
	        //商户号
	        sginMap.put("mch_id",mchId);
	        
	        //随机字符串
	        sginMap.put("nonce_str",new Date().getTime()+ RandomUtil.generateString(4));
	        // 商户订单号
	        sginMap.put("bill_date", date);
	        
	        sign = signMd5(key, sginMap);
	        	
			sginMap.put("sign", sign);
			
			SortedMap<String, String> xmlMap = new TreeMap<String, String>(sginMap);
			String xmlInfo = parseXML(xmlMap);
			System.out.println("xml字符串："+xmlInfo);
			String info = sendPost(ZsxmConfig.clear_url, xmlInfo);
			System.out.println("=====>招行厦门对账返回的订单信息:\n" + info + " \n");
			if(info.contains("<return_code>FAIL</return_code>")){
				return null;
			}
			String [] str = info.split("\n");
			System.out.println(str.length);
			return str;
			/*Map<String,String> resultMap = XmlUtils.toMap(info);
			System.out.println("=====>转换为map值为：" + resultMap);
			if (!ZsxmSignUtils.checkParam(resultMap,key)) {
				resultMap.put("msg","失败原因："+"验证签名不通过");
				System.out.println("=====>招行厦门对账调起失败原因：验证签名不通过");
            } else {
                if ("SUCCESS".equals(resultMap.get("return_code")) && "SUCCESS".equals(resultMap.get("result_code"))) {
                	result.put("code","10000");
                	result.put("msg","退款成功");
                }else{
                	result.put("msg","失败原因："+resultMap.toString()+"(上游问题)");
                	System.out.println("=====>招行厦门对账调起失败原因："+resultMap);
                } 
            }*/
			
		} catch (Exception e) {
			e.printStackTrace();
        	result.put("msg","失败原因：服务器异常");
		}
		
		return null;
	}
	
	/**
	 * 
	 * @Title: signMd5
	 * @Description: MD5字符串拼接加密
	 * @param @param key
	 * @param @param map
	 * @param @return 设定文件
	 * @return String 返回类型
	 * @throws
	 */
	public static String signMd5(String key, Map<String, String> map) {
		Map<String, Object> sginMap = new HashMap<String, Object>();
		for (String keys : map.keySet()) {
			sginMap.put(keys, map.get(keys));
		}
		/*String newMap = MapUtil.mapToJson(map);
		sginMap = MapUtil.parseJsonToMap(newMap);*/
		String sgin = PaySignUtil.getParamStr(sginMap);
		sgin = sgin + "&key=" + key;
		System.out.println("=====>招行厦门支付参数加密拼接为：" + sgin);
		return MD5.GetMD5Code(sgin).toUpperCase();
	}
	
	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
				SslUtils.ignoreSsl();
			}
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "text/xml;charset=ISO-8859-1");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n"+line;
			}
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流finally{
		try {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result.replaceFirst("\n", "");
	}
	
	/**
    * @Title: parseXML 
    * @Description:map->String xml
    * @param @param parameters
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws
    */
    public static String parseXML(SortedMap<String, String> parameters) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<xml>\n");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
        }
        sb.append("</xml>");
        return sb.toString();
    }
	
	public static void main(String[] args) {
//		pay(System.currentTimeMillis()+"", "0.01", "测试支付","192.168.1.172", "SCAN"
//				, "", "", "100000002249", "6965ae07be0a2a036a61a039461b88e6");
		
//		aliPay(System.currentTimeMillis()+"", "0.01", "测试支付","192.168.1.172", "100000002249", "6965ae07be0a2a036a61a039461b88e6");
	
//		query("DD20171201153808380738552", "1", "100000002249", "6965ae07be0a2a036a61a039461b88e6");
//		refund("DD20171202104318364989960", System.currentTimeMillis()+"", "1", "1", "1", "100000002249", "6965ae07be0a2a036a61a039461b88e6");
		
		clear("20171202", "100000002249", "6965ae07be0a2a036a61a039461b88e6");
	}
}
