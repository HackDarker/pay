package com.pay.business.util.swtPay;

import java.net.URLDecoder;
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
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.core.teamwork.base.util.encrypt.MD5;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.alipay.xyBank.XmlUtils;
import com.pay.business.util.pfSZ.Base64;

/**
 * Title: 商务通支付
 * Description: 
 * @author yy
 * 2017年9月13日
 */
public class SWTPay {
	private static Logger log = Logger.getLogger(SWTPay.class);

	/**
	 * 微信支付宝主扫
	 * @param merchantId 商户号
	 * @param subChnMerno 渠道子商户
	 * @param orderId 商品订单号
	 * @param orderAmount 订单金额(分)
	 * @param payType 扫码类型1WX、2支付宝
	 * @param key
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> swtPayScan(String merchantId, String subChnMerno, String orderId, 
			String orderAmount, String payType, String key,String prdName){
		Map<String, String> result = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("version", SWTConfig.VERSION);
			param.put("merchantId", merchantId);
			param.put("subChnMerno", subChnMerno);
			if("1".equals(payType)){
				param.put("prodCode", "CP00000017");
			}else if ("2".equals(payType)) {
				param.put("prodCode", "CP00000018");
			}

			param.put("orderId", orderId);
			param.put("orderAmount", orderAmount);
			param.put("orderDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			param.put("prdOrdType", SWTConfig.ORDTYPE);
			param.put("retUrl", SWTConfig.NOTIFYURL);
			
			param.put("prdName", prdName);
			param.put("prdDesc", prdName);
			param.put("signType", SWTConfig.SIGNTYPE);

			
			param.put("signature", sign(key, param));
			System.out.println("signature："+param.get("signature"));
			log.info("商务通微信支付宝主扫参数：" + param.toString());
			String doPost = doPost(SWTConfig.URL, param);
			log.info("商务通微信支付宝主扫回复：" + doPost);
			result = XmlUtils.toMap(doPost);
			
			Document doc = DocumentHelper.parseText(doPost);
			Element root = doc.getRootElement();
			Map<String, String> map = new HashMap<String, String>();
			//处理xml
			getNodes(root, map);
			
			if(null!=result.get("codeUrl") && ""!=result.get("codeUrl")){
				result.put("qr_code", URLDecoder.decode(result.get("codeUrl")));
				result.remove("codeUrl");
				result.remove("qrURL");
				result.put("code", "10000");
			}else {
				result.put("msg", map.get("retMsg")+"(上游问题)");
				result.put("code", "10001");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("商务通微信支付宝主扫异常："+ e.getMessage());
		}
		return result;
	}

	/**
	 * 订单查询
	 * @param merchantId
	 * @param orderId
	 * @param key
	 * @return
	 */
	public static Map<String, String> queryOrder(String merchantId, String orderId, String key){
		Map<String, String> result = new HashMap<String, String>();
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("version", SWTConfig.VERSION);
			param.put("queryType", "1");
			param.put("merchantId", merchantId);
			param.put("orderId", orderId);
			param.put("signType", SWTConfig.SIGNTYPE);

			
			param.put("signature", sign(key, param));
			log.info("商务通订单查询参数：" + param.toString());
			String doPost = doPost(SWTConfig.QUERY_URL, param);
			log.info("商务通订单查询回复：" + doPost);
			Document doc = DocumentHelper.parseText(doPost);
			Element root = doc.getRootElement();
			Map<String, String> map = new HashMap<String, String>();
			//处理xml
			getNodes(root, map);
			if("0001".equals(map.get("retCode")) && "1".equals(map.get("status"))){
				String out_trade_no = map.get("orderId");
				String centsToYuan = DecimalUtil.centsToYuan(map.get("amount"));
				
				result.put("out_trade_no", out_trade_no);
				result.put("total_fee", centsToYuan);
			}else {
				log.info("商务通订单查询失败：" + map.get("retMsg") +","+map.get("statusDes"));
				result.put("retCode", map.get("retCode"));
				result.put("msg", map.get("statusDes"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("商务通订单查询异常："+ e.getMessage());
		}
		return result;
	}

	/**
	 * 商务通对账文件获取
	 * @param merchantId
	 * @param billDate 对账日期yyyyMMdd
	 * @param key
	 * @return
	 */
	public static String[] swtOrderDown(String merchantId, String billDate, String key){
		TreeMap<String, String> param = new TreeMap<String, String>();
		try {
			param.put("version", SWTConfig.VERSION);
			param.put("merchantId", merchantId);
			param.put("billDate", billDate);
			param.put("signType", SWTConfig.SIGNTYPE);
			
			param.put("signature", sign(key, param));
			log.info("商务通对账参数：" + param.toString());
			String doPost = doPost(SWTConfig.FILE_URL, param);
			log.info("商务通对账回复：" + doPost);

			Document doc = DocumentHelper.parseText(doPost);
			Element root = doc.getRootElement();
			Map<String, String> map = new HashMap<String, String>();
			//处理xml
			getNodes(root, map);
			
			if(!map.get("filecontent").equals("")){
				String str = new String(Base64.decode(map.get("filecontent")));
				return str.split("\n");
			}else {
				log.info("商务通对账无数据：" + merchantId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error("商务通对账异常：" + e.getMessage());
		}
		return null;
	}
	
	/*public static Map<String, String> dfPay(){
		
	}*/
	
	public static void main(String[] args) throws DocumentException {
		/*Map<String, String> swtPayScan = swtPayScan("00000000001865", "7551000001", "PP" + System.currentTimeMillis(), "1", "1", "HE4CCH20HIKO0GYPMP2ATFBX");
		System.out.println(swtPayScan.toString());*/
		/*Map<String, String> queryOrder = queryOrder("00000000001865", "PP1505698760854", "HE4CCH20HIKO0GYPMP2ATFBX");
		System.out.println(queryOrder.toString());*/
		String[] swtOrderDown = swtOrderDown("00000000001865", "20170914", "HE4CCH20HIKO0GYPMP2ATFBX");
		System.out.println(swtOrderDown.length);
		for (String str : swtOrderDown) {
			String[] split = str.split("\\|");
			System.out.println(split[2]);
		}
	}
	
	/**
	 * 遍历xml节点
	 */
	@SuppressWarnings("unchecked")
	public static void getNodes(Element node, Map<String, String> result){
		List<Attribute> listAttr=node.attributes();//当前节点的所有属性的list  
	    for(Attribute attr:listAttr){//遍历当前节点的所有属性  
	        String name=attr.getName();//属性名称  
	        String value=attr.getValue();//属性的值  
	        result.put(name, value);
	    }  
	      
	    //递归遍历当前节点所有的子节点  
	    List<Element> listElement=node.elements();//所有一级子节点的list  
	    for(Element e:listElement){//遍历所有一级子节点  
	        getNodes(e, result);  
	    }  
	}
	
	public static String sign(String key, TreeMap<String, String> param){
		String signInfo = "";
		for (String pkey : param.keySet()) {
			if("signature".equals(pkey)){
				continue;
			}
			signInfo += pkey+"="+param.get(pkey)+"&";
		}
		signInfo = signInfo.subSequence(0, signInfo.length()-1).toString();
		signInfo+=key;
		return MD5.GetMD5Code(signInfo);
	}
	
	/**
	 * 特殊验签
	 */
	public static String paySign(String key, Map<String, String> map){
		StringBuffer sb = new StringBuffer();
		sb.append("versionId="+map.get("versionId"));
		sb.append("&merchantId="+map.get("merchantId"));
		sb.append("&orderId="+map.get("orderId"));
		sb.append("&settleDate="+map.get("settleDate"));
		sb.append("&completeDate="+map.get("completeDate"));
		sb.append("&status="+map.get("status"));
		sb.append("&notifyTyp="+map.get("notifyTyp"));
		sb.append("&payOrdNo="+map.get("payOrdNo"));
		sb.append("&orderAmt="+map.get("orderAmt"));
		sb.append("&notifyUrl="+map.get("notifyUrl"));
		sb.append("&signType="+map.get("signType"));
		sb.append(key);
		System.out.println(sb);
		return MD5.GetMD5Code(sb.toString());
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
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.close();
		}
		return result;
	}
}
