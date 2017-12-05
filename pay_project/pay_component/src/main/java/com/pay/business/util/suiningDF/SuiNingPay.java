package com.pay.business.util.suiningDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.pay.business.util.xyBankWeChatPay.SslUtils;
import com.thoughtworks.xstream.XStream;

/**
 * @Title: SuiNingPay.java
 * @Package com.pay.business.util.suining
 * @Description: 遂宁银行代付
 * @author ZHOULIBO
 * @date 2017年10月11日 下午4:38:11
 * @version V1.0
 */
public class SuiNingPay {
	/**
	 * pay 
	 * 支付清算接口
	 * @param head
	 * @param body
	 * @return    设定文件 
	 * Map<String,Object>    返回类型
	 */
	public static Map<String, Object> pay(RequestHeader head, Body body) {
		String singnData = getSign(body);
		if (singnData != null) {
//			String params = createXml(head, body, singnData);
			String requestHeader=getRequestHeaderXml(head);
			String bodyXml=getBodyXml(body);
			String params =createXml2(requestHeader, bodyXml, singnData);
			String responseXml =sendPost(SuiNingConfig.payUrl, params);
			System.out.println("响应返回值为：" + responseXml);
		}
		return null;

	}
	/**
	 * getcashOpnbrNo 
	 * 清算行号查询接口
	 * @param head
	 * @param cdtrid	银行卡号
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String  getcashOpnbrNo(RequestHeader head,String cdtrid){
		try{
			String headerXml=getRequestHeaderXml(head);
			StringBuffer sb=new StringBuffer();
			sb.append("<msgtype>IPOS</msgtype>");
			sb.append("<txcode>2200</txcode>");
			sb.append("<sw_tx_code>IPOS</sw_tx_code>");
			sb.append("<cdtrid>"+cdtrid+"</cdtrid>");
			System.out.println("Body-XML参数值为："+sb.toString());
			String bodyXml=sb.toString();
			//签名
			String signature= getSign(bodyXml);
			//组装请求报文
			String xml="<?xml version='1.0' encoding='UTF-8'?>"+"<bankpay>"+"<Head>"+headerXml+"</Head>"+"<Body>"+bodyXml+"</Body>"+"<Signature>"+signature+"</Signature>"+"</bankpay>";
			System.out.println("组装请求报文XML为："+xml);
			String responseXml=	sendPost(SuiNingConfig.payUrl, xml);
			System.out.println("清算行号查询为："+responseXml);
			if(!responseXml.equals("")&&responseXml!=null){
				//解析
				Map<String, Object> xmlMap=readStringXmlOut(responseXml);
				if(xmlMap.containsKey("retCode")&&xmlMap.get("retCode").equals("0000")){
					String cash_opn_br_no=xmlMap.get("cash_opn_br_no").toString();
					System.out.println("行号为："+cash_opn_br_no);
					return cash_opn_br_no;
				}
			}else{
				System.out.println("查询行号失败！！！");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * queryResult 
	 * 清分结果查询接口
	 * @param head
	 * @param txtraceno
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String queryResult(RequestHeader head,String txtraceno,String txdate){
		String headerXml=getRequestHeaderXml(head);
		StringBuffer sb=new StringBuffer();
		sb.append("<msgtype>IPOS</msgtype>");
		sb.append("<sw_tx_code>IPOS</sw_tx_code>");
		sb.append("<txcode>2100</txcode>");
		sb.append("<txdate>"+txdate+"</txdate>");
		sb.append("<txtraceno>"+txtraceno+"</txtraceno>");
		System.out.println("Body-XML参数值为："+sb.toString());
		String bodyXml=sb.toString();
		//签名
		String signature= getSign(bodyXml);
		//组装请求报文
		String xml="<?xml version='1.0' encoding='UTF-8'?>"+"<bankpay>"+"<Head>"+headerXml+"</Head>"+"<Body>"+bodyXml+"</Body>"+"<Signature>"+signature+"</Signature>"+"</bankpay>";
		System.out.println("组装请求报文XML为："+xml);
		String responseXml=	sendPost(SuiNingConfig.payUrl, xml);
		System.out.println("清算行号查询为："+responseXml);
		if(!responseXml.equals("")&&responseXml!=null){
			//解析
			Map<String, Object> xmlMap=readStringXmlOut(responseXml);
			if(xmlMap.containsKey("retCode")&&xmlMap.get("retCode").equals("0000")){
				String state=xmlMap.get("state").toString();
				System.out.println("清分结果查询状态为："+state);
				return state;
			}
		}else{
			System.out.println("清分结果查询接口失败！！！");
		}
		return null;
	}
	
	
	/**
	 * getSign 
	 * 获取POS和宇信之间需要的验签
	 * @param head
	 * @param body
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String getSign(Body body) {
		try {
			String sb = getBodyXml(body);
			String singnData = getSign(sb.toString());
			return singnData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * getSign 
	 * 签名
	 * @param bodyXml
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String getSign(String bodyXml){
		try{
			//获取签名密钥地址
			PKCS12Tool tool = PKCS12Tool.getSigner(SuiNingConfig.key_pfx_path, SuiNingConfig.key_password);
			String singnData =tool.sign(bodyXml.getBytes());
			System.out.println("加密签名为："+singnData);
			return singnData;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * getBodyXml 
	 * 获取Body数据XML格式
	 * @param body
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String getBodyXml(Body body){
		StringBuffer sb=new StringBuffer();
		sb.append("<merchcode>"+body.getMerchcode()+"</merchcode>");
		sb.append("<msgtype>"+body.getMsgtype()+"</msgtype>");
		sb.append("<txcode>"+body.getTxcode()+"</txcode>");
		sb.append("<hvbrno>"+body.getHvbrno()+"</hvbrno>");
		sb.append("<sw_tx_code>"+body.getSw_tx_code()+"</sw_tx_code>");
		sb.append("<txtpcd>"+body.getTxtpcd()+"</txtpcd>");
		sb.append("<txamt>"+body.getTxamt()+"</txamt>");
		sb.append("<dbtrnm>"+body.getDbtrnm()+"</dbtrnm>");
		sb.append("<dbtrid>"+body.getDbtrid()+"</dbtrid>");
		sb.append("<dbtrissr>"+body.getDbtrissr()+"</dbtrissr>");
		sb.append("<dbtrtype>"+body.getDbtrtype()+"</dbtrtype>");
		sb.append("<dbtrbrnchid>"+body.getDbtrbrnchid()+"</dbtrbrnchid>");
		sb.append("<cash_opn_br_no>"+body.getCash_opn_br_no()+"</cash_opn_br_no>");
		sb.append("<cdtrnm>"+body.getCdtrnm()+"</cdtrnm>");
		sb.append("<cdtrid>"+body.getCdtrid()+"</cdtrid>");
		sb.append("<ctgypurpcd>"+body.getCtgypurpcd()+"</ctgypurpcd>");
		sb.append("<remark>"+body.getRemark()+"</remark>");
		sb.append("<usage>"+body.getUsage()+"</usage>");
		sb.append("<tel>"+body.getTel()+"</tel>");
		sb.append("<passwd>"+""+"</passwd>");
		System.out.println("Body-XML参数值为："+sb.toString());
		return sb.toString();
	}
	/**
	 * getRequestHeaderXml 
	 * 获取请求报文头部XML 
	 * @param head
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String getRequestHeaderXml(RequestHeader head){
		StringBuffer sb=new StringBuffer();
		sb.append("<version>"+head.getVersion()+"</version>");
		sb.append("<instId>"+head.getInstId()+"</instId>");
		sb.append("<certId>"+head.getCertId()+"</certId>");
		sb.append("<requestBusiDate>"+head.getRequestBusiDate()+"</requestBusiDate>");
		sb.append("<requestTimestamp>"+head.getRequestTimestamp()+"</requestTimestamp>");
		sb.append("<requestSeqNo>"+head.getRequestSeqNo()+"</requestSeqNo>");
		sb.append("<channelNo>"+head.getChannelNo()+"</channelNo>");
		System.out.println("RequestHeader-XML参数值为："+sb.toString());
		return sb.toString();
	}

	/**
	 * createXml2 
	 * 创建请求报文 2
	 * @param requestHeader
	 * @param body
	 * @param signature
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String createXml2(String requestHeader,String body,String signature){
		//组装请求报文
		String xml="<?xml version='1.0' encoding='UTF-8'?>"+"<bankpay>"+"<Head>"+requestHeader+"</Head>"+"<Body>"+body+"</Body>"+"<Signature>"+signature+"</Signature>"+"</bankpay>";
		System.out.println("请求报文数据XML为："+xml);
		return xml;
	}
	
	/**
	 * createXml 
	 * 创建请求报文  
	 * @param head
	 * @param body
	 * @param signature:POS和宇信之间需要验签
	 * @return    设定文件 
	 * String    返回类型
	 */
	public static String  createXml(RequestHeader head,Body body,String signature){
		try{
			BankPayData data=new BankPayData();
			data.setHead(head);
			data.setBody(body);
			data.setSignature(signature);
			XStream  xStream=new XStream();
			xStream.alias("bankpay", BankPayData.class);
			xStream.alias("Head", RequestHeader.class);
			xStream.alias("Body", Body.class);
			String stringXml=xStream.toXML(data);
			Document document = null; 
			document = DocumentHelper.parseText(stringXml.replaceAll("__", "_")); 
			System.out.println(document.asXML());
			return document.asXML();
		}catch(Exception e){
			e.printStackTrace();
		}
		return signature;
		
	}
	//post请求
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
				result += line;
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
		return result;
	}
	public static String  getTimeBy8(){
		try{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			String date=formatter.format(new Date());
			return date;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
		
	}
	/**
	 * readStringXmlOut 
	 * 解析XML 转换为Map
	 * @param xml
	 * @return    设定文件 
	 * Map<String,Object>    返回类型
	 */
	public static Map<String,Object> readStringXmlOut(String xml) {
        Map<String,Object> map = new HashMap<>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml); // 将字符串转为XML

            Element rootElt = doc.getRootElement(); // 获取根节点

            System.out.println("根节点：" + rootElt.getName()); // 拿到根节点的名称

            Element headElt = rootElt.element("Head");
            List<Element> headList = headElt.elements();
            for (Element element : headList) {
            	map.put(element.getName(), element.getStringValue());
			}
            System.out.println();
            Element bodyElt = rootElt.element("Body");
            List<Element> bodyList = bodyElt.elements();
            for (Element element : bodyList) {
            	map.put(element.getName(), element.getStringValue());
			}
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
	public static void main(String[] args) {
		RequestHeader h=new RequestHeader();
		h.setInstId("10000");
		h.setVersion("v1.0.0");
		h.setCertId("ID10000");
		h.setRequestBusiDate("20171012");
		h.setRequestTimestamp(getTimeBy8());
		h.setRequestSeqNo(String.valueOf(new Date().getTime()));
		h.setChannelNo("02");
		Body b=new Body();
		b.setMerchcode("000500000001");
		b.setMsgtype(SuiNingConfig.msgtype);
		b.setTxcode("1100");
		b.setHvbrno(SuiNingConfig.hvbrno);
		b.setSw_tx_code(SuiNingConfig.sw_tx_code);
		b.setTxtpcd(SuiNingConfig.txtpcd);
		b.setTxamt("100.00");
		b.setDbtrnm("理财产品暂收");
		b.setDbtrid("9224100100079");
		b.setDbtrissr("遂宁银行");
		b.setDbtrtype(SuiNingConfig.dbtrtype);
		b.setDbtrbrnchid(SuiNingConfig.dbtrbrnchid);
		b.setCash_opn_br_no("314305106644");
		b.setCdtrnm("刘晓波");
		b.setCdtrid("6228696811001399518");
		b.setCtgypurpcd(SuiNingConfig.ctgypurpcd);
		b.setRemark("代付");
		b.setUsage("代付");
		b.setTel("999999");
		b.setPasswd("");
		pay(h, b);
//		
//		getcashOpnbrNo(h,"6228696811001399518");
//		getTimeBy8();
	}
}
