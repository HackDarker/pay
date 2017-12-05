package com.pay.business.util.wxpay;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.core.teamwork.base.util.encrypt.MD5;
import com.pay.business.util.RandomUtil;

/**
 * Title: 微信企业支付
 * Description: 
 * @author yy
 * 2017年9月26日
 */
public class WXComPay {
	
	/**
	 * 商户id，用于加载证书
	 */
	private static final String MCHID = "1294177601";
	
	private static Logger logger = Logger.getLogger(WXComPay.class);
	
	/**
	 * 微信企业付款
	 * @param mch_appid    商户账号appid
	 * @param mchid    商户号
	 * @param orderNum    商户订单号
	 * @param openid    用户openid
	 * @param amount    金额
	 * @param desc    企业付款描述信息
	 * @param ip   
	 * @param path    证书地址
	 * @param key
	 * @return
	 */
	public static Map<String,String> comPay(String mch_appid, String mchid, String orderNum, String openid, String amount, String desc, String ip, String path, String key){
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("code", "10001");
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("nonce_str", new Date().getTime()+ RandomUtil.generateString(4));
			param.put("mch_appid", mch_appid);
			param.put("mchid", mchid);
			param.put("partner_trade_no", orderNum);
			param.put("openid", openid);
			param.put("amount", amount);
			param.put("desc", desc);
			param.put("spbill_create_ip", ip);
			//校验用户姓名选项，NO_CHECK：不校验真实姓名  FORCE_CHECK：强校验真实姓名
			param.put("check_name", "NO_CHECK");
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				if("signature".equals(pkey)){
					continue;
				}
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo += "key="+key;
			
			param.put("sign", MD5.GetMD5Code(signInfo).toUpperCase());
			logger.info("微信企业付款参数：" + param.toString());
			Map<String, String> map = XMLUtil.toMap(doPost(WeChatConstant.COMPANY_PAY, XMLUtil.toXML(param), path));
			logger.info("微信企业付款返回：" + param.toString());
			System.out.println(map.toString());
			if(map!=null && "success".equalsIgnoreCase(map.get("return_code")) && "success".equalsIgnoreCase(map.get("result_code"))){
				resultMap.put("code", "10000");
			}else {
				resultMap.put("msg", map.get("return_msg"));
				resultMap.put("reason", map.get("err_code_des"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信企业付款异常："+e);
		}
		return resultMap;
	}
	
	public static void main(String[] args) {
		//String key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQC/85cT9vOka4QOewCOR11Y7Yyy6/kQQ4jXf5xcSIz1vTm8CkUbMHil+ABi5/3RbdIyLoAm/AcdJ5eyGxrTBAB3Skg+VM4Sc8/OY+zCGX7lSqTWSpdLmGoc1kCn52Oaz7OWeMHUj3Ply3z2XFW/HfSvS4g0rtpzECRCir8l9LbqBbq+Te8PZaQhimIWEe/MY8cG2KxWGXqcCleb+5K1X+th0mR6iYoJ628bWviUdp5/lpJY6zvOfWi4hiAtdPXi4T1NXFpeDHgWDGyucMnSntcjGQ6Vk8H2nECz4+p1K7gOM2vyMwYUDXISQozIUEeFLu1yCTBVTdjdUxUPZoG9pjtbAgMBAAECggEAFia7OY+XIFNUAwPI1aKAsEGBqig3LtxMT2onljK9KaKzpfKddPzL/4M2MDPLg76jsizRly4geAnYpLIiD1X348xLt/27Zj+9LHk1mlSs4zMRALXM7elh3YAQKBA422W6uNOosqn3Wv53pkqTjqGbWhtiVYoUJZEoThGK64tpDGp3PVSPLJvTY8finF4YAPc/Mf0x8de/Z19hOCYcBGBqZu2bVtxzyPqyobDyW2F65OBf78kMPLvGrq/Vnnt1WRUc0oItchAq2YZmRGnrDy+/c+oBoJ6cSN55RwkU1IbYWxV4dX5B0TejwiiKCbJzaAU5IVUhIZQYZjn6eF3n/fCnMQKBgQD89cQLEP7833nrd4B2MFpa7bxsiSVmzbYMurg9dLh2FzDnpi4skpuKyrLyTsXb+jCNhpGpVEGPuQKEPaELUAcDy+38vsqjrgnAXEAizIwmHo8Xoe4pWaRdWRjYnFvD3zIZTy3MRH9OiqQTKGb4j0FgSXy4YCL70FldEO9SgGTKAwKBgQDCQiGNB6z1MRi+PD81mtf2olIsRLi7UhOwRg3hPE6LWXTU7DGS1NIN76jNKGqmNhTJnUZyXLeNOnYpJOaBUZwdbHqE4PieBXeyN5QVtNeJHwbzYFye9eXXdNhGftGdzalMCZeuEOb1mn8oewiDgx/p4sbxp5yYzNpIhH6v1js1yQKBgQD7m+1i4IXwusyHdPcXGCNXnG62Ong0WFtDMBqALnAoLgsb70SwWydRlysFL2tI0gOnUfnixobr0Sd6p95e5TwE/7v5kUD4K+1RI7E7q9+fLvg/lnbRNHPz0eKP2n7ARmqLmCFCs8M5OVfYInDBvaRWhOIEdsN1W5j3pBeYJVObRwKBgQCLYAFMe35XqXM0QmujnUZn+Ux14JSeKaRgq9KXkLpV9sgBYLmsvK89On9lOyNRv6moggQiV22GkbRnjscnb5NP9R3uqq8WSZv4rTG9ZTPjePR2t+OcVZcjv/zQj3r7+qwGFzfRtuTOwJuNQsD4AqNqHsBYb8bGRuwybYWryUxQKQKBgQC55HlDamiZXBQ+5tlNbHfICW7HbZUCWFAV/2iWDChVI7/vxx5gmH28suKkS19O2Nqk1uhUJvG7yzpajbaDvEX87TZXUT3OgtW4HkBX/yfzqSSU3ME6vHCtpwck9Kny3FOOumGf995dIN5LMemkI2I+X2bRelrLvJfl42FCCu1jSg==";
		
		String key = "wK0dfMLtovcrFPXQF5wY9QiDBsm4QQOb";
		comPay("wx077af1331c98f55b", "1294177601", "A"+System.currentTimeMillis(), "oQCeRt4OvdDvOGoRwI-SDLEkTwLY", "100", "测试", "113.116.89.36", "C:/apiclient_cert.p12", key);
	}
	
	@SuppressWarnings("deprecation")
	private static String doPost(String url, String data, String path) throws Exception{
		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(path));
		try {
			keyStore.load(instream, MCHID.toCharArray());
		} finally {
			instream.close();
		}

		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCHID.toCharArray()).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		
		try {
			HttpPost httpost = new HttpPost(url);
			System.out.println("executing request" + httpost.getRequestLine());
			httpost.addHeader("Connection", "keep-alive");
	        httpost.addHeader("Accept", "*/*");
	        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        httpost.addHeader("Host", "api.mch.weixin.qq.com");
	        httpost.addHeader("X-Requested-With", "XMLHttpRequest");
	        httpost.addHeader("Cache-Control", "max-age=0");
	        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
	        httpost.setEntity(new StringEntity(data, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				
				String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
		        return jsonStr;
			} catch(Exception e){
				e.printStackTrace();
				return e.getMessage();
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
}
