package com.pay.business.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PaySignUtil {
	private static final Log logger = LogFactory.getLog(PaySignUtil.class);
	
	/**
	 * 参数加密
	 * @param map
	 * @param keyValue  商户密钥
	 * @return
	 * @throws Exception 
	 */
	public static String getSign(Map<String,Object> map,String keyValue) throws Exception{
		StringBuffer buffer = new StringBuffer();
		buffer.append("keyValue=" + keyValue+"&");
		buffer.append(getParamStr(map));
		//System.out.println(buffer.toString());
		String sNewString = getSign(buffer.toString().toUpperCase(), "MD5");
		
		return sNewString;
	}
	
	/**
	 * 参数签名串拼接
	 * @param map
	 * @return
	 */
	public static String getParamStr(Map<String,Object> map){
		StringBuffer buffer = new StringBuffer();
		List<String> keys = new ArrayList<String>(map.keySet());
		//排序
        Collections.sort(keys);
		//参数值拼接进行加密
        for (int i = 0; i < keys.size(); i++) {
        	String key = keys.get(i);
			if(!"sign".equals(key)&&!"keyValue".equals(key)){
				String value = map.get(key)==null?"":map.get(key).toString();
				if(i==0){
					buffer.append(key + "=" + value);
				}else{
					buffer.append("&"+key + "=" + value);
				}
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 获取加密签名
	 * @param str 字符
	 * @param type 加密类型
	 * @return 
	 * @throws Exception
	 */
	public static String getSign(String str, String type) throws Exception {
		MessageDigest crypt = MessageDigest.getInstance(type);
		crypt.reset();
		crypt.update(str.getBytes("UTF-8"));
		return str = byteToHex(crypt.digest());
	}
	
	/**
	 * 
	* @Title: byteToHex 
	* @Description: 字节转换 16 进制
	* @param @param hash
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}
	
	/**
	 * 验签
	 * @param map
	 * @param keyValue  商户密钥
	 * @return
	 * @throws Exception 
	 */
	public static boolean checkSign(Map<String,Object> map,String keyValue) throws Exception{
		if(map.get("sign")==null){
			return false;
		}
		//密文
		String sign = map.get("sign").toString();
		map.remove("sign");
		String sNewString = getSign(map, keyValue);
		
		return sNewString.equals(sign);
	}
	
	public static void main(String[] args) throws Exception {
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("payMoney", "0.01");
		map.put("bussOrderNum", System.currentTimeMillis());
		//map.put("groupValue", "A");
		map.put("notifyUrl", "http://qiuguojie.wicp.net/payDetail/tfbCallBack.do");
		//map.put("returnUrl", "https://pay.yuanbaomj.com/aijinfu/return");
		//map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//外网appKey
		map.put("appKey", "270461df13a412f373ae6c2771ccd926");//内网appKey
		map.put("orderName", "测试支付");
		map.put("appType", "1");
		map.put("ip", "192.168.1.172");
		map.put("payPlatform", "2");
		
		/*map.put("goodsNote", "758986dsfag");
		map.put("goods_num", "1");
		map.put("remark", "dfsgdsge");
		map.put("ip", "192.168.1.172");
		map.put("appType", "1");
		map.put("payPlatform", "2");*/
		//map.put("orderDescribe", "该测试商品的详细描述");
//		map.put("sign", "387ca231498d271b4281dcb037630767");
		//String s =getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//外网密钥
		String s =getSign(map,"be29c66b2d0b166c90d7a346209259149470faf76e53bf52b39d1a98a8d9250b");//内网密钥
		String paramStr = getParamStr(map)+"&sign="+s;
		System.out.println(paramStr);
		
		System.out.println("本地h5测试——————http://192.168.1.172:8080/page/pay/pay.html?paramStr="+paramStr);
		System.out.println("本地h5测试——————http://192.168.1.172:8080/pay/payment.do?paramStr="+paramStr);
		System.out.println("测试服h5测试——————https://testpayapi.aijinfu.cn/page/pay/pay.html?paramStr="+paramStr);
		System.out.println("正式h5测试——————https://payapi.aijinfu.cn/page/pay/pay.html?paramStr="+paramStr);
		
		System.out.println();
		paramStr = URLEncoder.encode(paramStr);
		System.out.println("本地h5测试——————http://192.168.1.172:8080/pay/multifunctionPayment.do?paramStr="+paramStr);
		
		System.out.println("本地微信sdk测试——————http://192.168.1.172:8080/pay/sdkPayment.do?package=ceshi&paramStr="+paramStr);
		System.out.println("测试服微信sdk测试——————https://testpayapi.aijinfu.cn/pay/multifunctionPayment.do?paramStr="+paramStr);
		System.out.println("正式微信sdk测试——————https://payapi.aijinfu.cn/pay/wxpayPage.do?paramStr="+paramStr);
		System.out.println(paramStr);
		
		
		/*Map<String,Object> param = new HashMap<>();
		param.put("appKey", "3245jklhsdfgkljs对方是个");
		String paramStr = URLEncoder.encode("appKey=dsflghjldfsg得瑟得瑟");
		
		String s = HttpUtil.httpPost("https://payapi.aijinfu.cn/demo/demoPay.do", param);
		System.out.println(s);
		int s = HttpUtil.getCode("https://payapi.aijinfu.cn/demo/demoPay.do", param);
		
		System.out.println(s);
		int s =HttpsUtil.doPost("https://api.youjia2016.com/recharge/notify/quxun_wap", param, "utf-8");
		System.out.println(s);
		//HttpTest.doGet("https://payapi.aijinfu.cn/demo/demoPay.do", param);
		
		//XyBankPay.sendPost("https://payapi.aijinfu.cn/demo/demoPay.do",paramStr);
*/	}
}
