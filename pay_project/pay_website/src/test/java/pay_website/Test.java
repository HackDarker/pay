package pay_website;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.core.teamwork.base.util.http.HttpUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.httpsUtil.HttpsUtil;

public class Test {
	/**
	* @Title: main 
	* @Description: 支付宝扫码支付测试类
	* @param @param args
	* @param @throws Exception    设定文件 
	* @return void    返回类型 
	* @throws
	*/
	private static String DateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		return str;
	}
	
	private static Date DateStr(String date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date str = sdf.parse(date);
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		/*//回调测试
		notify("{sign=cfa348bf1be16344ba770d17abbda9e0, result_code=200, pay_money=0.01, order_num=DD20171028175600821167170, pay_time=20171028175754, pay_discount_money=0.01, buss_order_num=201710281757476}"
				,"https://testpaymg.aijinfu.cn/admin/testCallBack.do");*/
//		alipayPage(1);	//点游支付宝app支付
//		wxpayPage(1);	//点游微信wap支付
//		payment(1,"2");	//wap接口
		multifunctionPayment(3, "2");	//多功能接口
//		payRefund();
		
//		urlCon(2);
//		df(2);
//		dfOrder(2);
//		aliScanpay(3,"2");
//		appPay();
		//htbweixinpay();
//		payRefund();
//		wftweixinpay();
//		selectOrder();
//		htbweixinpay();
//		payRefund();
//		wftweixinpay();
		
	}
	// 支付宝扫码支付
	public static void aliScanpay(Integer type,String channel) {
		try {
			// "bussOrderNum","orderName","payMoney","appKey","returnUrl","notifyUrl","sign"
			// http://www.aijinfu.cn/com.qq.yyb/aaa.apk/data111111.html?
			// http://www.aijinfu.cn/articl/fdsdfdsfsd/4106512156.html
			// http://www.aijinfu.cn?a=com.qq.yyb&b=aaa.apk
			Map<String, Object> map = new HashMap<>();
			long num=new Date().getTime();
			System.out.println("======订单号为："+num+"==========");
			map.put("bussOrderNum",String.valueOf(num));
			map.put("payMoney", "0.01");
			map.put("notifyUrl", "https://testpaymg.aijinfu.cn/admin/testCallBack.do");
			map.put("orderName", "测试支付宝扫码支付");
			map.put("returnUrl", "https://www.baidu.com");
			map.put("channel", channel);
			
			String url = "";
			if(type==1){
				map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
				String s =PaySignUtil.getSign(map,"l7Er4m02fekDCXEXY3LazoApr3A8AllXD1AxN3R1");//内网密钥
				String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
				map.clear();
				map.put("paramStr", paramStr);
				url = "http://qiuguojie.wicp.net/aiJinFuPay/aliScanPay.do";
			}else if(type==2){
				map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//测试服appKey
				String s =PaySignUtil.getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//测试服密钥
				String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
				map.clear();
				map.put("paramStr", paramStr);
				url = "https://testpayapi.aijinfu.cn/aiJinFuPay/aliScanPay.do";
			}else{
				map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");//正式服appKey
				String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
				String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
				map.clear();
				map.put("paramStr", paramStr);
				url = "https://payapi.aijinfu.cn/aiJinFuPay/aliScanPay.do";
			}
			
			long time = System.currentTimeMillis();
			// 接口请求
			String result = HttpUtil.httpPost(url, map);
			System.out.println(System.currentTimeMillis()-time);
			System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			System.out.println("返回结果为========>"+json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// app支付
	public static void appPay() {
		try {
			// "bussOrderNum","orderName","payMoney","appKey","returnUrl","notifyUrl","sign"
			// http://www.aijinfu.cn/com.qq.yyb/aaa.apk/data111111.html?
			// http://www.aijinfu.cn/articl/fdsdfdsfsd/4106512156.html
			// http://www.aijinfu.cn?a=com.qq.yyb&b=aaa.apk
			Map<String, Object> map = new HashMap<>();
			long num=new Date().getTime();
			System.out.println("======订单号为："+num+"==========");
			map.put("bussOrderNum",String.valueOf(num));
			map.put("payMoney", "0.01");
			//map.put("appKey", "270461df13a412f373ae6c2771ccd926");
			map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");
			//map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");
			map.put("notifyUrl", "http://qiuguojie.wicp.net/aiJinFuPay/companyCallBack.do");
			map.put("orderName", "测试支付宝扫码支付");
			map.put("returnUrl", "https://www.baidu.com");
			map.put("payPlatform", 2);
			map.put("appType", 1);
			//String s = PaySignUtil.getSign(map, "be29c66b2d0b166c90d7a346209259149470faf76e53bf52b39d1a98a8d9250b");
			String s = PaySignUtil.getSign(map, "u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");// 外网密钥
			//String s = PaySignUtil.getSign(map, "91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");// 外网密钥
			
			String paramStr = PaySignUtil.getParamStr(map) + "&sign=" + s;
			// 参数进行URLEncoder转码
//				paramStr = URLEncoder.encode(paramStr);
//				System.out.println(paramStr);
			map.clear();
			map.put("paramStr", paramStr);
			map.put("package", "com.example.jinfu.pay.demo");
			
			long time = System.currentTimeMillis();
			// 接口请求
			String result = HttpUtil.httpPost("https://testpayapi.aijinfu.cn/pay/appPayment.do", map);
			System.out.println(System.currentTimeMillis()-time);
			System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			System.out.println("返回结果为========>"+json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 支付宝扫码退款
	public static void payRefund() {
		try {
			Map<String, Object> map = new HashMap<>();
			// 支付签名
			map.put("orderNum", "DD20170812091822181869161");
			map.put("refundMoney", "0.01");
			map.put("refundType", "Y");
			map.put("appKey", "8890415b6a6487ee12e9cb76ee9f9a66");
			String s = PaySignUtil.getSign(map, "fhCJ69JkdUDy807XFCIOrcyIDjBd5xDsGzUOUrM5");
			
			String paramStr = PaySignUtil.getParamStr(map) + "&sign=" + s;
			
			map.clear();
			
			map.put("paramStr", paramStr);
			
			// 接口请求
			String result = HttpUtil.httpPost("https://payapi.aijinfu.cn/payOrderRefundNew/payRefund.do", map);
			System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	* @Title: htbweixinpay 
	* @Description:汇付宝-微信公众号支付
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	*/
	public static void htbweixinpay() {
		try {
//			//			"bussOrderNum","orderName","payMoney","notifyUrl","returnUrl","goodsNote","remark","sign";
			Map<String, Object> map = new HashMap<>();
			long num=new Date().getTime();
			System.out.println("======订单号为："+num+"==========");
			map.put("bussOrderNum",String.valueOf(num));
			map.put("payMoney","0.01");
			map.put("appKey", "270461df13a412f373ae6c2771ccd926");
			map.put("notifyUrl", "http://qiuguojie.wicp.net/aiJinFuPay/companyCallBack.do");
			map.put("orderName", "公众号支付测试");
			map.put("returnUrl", "http://qiuguojie.wicp.net/success.html");
			map.put("goodsNote", "公众号支付测试");
			map.put("remark", "1111111");
			map.put("goods_num","100");
			String s = PaySignUtil.getSign(map, "be29c66b2d0b166c90d7a346209259149470faf76e53bf52b39d1a98a8d9250b");// 外网密钥
			String paramStr = PaySignUtil.getParamStr(map) + "&sign=" + s;
			// 参数进行URLEncoder转码
//			paramStr = URLEncoder.encode(paramStr);
//			System.out.println(paramStr);
			map.clear();
			map.put("paramStr", paramStr);
			// 接口请求
			String result = HttpUtil.httpPost("http://qiuguojie.wicp.net/GateWay/hfbWxGzhPay.do", map);
			System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			System.out.println("返回结果为========>"+json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	* @Title: wftweixinpay 
	* @Description: 威富通：微信公众号支付
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	*/
	public static void wftweixinpay() {
		try {
//			"bussOrderNum", "orderName", "payMoney", "notifyUrl","sign";
			Map<String, Object> map = new HashMap<>();
			// 支付签名
			long num=new Date().getTime();
			//System.out.println("======订单号为："+num+"==========");
			map.put("bussOrderNum",String.valueOf(num));
			map.put("payMoney",0.01);
			map.put("appKey", "270461df13a412f373ae6c2771ccd926");
			map.put("notifyUrl", "http://aijinfupay.tunnel.echomod.cn/aiJinFuPay/companyCallBack.do");
			map.put("orderName", "威富通公众号支付测试");
			String s = PaySignUtil.getSign(map, "be29c66b2d0b166c90d7a346209259149470faf76e53bf52b39d1a98a8d9250b");// 外网密钥
			String paramStr = PaySignUtil.getParamStr(map) + "&sign=" + s;
			// 参数进行URLEncoder转码
//			paramStr = URLEncoder.encode(paramStr);
//			System.out.println(paramStr);
			map.clear();
			map.put("paramStr", paramStr);
			// 接口请求
			String result = HttpUtil.httpPost("http://aijinfupay.tunnel.echomod.cn/GateWay/swiftWechatGzhPay.do", map);
			//System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			//System.out.println("返回结果为========>"+json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	* @Title: selectOrder 
	* @Description: 订单查询
	* @param     设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public static void selectOrder(){
		try{
			Map<String, Object> map = new HashMap<>();
			// 支付签名
			map.put("bussOrderNum", "1499915365087");
			map.put("appKey", "270461df13a412f373ae6c2771ccd926");
			String s = PaySignUtil.getSign(map, "be29c66b2d0b166c90d7a346209259149470faf76e53bf52b39d1a98a8d9250b");
			String paramStr = PaySignUtil.getParamStr(map) + "&sign=" + s;
			map.clear();
			map.put("paramStr", paramStr);
			// 接口请求
			String result = HttpUtil.httpPost("http://aijinfupay.tunnel.echomod.cn/pay/queryOrder.do", map);
			System.out.println(result);
			JSONObject json = JSONObject.parseObject(result);
			System.out.println(json);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * wap接口
	 * @param type  1本地2测试服3正式服
	 * @throws Exception
	 */
	public static void payment(Integer type,String payPlatform) throws Exception{
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("bussOrderNum", System.currentTimeMillis());
		map.put("orderName", "bbbb");
		map.put("notifyUrl", "https://testpaymg.aijinfu.cn/admin/testCallBack.do");
		//map.put("returnUrl", "https://pay.yuanbaomj.com/aijinfu/return");
		map.put("payMoney", "0.01");
		map.put("appType", "1");
		map.put("ip", "192.168.1.172");
		map.put("payPlatform", payPlatform);
		//map.put("remark", "dddd");
		if(type==1){
			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"l7Er4m02fekDCXEXY3LazoApr3A8AllXD1AxN3R1");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("http://192.168.1.172:8080/pay/payment.do?paramStr="+paramStr);
		}else if(type==2){
			map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//测试服appKey
			String s =PaySignUtil.getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("https://testpayapi.aijinfu.cn/pay/payment.do?paramStr="+paramStr);
		}else{
			map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");//正式服appKey
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("https://payapi.aijinfu.cn/pay/payment.do?paramStr="+paramStr);
		}
	}
	
	/**
	 * 多功能接口
	 * @param type  1本地2测试服3正式服
	 * @throws Exception 
	 */
	public static void multifunctionPayment(Integer type,String payPlatform) throws Exception{
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("bussOrderNum", System.currentTimeMillis());
		map.put("orderName", "bbbb");
		map.put("notifyUrl", "https://testpaymg.aijinfu.cn/admin/testCallBack.do");
		//map.put("returnUrl", "https://pay.yuanbaomj.com/aijinfu/return");
		map.put("payMoney", "0.01");
		map.put("appType", "1");
		map.put("ip", "192.168.1.172");
		map.put("payPlatform", payPlatform);
		map.put("remark", "{\"userid\":\"10002\",\"count\":\"200\"}");
		
		if(type==1){
			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"l7Er4m02fekDCXEXY3LazoApr3A8AllXD1AxN3R1");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("http://192.168.1.172:8080/pay/multifunctionPayment.do?paramStr="+paramStr);
		}else if(type==2){
			map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//测试服appKey
			String s =PaySignUtil.getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("https://testpayapi.aijinfu.cn/pay/multifunctionPayment.do?paramStr="+paramStr);
		}else{
			map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");//正式服appKey
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			paramStr = URLEncoder.encode(paramStr);
			System.out.println("https://payapi.aijinfu.cn/pay/multifunctionPayment.do?paramStr="+paramStr);
		}
		
	}
	
	/**
	 * 点游支付宝app支付
	 * @param type  1本地2测试服3正式服
	 * @throws Exception
	 */
	public static void alipayPage(Integer type) throws Exception{
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("bussOrderNum", System.currentTimeMillis());
		map.put("orderName", "bbbb");
		map.put("notifyUrl", "https://testpaymg.aijinfu.cn/admin/testCallBack.do");
		//map.put("returnUrl", "https://pay.yuanbaomj.com/aijinfu/return");
		map.put("payMoney", "0.01");
		//map.put("remark", "dddd");
		
		String url = "";
		if(type==1){
			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"l7Er4m02fekDCXEXY3LazoApr3A8AllXD1AxN3R1");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "http://qiuguojie.wicp.net/pay/alipayPage.do";
		}else if(type==2){
			map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//测试服appKey
			String s =PaySignUtil.getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://testpayapi.aijinfu.cn/pay/alipayPage.do";
		}else{
			map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");//正式服appKey
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://payapi.aijinfu.cn/pay/alipayPage.do";
		}
		long time = System.currentTimeMillis();
		// 接口请求
		String result = HttpUtil.httpPost(url, map);
		System.out.println(System.currentTimeMillis()-time);
		System.out.println(result);
		JSONObject json = JSONObject.parseObject(result);
		System.out.println("返回结果为========>"+json);
	}
	
	/**
	 * 点游支付宝wap支付
	 * @param type  1本地2测试服3正式服
	 * @throws Exception
	 */
	public static void wxpayPage(Integer type) throws Exception{
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("bussOrderNum", System.currentTimeMillis());
		map.put("orderName", "bbbb");
		map.put("notifyUrl", "https://testpaymg.aijinfu.cn/admin/testCallBack.do");
		//map.put("returnUrl", "https://pay.yuanbaomj.com/aijinfu/return");
		map.put("payMoney", "0.01");
		//map.put("remark", "dddd");
		String url = "";
		if(type==1){
			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"l7Er4m02fekDCXEXY3LazoApr3A8AllXD1AxN3R1");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "http://qiuguojie.wicp.net/pay/wxpayPage.do";
		}else if(type==2){
			map.put("appKey", "6413f866b558d3e2b6ccf4f0d865f9df");//测试服appKey
			String s =PaySignUtil.getSign(map,"u4smNesRMrDAIU62HXNy1eoeP9uD8yaUKCcd103j");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://testpayapi.aijinfu.cn/pay/wxpayPage.do";
		}else{
			map.put("appKey", "542f1a44e5576ef2ba53b40a9044cd42");//正式服appKey
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://payapi.aijinfu.cn/pay/wxpayPage.do";
		}
		map.put("appType", 1);
		long time = System.currentTimeMillis();
		// 接口请求
		String result = HttpUtil.httpPost(url, map);
		System.out.println(System.currentTimeMillis()-time);
		System.out.println(result);
		JSONObject json = JSONObject.parseObject(result);
		System.out.println("返回结果为========>"+json);
	}
	
	/**
	 * 罗马接口封装
	 * @param type  1本地2测试服3正式服
	 * @throws Exception
	 */
	public static void urlCon(Integer type) throws Exception{
		Map<String,Object> map = new HashMap<>();
		
		//支付签名
		map.put("url", URLEncoder.encode("http://qiuguojie.wicp.net/payDetail/tfbCallBack.do?qq=11&dfgsd=dsflg"));
		String url = "";
		if(type==1){
			map.put("mchNum","JF80000039");
			String s =PaySignUtil.getSign(map,"KXx404WYHaOWSX3f7bOswYjaWo0v00SA");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "http://qiuguojie.wicp.net/jkInfo/urlCon.do";
		}else if(type==2){
			map.put("mchNum","JF800000106");
			String s =PaySignUtil.getSign(map,"f0935e4cd5920aa6c7c996a5ee53a70f");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://testpayapi.aijinfu.cn/jkInfo/urlCon.do";
		}else{
			map.put("mchNum","JF80000039");
			String s =PaySignUtil.getSign(map,"wB8enVfNHs5NRIoAosCiDMEsPEpgib5r");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://payapi.aijinfu.cn/jkInfo/urlCon.do";
		}
		long time = System.currentTimeMillis();
		// 接口请求
		String result = HttpsUtil.doPostString(url, map, "utf-8");
		System.out.println(System.currentTimeMillis()-time);
		System.out.println(result);
		JSONObject json = JSONObject.parseObject(result);
		System.out.println("返回结果为========>"+json);
	}
	/**
	 * 
	 * df 
	 * 代付测试
	 * @param type    设定文件 
	 * void    返回类型
	 */
	public static void df(Integer type) throws Exception{
		Map<String,Object> map = new HashMap<>();
		map.put("payMoney", "0.17");
		map.put("payType", "1");
		map.put("acctName", "周立波");
		map.put("acctNum", "6214856554609846");
		map.put("memo", "金服代付");
		map.put("acctType", "0");
		Long merchantOrderNum=System.currentTimeMillis();
		System.out.println("商户订单号："+merchantOrderNum);
		map.put("merchantOrderNum",merchantOrderNum+"");
		map.put("orderTime",new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		String url = "";
		if(type==1){
			map.put("mchNum","JF800000205");
//			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"wxP1XCQlH0SOYxajlI47NrPE5XtV1tJN");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "http://aijinfupay.tunnel.echomod.cn/dfPay/singlePay.do";
		}else if(type==2){
			map.put("mchNum","JF800000114");
			String s =PaySignUtil.getSign(map,"afe868bb1edf5054ac1a9aec46d65305");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://testpayapi.aijinfu.cn/dfPay/singlePay.do";
		}else{
			map.put("mchNum","JF800000205");
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://payapi.aijinfu.cn/dfPay/singlePay.do";
		}
		long time = System.currentTimeMillis();
		// 接口请求
		String result = HttpsUtil.doPostString(url, map, "utf-8");
		System.out.println(System.currentTimeMillis()-time);
		System.out.println(result);
		JSONObject json = JSONObject.parseObject(result);
		System.out.println("返回结果为========>"+json);
	}
	/**
	 * 代付订单查询
	 * @param type
	 * @throws Exception
	 */
	public static void dfOrder(Integer type) throws Exception{
		Map<String,Object> map = new HashMap<>();
		map.put("merchantOrderNum","1508897223775");
		String url = "";
		if(type==1){
			map.put("mchNum","JF800000205");
//			map.put("appKey", "76dd814dad76c9f9d3634e490b260c19");//内网appKey
			String s =PaySignUtil.getSign(map,"wxP1XCQlH0SOYxajlI47NrPE5XtV1tJN");//内网密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "http://aijinfupay.tunnel.echomod.cn/dfPay/singleQuery.do";
		}else if(type==2){
			map.put("mchNum","JF800000114");
			String s =PaySignUtil.getSign(map,"afe868bb1edf5054ac1a9aec46d65305");//测试服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://testpayapi.aijinfu.cn/dfPay/singleQuery.do";
		}else{
			map.put("mchNum","JF800000205");
			String s =PaySignUtil.getSign(map,"91dX0t4N3C0CK6UD9r90zhJCfXG00vjTUab91hXN");//正式服密钥
			String paramStr = PaySignUtil.getParamStr(map)+"&sign="+s;
			map.clear();
			map.put("paramStr", paramStr);
			url = "https://payapi.aijinfu.cn/dfPay/singleQuery.do";
		}
		long time = System.currentTimeMillis();
		// 接口请求
		String result = HttpsUtil.doPostString(url, map, "utf-8");
		System.out.println(System.currentTimeMillis()-time);
		System.out.println(result);
		JSONObject json = JSONObject.parseObject(result);
		System.out.println("返回结果为========>"+json);
	}
	
	/**
	 * 回调测试
	 * @param paramStr 参数字符串{sign=cfa348bf1be16344ba770d17abbda9e0, result_code=200, pay_money=0.01, order_num=DD20171028175600821167170, pay_time=20171028175754, pay_discount_money=0.01, buss_order_num=201710281757476}
	 * @param notifyUrl	回调地址
	 * @throws Exception
	 */
	public static void notify(String paramStr,String notifyUrl) throws Exception{
		String [] array = paramStr.substring(1,paramStr.length()-1).split(",");
		Map<String, Object> param = new HashMap<>();
		for(int i=0; i<array.length; i++) {
		  String[] data = array[i].split("=");
		  param.put(data[0].trim(),data[1]);
		}
		String result = "";
		if(notifyUrl.startsWith("https")){
			result = HttpsUtil.doPostString(notifyUrl, param, "utf-8");
		}else{
			// 通知商户
			result = HttpUtil.httpPost(notifyUrl, param);
		}
		System.out.println("回调商户接收：【"+result+"】");
		System.out.println(result.toCharArray().length);
		System.out.println("SUCCESS".equals(result.toUpperCase()));
	}
}
