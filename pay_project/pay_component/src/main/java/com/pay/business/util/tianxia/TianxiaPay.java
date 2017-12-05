package com.pay.business.util.tianxia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pay.business.util.alipay.xyBank.XmlUtils;

/**
 * Title: 天付宝代付
 * @author yy
 * 2017年8月28日
 */
public class TianxiaPay {
	private static Logger log = Logger.getLogger(TianxiaPay.class);
	
	private static Map<String, String> resultMap = null;
	
	private static TreeMap<String, String> param = null;
	
	private static SimpleDateFormat  format=new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * 单笔代付
	 * @param spid 商户号
	 * @param sp_serialno 商户代付单号
	 * @param tran_amt 交易金额(分)
	 * @param pay_type 付款方式(1-余额支付、2-企业网银、3-垫资支付、缺损值为1)
	 * @param acct_name 收款人姓名
	 * @param acct_id 收款人账号
	 * @param acct_type 账号类型(null,0-借记卡，1-贷记卡，2-对公账号)
	 * @param mobile 收款人号码(null)
	 * @param bank_name 开户行名称(null)
	 * @param bank_settle_no 开户行支行联行号(null)
	 * @param bank_branch_name 支行名称(null)
	 * @param business_type 业务类型
	 * @param memo 摘要
	 * @param key 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> singlePay(String spid, String sp_serialno, String tran_amt, String pay_type, String acct_name,  String acct_id, 
			String acct_type, String mobile, String bank_name, String bank_settle_no, String bank_branch_name, String business_type, String memo, String publicKey, String privateKey, String md5Key) throws UnsupportedEncodingException{
		resultMap = new HashMap<String, String>();
		resultMap.put("code","10001");
		param = new TreeMap<String, String>();
		param.put("version", TianxiaConfig.version);
		param.put("spid", spid);
		param.put("sp_reqtime", format.format(new Date()));
		param.put("sp_serialno", sp_serialno);
		param.put("tran_amt", tran_amt);
		param.put("cur_type", "1");
		param.put("pay_type", pay_type);
		param.put("acct_id", acct_id);
		if(null!=acct_type){
			param.put("acct_type", acct_type);
		}
		if(null!=mobile){
			param.put("mobile", mobile);
		}
		if(null!=bank_name){
			param.put("bank_name", bank_name);
		}
		if(null!=bank_settle_no){
			param.put("bank_settle_no", bank_settle_no);
		}
		if(null!=bank_branch_name){
			param.put("bank_branch_name", bank_branch_name);
		}
		param.put("business_type", business_type);
		
		try {
			param.put("acct_name", acct_name);
			param.put("memo", memo);
			
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo += "sign="+sign(signInfo, md5Key);
			System.out.println("加密：" + signInfo);
			log.info("=====>天下代付已加签参数：\n" + param.toString());
			String cipherData = encrypt(signInfo, publicKey);
			log.info("=====>天下代付cipherData：\n" + param.toString());
			System.out.println("cipherData：" + cipherData);
			
			String url = TianxiaConfig.URL + TianxiaConfig.SINGLEPAY;
			String doPost = doPost(url, "cipher_data=" + URLEncoder.encode(cipherData));
			log.info("=====>天下代付单笔代付回复：\n" + doPost.toString());
			Map<String, String> map = XmlUtils.toMap(doPost);
			if("00".equals(map.get("retcode"))){
				String decryptResponseData = decryptResponseData(map.get("cipher_data"), privateKey);
				String signs = decryptResponseData.substring(decryptResponseData.indexOf("sign=") + 5, decryptResponseData.length());
				String source = decryptResponseData.substring(0, decryptResponseData.lastIndexOf("&sign"));
				
				//验签
				if(verify(source+"&", signs,  md5Key)) {
					resultMap.put("code","10000");
					resultMap.put("msg", "操作成功！");
					Map<String, Object> maps = toMap(decryptResponseData);
					resultMap.put("bank_transaction", maps.get("tfb_serialno").toString());
					resultMap.put("statusState", maps.get("serialno_state").toString() + "," + maps.get("serialno_desc").toString());
					resultMap.put("rsptime", maps.get("tfb_rsptime").toString());
				}else {
					resultMap.put("msg", "验签失败！");
				}
			}else {
				resultMap.put("msg", map.get("retmsg"));
				log.error("=====>天下代付错误：\n" + map.get("retmsg"));
			}
		} catch (Exception e) {
			log.error("=====>天下代付错误：\n" + e.getMessage());
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 单笔代付查询
	 * @param spid 商户号
	 * @param sp_serialno 订单号
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> singleQuery(String spid, String sp_serialno, String publicKey, String privateKey, String md5Key) {
		resultMap = new HashMap<String, String>();
		resultMap.put("code","10001");
		param = new TreeMap<String, String>();
		param.put("version", TianxiaConfig.version);
		param.put("spid", spid);
		param.put("sp_reqtime", format.format(new Date()));
		param.put("sp_serialno", sp_serialno);
		try {
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo += "sign="+sign(signInfo, md5Key);
			System.out.println("加签：" + signInfo);
			
			String cipherData = encrypt(signInfo, publicKey);
			System.out.println("cipherData：" + cipherData);
			
			String url = TianxiaConfig.URL + TianxiaConfig.SINGLEQUERY;
			String doPost = doPost(url, "cipher_data=" + URLEncoder.encode(cipherData));
			Map<String, String> map = XmlUtils.toMap(doPost);
			if("00".equals(map.get("retcode"))){
				String decryptResponseData = decryptResponseData(map.get("cipher_data"), privateKey);
				String signs = decryptResponseData.substring(decryptResponseData.indexOf("sign=") + 5, decryptResponseData.length());
				String source = decryptResponseData.substring(0, decryptResponseData.lastIndexOf("&sign"));
				
				//验签
				if(verify(source+"&", signs, md5Key)) {
					resultMap.put("code","10000");
					Map<String, Object> maps = toMap(decryptResponseData);
					resultMap.put("dfStatus", "3");
					if("1".equals(maps.get("serialno_state"))){
						resultMap.put("dfStatus", "1");
						resultMap.put("msg", "处理成功！");
					}else if ("2".equals(maps.get("serialno_state"))) {
						resultMap.put("dfStatus", "2");
						resultMap.put("msg", "处理中...");
					}else if ("3".equals(maps.get("serialno_state"))) {
						resultMap.put("msg", "处理失败！");
					}else if ("4".equals(maps.get("serialno_state"))) {
						resultMap.put("msg", "已退汇！");
					}
					resultMap.put("money", maps.get("tran_amt").toString());
				}else {
					resultMap.put("msg", "验签失败！");
				}
			}else {
				resultMap.put("msg", map.get("retmsg"));
				log.error("=====>天下代付查询错误：\n" + map.get("retmsg"));
			}
		} catch (Exception e) {
			log.error("=====>天下代付错误：\n" + e.getMessage());
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 余额查询
	 * @param spid 商户号
	 * @param sp_serialno 流水号
	 * @param qry_type 查询类型(1-只查余额 2-所有账目(暂不支持） 3-查询垫资可用额度 4-查询垫资限额)
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Map<String, String> balanceQuery(String spid, String sp_serialno, String qry_type, String publicKey, String privateKey, String md5Key) {
		resultMap = new HashMap<String, String>();
		resultMap.put("code","10001");
		param = new TreeMap<String, String>();
		param.put("version", TianxiaConfig.version);
		param.put("spid", spid);
		param.put("sp_reqtime", format.format(new Date()));
		param.put("sp_serialno", sp_serialno);
		param.put("qry_type", qry_type);
		try {
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo += "sign="+sign(signInfo, md5Key);
			System.out.println("加签：" + signInfo);
			
			String cipherData = encrypt(signInfo, publicKey);
			System.out.println("cipherData：" + cipherData);
			
			String url = TianxiaConfig.URL + TianxiaConfig.BALANCEQUERY;
			String doPost = doPost(url, "cipher_data=" + URLEncoder.encode(cipherData));
			Map<String, String> map = XmlUtils.toMap(doPost);
			if("00".equals(map.get("retcode"))){
				String decryptResponseData = decryptResponseData(map.get("cipher_data"), privateKey);
				String signs = decryptResponseData.substring(decryptResponseData.indexOf("sign=") + 5, decryptResponseData.length());
				String source = decryptResponseData.substring(0, decryptResponseData.lastIndexOf("&sign"));
				if(verify(source+"&", signs, md5Key)) {
					resultMap.put("code","10000");
					Map<String, Object> maps = toMap(decryptResponseData);
					if("4".equals(qry_type)){
						resultMap.put("quota", maps.get("advancepay_quota").toString());
					}
					if("1".equals(maps.get("account_status").toString())){
						resultMap.put("status", "账户正常！");
						resultMap.put("money", maps.get("available_balance").toString());
					}else if("2".equals(maps.get("account_status"))){
						resultMap.put("status", "账户冻结变更！");
					}else if("3".equals(maps.get("account_status"))){
						resultMap.put("status", "账户冻结！");
						resultMap.put("money", maps.get("available_balance").toString());
					}else if("4".equals(maps.get("account_status"))){
						resultMap.put("status", "账户销户！");
					}
					
				}else {
					resultMap.put("msg", "验签失败！");
				}
			}else {
				resultMap.put("msg", map.get("retmsg"));
				log.error("=====>天下代付查询错误：\n" + map.get("retmsg"));
			}
		}catch (Exception e) {
			log.error("=====>天下代付错误：\n" + e.getMessage());
			resultMap.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 批量代付
	 * @param spid 商户号
	 * @param sp_batch_no 批次号
	 * @param total_money 总金额(分)
	 * @param pay_type 付款方式(1-余额支付、2-企业网银、3-垫资支付、缺损值为1)
	 * @param business_type 业务类型
	 * @param batch_postscript 批次说明(null)
	 * @param lists 批次明细xml
	 * @return
	 */
	public static Map<String, String> batchPay(String spid, String sp_batch_no, String total_money, String pay_type, String business_type, String batch_postscript, List<TianXiaVO> lists, String publicKey, String privateKey, String md5Key){
		resultMap = new HashMap<String, String>();
		resultMap.put("code","10001");
		param = new TreeMap<String, String>();
		param.put("version", TianxiaConfig.version);
		param.put("spid", spid);
		param.put("sp_reqtime", format.format(new Date()));
		param.put("sp_batch_no", sp_batch_no);
		param.put("total_money", total_money);
		param.put("pay_type", pay_type);
		param.put("business_type", business_type);
		if(null!=batch_postscript){
			param.put("batch_postscript", batch_postscript);
		}
		param.put("details", listToXml(lists));
		try {
			String signInfo = "";
			for (String pkey : param.keySet()) {
				signInfo += pkey+"="+param.get(pkey)+"&";
			}
			signInfo += "sign="+sign(signInfo, md5Key);
			System.out.println("加密：" + signInfo);
			log.info("=====>天下代付已加签参数：\n" + param.toString());
			String cipherData = encrypt(signInfo, publicKey);
			log.info("=====>天下代付cipherData：\n" + param.toString());
			System.out.println("cipherData：" + cipherData);
			
			String doPost = doPost(TianxiaConfig.URL + TianxiaConfig.BATCHPAY, "cipher_data=" + URLEncoder.encode(cipherData));
			System.out.println(doPost);
			Map<String, String> map = XmlUtils.toMap(doPost);
			System.out.println(new String(map.get("retmsg").getBytes("GB2312"), "UTF-8"));
			System.out.println(new String(map.get("retmsg").getBytes("GBK"),"UTF-8")); 
			System.out.println(new String(map.get("retmsg").getBytes("ISO-8859-1"),"UTF-8"));
			System.out.println(new String(map.get("retmsg").getBytes("ISO-8859-1"),"GBK"));
			System.out.println(new String(map.get("retmsg").getBytes("UTF-8"),"GB2312"));
			System.out.println(new String(map.get("retmsg").getBytes("UTF-8"),"GBK"));  
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return resultMap;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		/*Map<String, String> singlePay = singlePay("1800046681", Long.toString(new Date().getTime()), "1", "1", "国采支付", "6225887805085784", null, null, null, null, null, "20101", "代付测试");
		System.out.println(singlePay.toString());*/
		/*Map<String, String> singleQuery = singleQuery("1800046681", "1503892694636");
		System.out.println(singleQuery.toString());*/
		/*Map<String, String> balanceQuery = balanceQuery("1800046681", Long.toString(new Date().getTime()), "1");
		System.out.println(balanceQuery.toString());*/
		SimpleDateFormat formats = new SimpleDateFormat("YYYYMMDD");
		List<TianXiaVO> lists = new ArrayList<TianXiaVO>();
		TianXiaVO t1 = new TianXiaVO(Long.toString(new Date().getTime())+(long)1, "1", "国采支付", "6225887805085784", null, null, null, null, null, null, "代付测试1");
		TianXiaVO t2 = new TianXiaVO(Long.toString(new Date().getTime())+(long)2, "1", "国采支付", "6225887805085784", null, null, null, null, null, null, "代付测试2");
		lists.add(t2);
		lists.add(t1);
		/*String format2 = String.format("%04d", 99);
		System.out.println(format2);*/
		//batchPay("1800046681", formats.format(new Date())+"000001", "2", "1", "20201", null, lists);
	}
 
	public static String gb2312ToUtf8(String str) {

		String urlEncode = "" ;

		try {

			urlEncode = URLEncoder.encode (str, "UTF-8" );

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		}

		return urlEncode;

	}
	
	/**
	 * 加密得到cipherData
	 */
	private static String encrypt(String paramstr, String publickey) {
		//String publickey = RSAUtils.loadPublicKey(RSAUtils.GC_PUBLIC_KEY_PATH);
		//String publickey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCjDrkoVbyv4jTxeKtKEiK2mZiezQvfJV3sGhiwOnB+By5sa5Sa6Ls4dt5AGVqKHxyQVKRpu/utwtEt2MijWx45P1y2xGe7oDz2hUXP0j8sSa1NP26TmWHwO7czgJxxrdJ6RNqskSfjwsa5YMsqmcrumxUIxeCg5EOkgU26bnPoZQIDAQAB";
		String cipherData = null;
		try {
			cipherData = RSAUtils.encryptByPublicKey(paramstr.getBytes("UTF-8"), publickey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherData;
	}
	
	/**
	 * rsa解密
	 */
	private static String decryptResponseData(String cipherData, String privatekey) {
		//String privatekey = RSAUtils.loadPrivateKey(RSAUtils.PRIVATE_KEY_PATH);
		String result;
		try {
			result = RSAUtils.decryptByPrivateKey(Base64.decode(cipherData), privatekey);
			//result = new String(result.getBytes("UTF-8"), "UTF-8");
			System.out.println("解密结果:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 加签
	 */
	private static String sign(String signInfo, String md5Key) {
		//signInfo += "key=" + RSAUtils.MD5_KEY;
		signInfo += "key=" +  md5Key;
		String sign = null;
		try {
			sign = RSAUtils.getMD5Str(signInfo);
			System.out.println("签名:" + sign);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sign;
	}
	
	/**
	 * 验签
	 */
	private static boolean verify(String source, String sign, String md5Key) {
		return (sign(source, md5Key).equals(sign.trim())) ? true:false;
	}
	
	public static String doPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			System.out.println("请求结果:" + result);
		} catch (Exception e) {
			System.out.println("发送 POST 请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
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
		}
		return result;
	}
	
	public static Map<String,Object> toMap(String paramString){
		Map<String,Object> map = new HashMap<>();
		if(StringUtils.isNotBlank(paramString)){
			String [] paramArr = paramString.split("&");
			if(paramArr.length>0){
				for (String param : paramArr) {
					String [] keyValueArr = param.split("=", 2);
					if(keyValueArr.length==2){
						map.put(keyValueArr[0], keyValueArr[1]);
					}else if(keyValueArr.length==1){
						map.put(keyValueArr[0], "");
					}else{
						new HashMap<>();
					}
				}
			}
		}
		return map;
	}

	private static String listToXml(List<TianXiaVO> lists) {
		StringBuilder xml = new StringBuilder("<root>");
		for (TianXiaVO vo : lists) {
			xml.append("<row>");
			xml.append("<sp_serialno>" + vo.getSpSerialno() + "</sp_serialno>");
			xml.append("<tran_amt>" + vo.getTranAmt() + "</tran_amt>");
			xml.append("<cur_type>" + vo.getCurType() + "</cur_type>");
			xml.append("<acct_name>" + vo.getAcctName() + "</acct_name>");
			xml.append("<acct_id>" + vo.getAcctId() + "</acct_id>");
			if(null!=vo.getAcctType()) { xml.append("<acct_type>" + vo.getAcctType() + "</acct_type>"); }
			if(null!=vo.getMobile()) { xml.append("<mobile>" + vo.getMobile() + "</mobile>"); }
			if(null!=vo.getBankName()) { xml.append("<bank_name>" + vo.getBankName() + "</bank_name>"); }
			if(null!=vo.getBankSettleNo()) { xml.append("<bank_settle_no>" + vo.getBankSettleNo() + "</bank_settle_no>"); }
			if(null!=vo.getBankBranchName()) { xml.append("<bank_branch_name>" + vo.getBankBranchName() + "</bank_branch_name>"); }
			if(null!=vo.getBusinessNo()) { xml.append("<business_no>" + vo.getBusinessNo() + "</business_no>"); }
			xml.append("<memo>" + vo.getMemo() + "</memo>");
			xml.append("</row>");
		}
		xml.append("</root>");
		return xml.toString();
	}
}
