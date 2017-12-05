package com.pay.business.util;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.http.HttpUtil;
import com.core.teamwork.base.util.properties.PropertiesUtil;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.pay.business.util.alipay.AliPay;
import com.pay.business.util.alipay.AppAliPay;
import com.pay.business.util.alipay.PayConfigApi;
import com.pay.business.util.alipayZz.AliZzPay;
import com.pay.business.util.guofu.GuoFuPay;
import com.pay.business.util.hfbpay.HfbPay;
import com.pay.business.util.mail.MailRun;
import com.pay.business.util.minshengbank.HttpMinshengBank;
import com.pay.business.util.minshengbank.junhu.JunHuPay;
import com.pay.business.util.minshengbank.xm.MinShengXMPay;
import com.pay.business.util.nowpay.NowPayUtil;
import com.pay.business.util.pfSZ.PFSZBankPay;
import com.pay.business.util.pinganbank.config.TestParams;
import com.pay.business.util.pinganbank.pay.PABankPay;
import com.pay.business.util.shande.ShandePay;
import com.pay.business.util.suiningYH.SNBankPay;
import com.pay.business.util.swtPay.SWTPay;
import com.pay.business.util.wftpay.WftWeChatPay;
import com.pay.business.util.wftpay.WftWechatConfig;
import com.pay.business.util.wukaPay.WukaPay;
import com.pay.business.util.xyBankWeChatPay.XyBankPay;
import com.pay.business.util.xyShenzhen.XYSZBankPay;
import com.pay.business.util.zsxmPay.ZsxmPay;

/**
 * 支付字典的值（对应数据库）
 * @author Administrator
 *
 */
public class PayRateDictValue {
	
	private static final Log logger = LogFactory.getLog(PayRateDictValue.class);
	/**
	 * 接入支付类型
	 */
	public static final String PAY_TYPE ="PAY_TYPE";
	/**
	 * 支付宝
	 */
	public static final String PAY_TYPE_ALIPAY ="PAY_TYPE_ALIPAY";
	/**
	 * 微信支付
	 */
	public static final String PAY_TYPE_WEIXIN ="PAY_TYPE_WEIXIN";
	/**
	 * 威富通微信wap支付
	 */
	public static final String PAY_TYPE_WFT_WEIXIN ="PAY_TYPE_WFT_WEIXIN_WAP";
	
	/**
	 * 威富通微信APP支付
	 */
	public static final String PAY_TYPE_WFT_WEIXIN_APP ="PAY_TYPE_WFT_WEIXIN_APP";
	
	/**
	 * 威富通QQ扫码支付
	 */
	public static final String PAY_TYPE_WFT_QQ_SCAN ="PAY_TYPE_WFT_QQ_SCAN";
	/**
	 * 威富通支付宝扫码支付
	 */
	public static final String PAY_TYPE_WFT_ALI_SCAN ="PAY_TYPE_WFT_ALI_SCAN";
	/**
	 * 威富通微信扫码支付
	 */
	public static final String PAY_TYPE_WFT_WEIXIN_SCAN ="PAY_TYPE_WFT_WEIXIN_SCAN";
	
	/**
	 * 微信官方APP支付
	 */
	public static final String PAY_TYPE_GF_WEIXIN_APP = "PAY_TYPE_GF_WEIXIN_APP";
	
	/**
	 * 支付宝app支付
	 */
	public static final String PAY_TYPE_ALIPAY_APP ="PAY_TYPE_ALIPAY_APP";
	/**
	 * 支付宝条码支付
	 */
	public static final String PAY_TYPE_ALIPAY_SCAN ="PAY_TYPE_ALIPAY_SCAN";
	/**
	 * 微信刷卡支付
	 */
	public static final String PAY_TYPE_WEIXIN_SCAN ="PAY_TYPE_WEIXIN_SCAN";
	/**
	 * 支付宝web支付
	 */
	public static final String PAY_TYPE_ALIPAY_WEB ="PAY_TYPE_ALIPAY_WAP";
	/**
	 * 有氧支付宝app支付
	 */
	public static final String PAY_TYPE_ALIPAY_YY ="PAY_TYPE_ALIPAY_YY";
	/**
	 * 支付宝扫码支付直连通道一
	 */
	public static final String PAY_TYPE_ALIPAY_SMZL ="PAY_TYPE_ALIPAY_SCAN";
	/**
	 * 汇付宝微信h5支付
	 */
	public static final String PAY_TYPE_HFB_WX_WEB ="PAY_TYPE_HFB_WEIXIN_WAP";
	/**
	 * 汇付宝微信app支付
	 */
	public static final String PAY_TYPE_HFB_WX_SDK ="PAY_TYPE_HFB_WEIXIN_APP";
	/**
	 * 汇付宝支付宝APP支付
	 */
	public static final String PAY_TYPE_HFB_ALI_SDK ="PAY_TYPE_HFB_ALI_APP";
	/**
	 * 汇付宝微信公众号支付
	 */
	public static final String PAY_TYPE_HFB_WEIXIN_GZH ="PAY_TYPE_HFB_WEIXIN_GZH";
	/**
	 * 威富通微信公众号支付
	 */
	public static final String PAY_TYPE_WFT_WEIXIN_GZH ="PAY_TYPE_WFT_WEIXIN_GZH";
	/**
	 * 兴业银行微信公众号支付
	 */
	public static final String PAY_TYPE_XYBANk_WEIXIN_GZH ="PAY_TYPE_XYBANk_WEIXIN_GZH";
	/**
	 * 兴业银行支付宝扫码支付
	 */
	public static final String PAY_TYPE_XYBANk_ALI_SCAN ="PAY_TYPE_XYBANk_ALI_SCAN";
	/**
	 * 兴业银行微信扫码支付
	 */
	public static final String PAY_TYPE_XYBANk_WEIXIN_SCAN ="PAY_TYPE_XYBANk_WEIXIN_SCAN";
	/**
	 * 民生银行支付宝扫码支付
	 */
	public static final String PAY_TYPE_MSBANk_ALI_SCAN="PAY_TYPE_MSBANk_ALI_SCAN";
	/**
	 * 民生银行微信扫码支付
	 */
	public static final String PAY_TYPE_MSBANk_WEIXIN_SCAN="PAY_TYPE_MSBANk_WEIXIN_SCAN";
	/**
	 * 民生银行微信公众号支付
	 */
	public static final String PAY_TYPE_MSBANk_WEIXIN_GZH="PAY_TYPE_MSBANk_WEIXIN_GZH";
	/**
	 * 平安银行微信扫码支付
	 */
	public static final String PAY_TYPE_PABANk_WEIXIN_SCAN="PAY_TYPE_PABANk_WEIXIN_SCAN";
	/**
	 * 平安银行支付宝扫码支付
	 */
	public static final String PAY_TYPE_PABANk_ALI_SCAN="PAY_TYPE_PABANk_ALI_SCAN";
	/**
	 * 平安银行公众号支付（特殊）
	 */
	public static final String PAY_TYPE_PABANk_GZH_WEIXIN_SCAN="PAY_TYPE_PABANk_GZH_WEIXIN_SCAN";
	/**
	 * 平安银行公众号支付
	 */
	public static final String PAY_TYPE_PABANk_WEIXIN_GZH="PAY_TYPE_PABANk_WEIXIN_GZH";
	/**
	 * 民生银行QQ扫码支付
	 */
	public static final String PAY_TYPE_MSBANk_QQ_SCAN="PAY_TYPE_MSBANk_QQ_SCAN";
	
	/**
	 * 兴业深圳支付宝扫码
	 */
	public static final String PAY_TYPE_XYSZ_ALI_SCAN="PAY_TYPE_XYSZ_ALI_SCAN";
	
	/**
	 * 兴业深圳微信扫码
	 */
	public static final String PAY_TYPE_XYSZ_WEIXIN_SCAN = "PAY_TYPE_XYSZ_WEIXIN_SCAN";
	
	/**
	 * 兴业深圳微信WAP
	 */
	public static final String PAY_TYPE_XYSZ_WEIXIN_WAP = "PAY_TYPE_XYSZ_WEIXIN_WAP";
	
	/**
	 * 兴业深圳微信公众号
	 */
	public static final String PAY_TYPE_XYSZ_WEIXIN_GZH = "PAY_TYPE_XYSZ_WEIXIN_GZH";
	
	/**
	 * 兴业深圳QQ扫码支付
	 */
	public static final String PAY_TYPE_XYSZ_QQ_SCAN ="PAY_TYPE_XYSZ_QQ_SCAN";
	
	/**
	 * 国付QQ被扫码
	 */
	public static final String PAY_TYPE_GUOFU_PASSIVE_QQ_SCAN = "PAY_TYPE_GUOFU_PASSIVE_QQ_SCAN";
	
	/**
	 * 国付支付宝被扫码
	 */
	public static final String PAY_TYPE_GUOFU_PASSIVE_ALI_SCAN = "PAY_TYPE_GUOFU_PASSIVE_ALI_SCAN";
	
	/**
	 * 国付微信被扫码
	 */
	public static final String PAY_TYPE_GUOFU_PASSIVE_WEIXIN_SCAN = "PAY_TYPE_GUOFU_PASSIVE_WEIXIN_SCAN";
	
	/**
	 * 民生厦门QQ扫码
	 */
	public static final String PAY_TYPE_MSXM_QQ_SCAN = "PAY_TYPE_MSXM_QQ_SCAN";
	
	/**
	 * 民生厦门阿里扫码
	 */
	public static final String PAY_TYPE_MSXM_ALI_SCAN = "PAY_TYPE_MSXM_ALI_SCAN";
	
	/**
	 * 民生厦门微信扫码
	 */
	public static final String PAY_TYPE_MSXM_WEIXIN_SCAN = "PAY_TYPE_MSXM_WEIXIN_SCAN";
	
	/**
	 * 骏狐QQ扫码
	 */
	public static final String PAY_TYPE_JUNHU_QQ_SCAN = "PAY_TYPE_JUNHU_QQ_SCAN";
	
	/**
	 * 骏狐支付宝扫码
	 */
	public static final String PAY_TYPE_JUNHU_ALI_SCAN = "PAY_TYPE_JUNHU_ALI_SCAN";
	
	/**
	 * 骏狐微信扫码
	 */
	public static final String PAY_TYPE_JUNHU_WEIXIN_SCAN = "PAY_TYPE_JUNHU_WEIXIN_SCAN";
	
	/**
	 * 浦发支付宝扫码
	 */
	public static final String PAY_TYPE_PFBANK_ALI_SCAN = "PAY_TYPE_PFBANK_ALI_SCAN";
	/**
	 * 浦发微信扫码
	 */
	public static final String PAY_TYPE_PFBANK_WEIXIN_SCAN = "PAY_TYPE_PFBANK_WEIXIN_SCAN";
	/**
	 * 浦发微信H5
	 */
	public static final String PAY_TYPE_PFBANK_WEIXIN_WEB = "PAY_TYPE_PFBANK_WEIXIN_WAP";
	
	/**
	 * 遂宁支付宝扫码
	 */
	public static final String PAY_TYPE_SNBANK_ALI_SCAN = "PAY_TYPE_SNBANK_ALI_SCAN";
	
	/**
	 * 遂宁微信扫码
	 */
	public static final String PAY_TYPE_SNBANK_WEIXIN_SCAN = "PAY_TYPE_SNBANK_WEIXIN_SCAN";
	
	/**
	 * 遂宁QQ扫码
	 */
	public static final String PAY_TYPE_SNBANK_QQ_SCAN = "PAY_TYPE_SNBANK_QQ_SCAN";
	
	/**
	 * 遂宁微信WAP
	 */
	public static final String PAY_TYPE_SNBANK_WEIXIN_WAP = "PAY_TYPE_SNBANK_WEIXIN_WAP";
	
	/**
	 * 遂宁微信公众号
	 */
	public static final String PAY_TYPE_SNBANK_WEIXIN_GZH = "PAY_TYPE_SNBANK_WEIXIN_GZH";
	
	/**
	 * 商务通微信扫码
	 */
	public static final String PAY_TYPE_SWT_WEIXIN_SCAN = "PAY_TYPE_SWT_WEIXIN_SCAN";
	
	/**
	 * 商务通支付宝扫码
	 */
	public static final String PAY_TYPE_SWT_ALI_SCAN = "PAY_TYPE_SWT_ALI_SCAN";
	
	/**
	 * 现在支付支付宝WAP
	 */
	public static final String PAY_TYPE_NOW_ALI_WAP = "PAY_TYPE_NOW_ALI_WAP";
	
	/**
	 * 现在支付微信WAP
	 */
	public static final String PAY_TYPE_NOW_WEIXIN_WAP = "PAY_TYPE_NOW_WEIXIN_WAP";
	
	/**
	 * 现在支付支付宝扫码
	 */
	public static final String PAY_TYPE_NOW_ALI_SCAN = "PAY_TYPE_NOW_ALI_SCAN";
	
	/**
	 * 现在支付微信扫码
	 */
	public static final String PAY_TYPE_NOW_WEIXIN_SCAN = "PAY_TYPE_NOW_WEIXIN_SCAN";
	
	/**
	 * 杉德微信扫码
	 */
	public static final String PAY_TYPE_SD_WEIXIN_SCAN = "PAY_TYPE_SD_WEIXIN_SCAN";
	
	/**
	 * 杉德支付宝扫码
	 */
	public static final String PAY_TYPE_SD_ALI_SCAN = "PAY_TYPE_SD_ALI_SCAN";
	
	/**
	 * 支付宝转账模式WAP
	 */
	public static final String PAY_TYPE_ZZ_ALI_WAP = "PAY_TYPE_ZZ_ALI_WAP";
	
	/**
	 * 无卡QQ扫码
	 */
	public static final String PAY_TYPE_WUKA_QQ_SCAN = "PAY_TYPE_WUKA_QQ_SCAN";
	
	/**
	 * 无卡微信扫码
	 */
	public static final String PAY_TYPE_WUKA_WEIXIN_SCAN = "PAY_TYPE_WUKA_WEIXIN_SCAN";
	
	/**
	 * 无卡微信WAP
	 */
	public static final String PAY_TYPE_WUKA_WEIXIN_WAP = "PAY_TYPE_WUKA_WEIXIN_WAP";
	
	/**
	 * 无卡支付宝WAP
	 */
	public static final String PAY_TYPE_WUKA_ALI_WAP = "PAY_TYPE_WUKA_ALI_WAP";
	
	/**
	 * 招行厦门微信扫码
	 */
	public static final String PAY_TYPE_ZSXM_WEIXIN_SCAN = "PAY_TYPE_ZSXM_WEIXIN_SCAN";
	
	/**
	 * 招行厦门支付宝扫码
	 */
	public static final String PAY_TYPE_ZSXM_ALI_SCAN = "PAY_TYPE_ZSXM_ALI_SCAN";
	
	/**
	 * 调用支付通道
	 * @param dictName
	 * @param orderMap
	 * @param appId
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static Map<String,String> ratePay(String dictName,Map<String,String> orderMap
			,String ip,String appType,String dictNameLike) throws Exception{
		Map<String, String> resultMap = new HashMap<>();
		String payTypes = "";
		String orderNum = orderMap.get("orderNum");
		String merchantOrderNum = orderMap.get("merchantOrderNum")==null?orderNum:orderMap.get("merchantOrderNum");
		double payMoney = Double.valueOf(orderMap.get("payMoney"));
		String yuanToCents = DecimalUtil.yuanToCents(orderMap.get("payMoney"));
		//String orderName = orderMap.get("orderName").toString();
		String returnUrl = PayConfigApi.H5_RETURN_URL;
		String hfbStr = "";
		//是否公众号支付
		Integer is_GZH = 0;
		long time = System.currentTimeMillis();
		switch (dictName) {
			case PAY_TYPE_ALIPAY_APP:
				BigDecimal b = new BigDecimal(payMoney);
				double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				// 调用支付宝支付
				String aliPay = AppAliPay.sign(orderNum, "" + f1
						, merchantOrderNum,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
				resultMap.put("aliPay", aliPay);
				break;
			case PAY_TYPE_WFT_WEIXIN:
				
				Map<String, String> wxMap = WftWeChatPay.pay(merchantOrderNum,
						orderNum, "" + DecimalUtil.yuanToCents(payMoney+""), ip,appType
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"),dictName,dictNameLike);
				if (wxMap.containsKey("token_id")||wxMap.containsKey("pay_info")) {
					resultMap.put("token_id", wxMap.get("token_id"));
					resultMap.put("services", wxMap.get("services"));
					resultMap.put("webStr", wxMap.get("pay_info"));
				} else {
					//发送错误信息邮件
					MailRun.send(
							dictName,wxMap.get("msg"),
							orderNum,
							orderMap.get("rateId"),
							orderMap.get("appName"),""+payMoney,
							resultMap.get("companyName"),
							orderMap.get("rateCompanyName"),
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_WFT_ALI_SCAN:
			case PAY_TYPE_WFT_WEIXIN_SCAN:
			case PAY_TYPE_WFT_QQ_SCAN:
				String service =WftWechatConfig.PAY_SERVICE_ALI;
				if(PAY_TYPE_WFT_WEIXIN_SCAN.equals(dictName)){
					service =WftWechatConfig.PAY_SERVICE_WX;
				}else if(PAY_TYPE_WFT_QQ_SCAN.equals(dictName)){
					service =WftWechatConfig.PAY_SERVICE_QQ;
				}
				wxMap = WftWeChatPay.scanPay(merchantOrderNum,
						orderNum, "" + DecimalUtil.yuanToCents(payMoney+""), ip
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"),service);
				if (wxMap.containsKey("code_url")) {
					resultMap.put("webStr", wxMap.get("code_url"));
				} else {
					//发送错误信息邮件
					MailRun.send(
							dictName,"威富通wap下单请求错误",
							orderNum,
							orderMap.get("rateId"),
							orderMap.get("appName"),
							""+payMoney,resultMap.get("companyName"),
							orderMap.get("rateCompanyName"),
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;	
			case PAY_TYPE_ALIPAY_WEB:
				String form = AliPay.alipayH5(returnUrl,orderNum, merchantOrderNum, payMoney
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"),orderMap.get("rateKey3"));
				resultMap.put("alipayStr", form);
				break;
			case PAY_TYPE_HFB_WX_SDK:
				hfbStr = HfbPay.sdkPay(orderNum, payMoney, ip, merchantOrderNum, new Date(),2
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
				if("".equals(hfbStr)){
					//发送错误信息邮件
					MailRun.send(
							dictName,
							"汇付宝微信sdk下单请求错误",
							orderNum,
							orderMap.get("rateId"),orderMap.get("appName"),
							""+payMoney,orderMap.get("companyName"),
							orderMap.get("rateCompanyName"),
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				resultMap.put("hfbStr", hfbStr);
				break;
			case PAY_TYPE_HFB_WX_WEB:
				hfbStr = HfbPay.webPay(orderNum, payMoney, ip, merchantOrderNum, new Date(),2
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
				if("".equals(hfbStr)){
					//发送错误信息邮件
					MailRun.send(
							dictName,"汇付宝微信web下单请求错误",
							orderNum,
							orderMap.get("rateId"),
							orderMap.get("appName"),
							""+payMoney,orderMap.get("companyName"),
							orderMap.get("rateCompanyName"),
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				resultMap.put("webStr", hfbStr);
				break;
			case PAY_TYPE_HFB_ALI_SDK:
				hfbStr = HfbPay.sdkPay(orderNum, payMoney, ip, merchantOrderNum, new Date(),1
						,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
				if("".equals(hfbStr)){
					//发送错误信息邮件
					MailRun.send(
							dictName,"汇付宝支付宝sdk下单请求错误",
							orderNum,
							orderMap.get("rateId"),
							orderMap.get("appName"),
							""+payMoney,orderMap.get("companyName"),
							orderMap.get("rateCompanyName"),
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				resultMap.put("hfbStr", hfbStr);
				break;
			case PAY_TYPE_ALIPAY_SMZL:
				logger.info("=======>扫码支付：走了支付宝扫码支付通道<=======");
				// 30分失效
				int timeOut = 30;
				String APPID = orderMap.get("rateKey1");
				String PKCS8_PRIVATE =orderMap.get("rateKey2");
				String ALIPAY_RSA_PUBLIC =orderMap.get("rateKey3");
				if (APPID != null && PKCS8_PRIVATE != null && ALIPAY_RSA_PUBLIC != null) {
					Map<String, String> aliPayMap = AliPay.aliTradePrecreatePay(orderNum, merchantOrderNum, payMoney, timeOut, APPID, PKCS8_PRIVATE, ALIPAY_RSA_PUBLIC);
					if(!StringUtils.isEmpty(aliPayMap.get("qr_code"))){
						resultMap.put("webStr", aliPayMap.get("qr_code"));
					}else{
						//发送错误信息邮件
						MailRun.send(
								orderMap.get("dictName"),
								aliPayMap.get("msg"),
								orderNum,
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"),
								orderMap.get("companyName"),
								orderMap.get("rateCompanyName"),
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}
				}else {
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				if(resultMap.get("webStr")==null||resultMap.get("webStr").equals("")){
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_WFT_WEIXIN_GZH:
				is_GZH = 1;
				break;
			case PAY_TYPE_HFB_WEIXIN_GZH:
				is_GZH = 1;
				break;
			case PAY_TYPE_XYBANk_WEIXIN_GZH:
				is_GZH = 1;
				break;
			case PAY_TYPE_MSBANk_WEIXIN_GZH:
				is_GZH = 1;
				break;
			case PAY_TYPE_XYBANk_ALI_SCAN:
				
				logger.info("=======>扫码支付：走了兴业银行支付宝扫码支付通道<=======");
				String appid=orderMap.get("rateKey1");
				String mch_id=orderMap.get("rateKey2");
				String key=orderMap.get("rateKey3");
				if(appid!=null&&mch_id!=null&&key!=null){
					// 金额
					String total_fee= DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					Map<String, String> aliPayMap =AliPay.xyBankAliaScanPay(orderNum, total_fee, merchantOrderNum, appid, mch_id, key);
					if (Integer.valueOf(aliPayMap.get("code").toString()) != 10000) {
						//发送错误信息邮件
						MailRun.send(
								orderMap.get("dictName"),
								aliPayMap.get("msg"),
								orderNum,
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"),
								orderMap.get("companyName"),
								orderMap.get("rateCompanyName"),
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}else{
						resultMap.put("webStr", aliPayMap.get("qr_code"));
					}
				}else{
					logger.error("=======>兴业银行支付宝扫码支付通道：相关数据库配置不完整：rateKey1，rateKey2，rateKey3");
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return orderMap;
				}
				
				break;
			case PAY_TYPE_XYBANk_WEIXIN_SCAN:
				
				logger.info("=====》扫码支付：支付走了：兴业银行：微信扫码支付通道");
				appid=String.valueOf(orderMap.get("rateKey1"));
				mch_id=String.valueOf(orderMap.get("rateKey2"));
				key=String.valueOf(orderMap.get("rateKey3"));
				if(appid!=null&&mch_id!=null&&key!=null){
					String totalFee = DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					//支付来源：1:公众号支付：2扫码支付
					int fromType=2;
					Map<String, String> aliPayMap=XyBankPay.xYBankWechatGzhPay(orderNum, ip, totalFee, merchantOrderNum, appid, mch_id, key,null,null,fromType);
					//成功
					if (Integer.valueOf(aliPayMap.get("code").toString()) != 10000) {
						//发送错误信息邮件
						MailRun.send(
								orderMap.get("dictName"), 
								aliPayMap.get("msg"),
								orderNum,
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"),
								orderMap.get("companyName"),
								orderMap.get("rateCompanyName"),
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}else{
						resultMap.put("webStr", aliPayMap.get("qr_code"));
					}
				}else{
					logger.error("=======>兴业银行：微信扫码支付通道：相关数据库配置不完整：rateKey1，rateKey2，rateKey3");
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				
				break;
			case PAY_TYPE_MSBANk_ALI_SCAN:	
			case PAY_TYPE_MSBANk_QQ_SCAN:
				
				
				logger.info("=======>扫码支付：走了民生银行支付宝扫码支付通道<=======");
				Map<String,Object> paramMap=new HashMap<String, Object>();
				//商户号
				String merNo=orderMap.get("rateKey1");
				//商户key
				String bankSecretKey=orderMap.get("rateKey2");
				if(merNo!=null&&bankSecretKey!=null){
					//商户id
					paramMap.put("merNo",merNo);
					//支付集订单号
					paramMap.put("orderNo", orderNum);
					//支付宝扫码支付渠道
					if(PAY_TYPE_MSBANk_QQ_SCAN.equals(dictName)){
						paramMap.put("channelFlag","04");	//qq
					}else {
						paramMap.put("channelFlag","01");
					}
					//商品描述
//					paramMap.put("goodsName",orderMap.get("orderName"));
					paramMap.put("goodsName",merchantOrderNum);
					// 金额
					String amount=DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					paramMap.put("amount",amount);
					//交易流水号
					paramMap.put("reqId",new Date().getTime()+RandomUtil.generateString(4));
					//交易时间
					paramMap.put("reqTime",DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
					Map<String, String> aliPayMap=HttpMinshengBank.minshengBankCallBack(paramMap, bankSecretKey);
					if (Integer.valueOf(aliPayMap.get("code").toString()) != 10000) {
						//发送错误信息邮件
						MailRun.send(
								orderMap.get("dictName"), 
								aliPayMap.get("msg"),
								orderMap.get("orderNum"),
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"),
								orderMap.get("companyName"),
								orderMap.get("rateCompanyName"),
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}else{
						resultMap.put("webStr", aliPayMap.get("qr_code"));
					}
				}else{
					logger.error("=======>民生银行支付宝扫码支付通道：相关数据库配置不完整：rateKey1，rateKey2，rateKey3");
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				
				if(resultMap.get("webStr")==null||resultMap.get("webStr").equals("")){
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_MSBANk_WEIXIN_SCAN:	
				logger.info("=======>扫码支付：走了民生银行微信扫码支付支付通道<=======");
				//商户号
				merNo=orderMap.get("rateKey1");
				//商户key
				bankSecretKey=orderMap.get("rateKey2");
				if(merNo!=null&&bankSecretKey!=null){
					paramMap=new HashMap<String, Object>();
					//商户id
					paramMap.put("merNo",merNo);
					//支付集订单号
					paramMap.put("orderNo", orderNum);
					//微信扫码支付渠道
					paramMap.put("channelFlag","00");
					//商品描述
//					paramMap.put("goodsName",orderMap.get("orderName"));
					paramMap.put("goodsName",merchantOrderNum);
					// 金额
					String amount=DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					paramMap.put("amount",amount);
					//交易流水号
					paramMap.put("reqId",new Date().getTime()+RandomUtil.generateString(4));
					//交易时间
					paramMap.put("reqTime",DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
					Map<String, String> aliPayMap=HttpMinshengBank.minshengBankCallBack(paramMap, bankSecretKey);
					if (Integer.valueOf(aliPayMap.get("code").toString()) != 10000) {
						//发送错误邮件
						MailRun.send(
								orderMap.get("dictName"), 
								aliPayMap.get("msg"),
								orderMap.get("orderNum"),
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"),
								orderMap.get("companyName"),
								orderMap.get("rateCompanyName"),
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}else{
						resultMap.put("webStr", aliPayMap.get("qr_code"));
					}
				}else{
					logger.error("=======>民生银行微信扫码支付通道：相关数据库配置不完整：rateKey1，rateKey2，rateKey3");
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				if(resultMap.get("webStr")==null||resultMap.get("webStr").equals("")){
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_PABANk_ALI_SCAN:
			case PAY_TYPE_PABANk_WEIXIN_SCAN:
			case PAY_TYPE_PABANk_GZH_WEIXIN_SCAN:
				String OPEN_ID=orderMap.get("rateKey1");
				String OPEN_KEY=orderMap.get("rateKey2");
				if(OPEN_ID!=null&&OPEN_KEY!=null){
					String outNo = orderNum;
					String pmtTag = "";
					if (dictName.equals(PayRateDictValue.PAY_TYPE_PABANk_WEIXIN_SCAN)
							||dictName.equals(PayRateDictValue.PAY_TYPE_PABANk_GZH_WEIXIN_SCAN)) {
						// 微信:这里是微信扫码
						pmtTag = "Weixin";
					}
					if (orderMap.get("dictName").equals(PayRateDictValue.PAY_TYPE_PABANk_ALI_SCAN)) {
						// 支付宝为：这里是支付宝扫码
						pmtTag = "AlipayPAZH";
					}
					String ordName = merchantOrderNum;
					// 金额
					String originalAmount = DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					// 实际金额
					String tradeAmount = DecimalUtil.yuanToCents(orderMap.get("payMoney"));
					// 异步回调URL
					String notifyUrl = TestParams.NOTIFY_URL;
					//这个参数是区别是否是公众号支付：公众号支付必传参数
					String jumpUrl=null;
					if(dictName.equals(PayRateDictValue.PAY_TYPE_PABANk_GZH_WEIXIN_SCAN)){
						logger.info("=======>走了：平安银行特殊公众号支付通道<=======");
						jumpUrl=TestParams.JUMP_URL;
					}else{
						logger.info("=======>扫码支付： 平安银行微信 ，支付宝，扫码支付通道<=======");
					}
					Map<String, String> paMap = PABankPay.queryOrder(outNo, pmtTag, null, ordName, Integer.valueOf(originalAmount), null, null,
							Integer.valueOf(tradeAmount), null, null, null, null, null, null, jumpUrl, notifyUrl,OPEN_ID,OPEN_KEY,null,null,1);
					if (Integer.valueOf(paMap.get("code").toString()) != 10000) {
						//发送错误邮件
						MailRun.send(
								orderMap.get("dictName"), 
								paMap.get("msg"), 
								orderMap.get("orderNum"), 
								orderMap.get("rateId"),
								orderMap.get("appName"),
								orderMap.get("payMoney"), 
								orderMap.get("companyName"), 
								orderMap.get("rateCompanyName"), 
								orderMap.get("payWayName"));
						resultMap.put("status", PayFinalUtil.RATE_FAIL);
						return resultMap;
					}else{
						resultMap.put("webStr", paMap.get("qr_code"));
					}
				}else{
					logger.error("=======>平安银行：微信 ，支付宝，扫码支付付通道：相关数据库配置不完整：rateKey1，rateKey2，rateKey5");
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_XYSZ_ALI_SCAN:
				Map<String, String> xyBankWFTAliaScanPay = XYSZBankPay.xySZWFTAliaScanPay(orderNum, yuanToCents, merchantOrderNum, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("兴业深圳阿里扫码" + xyBankWFTAliaScanPay.toString());
				if("10000".equals(xyBankWFTAliaScanPay.get("code"))){
					resultMap.put("webStr", xyBankWFTAliaScanPay.get("qr_code"));
				}else {
					//发送错误邮件
					MailRun.send(
							orderMap.get("dictName"), 
							xyBankWFTAliaScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_XYSZ_WEIXIN_SCAN:
				Map<String, String> xyBankWFTWXScanPay = XYSZBankPay.xySZWFTWXScanPay(orderNum, yuanToCents, merchantOrderNum, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("兴业深圳微信扫码" + xyBankWFTWXScanPay.toString());
				if("10000".equals(xyBankWFTWXScanPay.get("code"))){
					resultMap.put("webStr", xyBankWFTWXScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							xyBankWFTWXScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_XYSZ_WEIXIN_WAP:
				Map<String, String> xySZWFTWXWapPay = XYSZBankPay.xySZWFTWXWapPay(merchantOrderNum, orderNum, yuanToCents, ip, appType, orderMap.get("rateKey1"), orderMap.get("rateKey2"), dictNameLike);
				System.out.println("兴业深圳微信WAP" + xySZWFTWXWapPay.toString());
				if (xySZWFTWXWapPay.containsKey("token_id")||xySZWFTWXWapPay.containsKey("pay_info")) {
					resultMap.put("token_id", xySZWFTWXWapPay.get("token_id"));
					resultMap.put("services", xySZWFTWXWapPay.get("services"));
					resultMap.put("webStr", xySZWFTWXWapPay.get("pay_info"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							xySZWFTWXWapPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_XYSZ_QQ_SCAN:
				xyBankWFTAliaScanPay = XYSZBankPay.qqPay(merchantOrderNum,orderNum, yuanToCents, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("兴业深圳QQ扫码" + xyBankWFTAliaScanPay.toString());
				if("10000".equals(xyBankWFTAliaScanPay.get("code"))){
					resultMap.put("webStr", xyBankWFTAliaScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							xyBankWFTAliaScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SNBANK_ALI_SCAN:
				Map<String, String> snWFTAliaScanPay = SNBankPay.snWFTAliaScanPay(orderNum, yuanToCents, merchantOrderNum, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("遂宁阿里扫码" + snWFTAliaScanPay.toString());
				if("10000".equals(snWFTAliaScanPay.get("code"))){
					resultMap.put("webStr", snWFTAliaScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							snWFTAliaScanPay.get("msg"), 
							orderMap.get("orderNum"),
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SNBANK_WEIXIN_SCAN:
				Map<String, String> snWFTWXScanPay = SNBankPay.snWFTWXScanPay(orderNum, yuanToCents, merchantOrderNum, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("遂宁微信扫码" + snWFTWXScanPay.toString());
				if("10000".equals(snWFTWXScanPay.get("code"))){
					resultMap.put("webStr", snWFTWXScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							snWFTWXScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SNBANK_QQ_SCAN:
				snWFTAliaScanPay = SNBankPay.snWFTQQScanPay(orderNum, yuanToCents, merchantOrderNum, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				System.out.println("遂宁QQ扫码" + snWFTAliaScanPay.toString());
				if("10000".equals(snWFTAliaScanPay.get("code"))){
					resultMap.put("webStr", snWFTAliaScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							snWFTAliaScanPay.get("msg"), 
							orderMap.get("orderNum"),
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SNBANK_WEIXIN_WAP:
				Map<String, String> snWFTWXWapPay = SNBankPay.snWFTWXWapPay(merchantOrderNum, orderNum, yuanToCents, ip, appType, orderMap.get("rateKey1"), orderMap.get("rateKey2"), dictNameLike);
				System.out.println("兴业深圳微信WAP" + snWFTWXWapPay.toString());
				if (snWFTWXWapPay.containsKey("token_id")||snWFTWXWapPay.containsKey("pay_info")) {
					resultMap.put("token_id", snWFTWXWapPay.get("token_id"));
					resultMap.put("services", snWFTWXWapPay.get("services"));
					resultMap.put("webStr", snWFTWXWapPay.get("pay_info"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							snWFTWXWapPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SNBANK_WEIXIN_GZH:
			case PAY_TYPE_XYSZ_WEIXIN_GZH:
			case PAY_TYPE_PABANk_WEIXIN_GZH:
				is_GZH = 1;
				break;
			case PAY_TYPE_GUOFU_PASSIVE_QQ_SCAN:
			case PAY_TYPE_GUOFU_PASSIVE_ALI_SCAN:
			case PAY_TYPE_GUOFU_PASSIVE_WEIXIN_SCAN:
				
				if(dictName.equals(PAY_TYPE_GUOFU_PASSIVE_QQ_SCAN)){
					payTypes="8";
				}else if (dictName.equals(PAY_TYPE_GUOFU_PASSIVE_ALI_SCAN)) {
					payTypes="1";
				}else if (dictName.equals(PAY_TYPE_GUOFU_PASSIVE_WEIXIN_SCAN)) {
					payTypes="2";
				}
				Map<String, String> passivePay = GuoFuPay.passivePay(orderMap.get("rateKey1"), orderMap.get("payMoney"), orderNum, payTypes, "1", orderMap.get("rateKey2"),merchantOrderNum);
				System.out.println("国付QQ被扫：" + passivePay.toString());
				if("10000".equals(passivePay.get("code"))){
					resultMap.put("webStr", passivePay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							passivePay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", passivePay.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_MSXM_QQ_SCAN:
			case PAY_TYPE_MSXM_ALI_SCAN:
			case PAY_TYPE_MSXM_WEIXIN_SCAN:
				if(dictName.equals(PAY_TYPE_MSXM_QQ_SCAN)){
					payTypes="3";
				}else if (dictName.equals(PAY_TYPE_MSXM_ALI_SCAN)) {
					payTypes="2";
				}else if (dictName.equals(PAY_TYPE_MSXM_WEIXIN_SCAN)) {
					payTypes="1";
				}
				Map<String, String> xmScanPay = MinShengXMPay.xmScanPay(orderMap.get("rateKey1"), orderNum, orderMap.get("payMoney"), payTypes, orderMap.get("rateKey2"));
				System.out.println("民生厦门扫码：" + xmScanPay.toString());
				if("10000".equals(xmScanPay.get("code"))){
					resultMap.put("webStr", xmScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							xmScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", xmScanPay.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_JUNHU_QQ_SCAN:
			case PAY_TYPE_JUNHU_ALI_SCAN:
			case PAY_TYPE_JUNHU_WEIXIN_SCAN:
				if(dictName.equals(PAY_TYPE_JUNHU_QQ_SCAN)){
					payTypes="3";
				}else if (dictName.equals(PAY_TYPE_JUNHU_ALI_SCAN)) {
					payTypes="2";
				}else if (dictName.equals(PAY_TYPE_JUNHU_WEIXIN_SCAN)) {
					payTypes="1";
				}
				Map<String, String> jhScanPay = JunHuPay.jhScanPay(orderMap.get("rateKey1"), orderNum, orderMap.get("payMoney"), payTypes, orderMap.get("rateKey2"));
				System.out.println("骏狐扫码：" + jhScanPay.toString());
				if("10000".equals(jhScanPay.get("code"))){
					resultMap.put("webStr", jhScanPay.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							jhScanPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", jhScanPay.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_PFBANK_ALI_SCAN:
			case PAY_TYPE_PFBANK_WEIXIN_SCAN:
			case PAY_TYPE_PFBANK_WEIXIN_WEB:
				if(dictName.equals(PAY_TYPE_PFBANK_ALI_SCAN)){
					payTypes="3";
				}else if (dictName.equals(PAY_TYPE_PFBANK_WEIXIN_SCAN)) {
					payTypes="1";
				}else if (dictName.equals(PAY_TYPE_PFBANK_WEIXIN_WEB)) {
					payTypes="2";
				}
				Map<String, String> pfszPay = PFSZBankPay.pfszPay(orderNum, orderMap.get("rateKey1"), orderMap.get("rateKey2"), orderMap.get("rateKey3"), ip, yuanToCents, payTypes, 
						orderMap.get("rateKey5"), orderMap.get("rateKey6"),merchantOrderNum);
				System.out.println("浦发深圳支付：" + pfszPay.toString());
				if("10000".equals(pfszPay.get("code"))){
					String qr_code = pfszPay.get("qr_code");
					if("2".equals(appType)){
						//拼接回调地址（只拼接ios的）
						if (dictName.equals(PAY_TYPE_PFBANK_WEIXIN_WEB)) {
							String redirect_url = URLEncoder.encode(PropertiesUtil.getProperty("rate", "return_app_url")
									+"?orderNum="+orderNum);
							qr_code = qr_code+"&redirect_url="+redirect_url;
						}
					}
					resultMap.put("webStr", qr_code);
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							pfszPay.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", pfszPay.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SWT_WEIXIN_SCAN:
			case PAY_TYPE_SWT_ALI_SCAN:
				if(dictName.equals(PAY_TYPE_SWT_WEIXIN_SCAN)){
					payTypes="1";
				}else if (dictName.equals(PAY_TYPE_SWT_ALI_SCAN)) {
					payTypes="2";
				}
				Map<String, String> swtPayScan = SWTPay.swtPayScan(orderMap.get("rateKey1"), orderMap.get("rateKey3"), orderNum, yuanToCents, payTypes, orderMap.get("rateKey2"),merchantOrderNum);
				System.out.println("商务通支付：" + swtPayScan.toString());
				if("10000".equals(swtPayScan.get("code"))){
					resultMap.put("webStr", swtPayScan.get("qr_code"));
				}else {
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							swtPayScan.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", swtPayScan.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_NOW_ALI_WAP:
			case PAY_TYPE_NOW_WEIXIN_WAP:
				String payPlatform = "2";
				if(dictName.equals(PAY_TYPE_NOW_ALI_WAP)){
					payPlatform="1";
				}
				Map<String, String> nowResult = NowPayUtil.wayPay(orderNum, DecimalUtil.yuanToCents(payMoney+""), payPlatform
						,ip,orderMap.get("rateKey1"), orderMap.get("rateKey2"),merchantOrderNum);
				if("10000".equals(nowResult.get("code"))){
					resultMap.put("webStr", nowResult.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							nowResult.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", nowResult.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_NOW_ALI_SCAN:
			case PAY_TYPE_NOW_WEIXIN_SCAN:
				payPlatform = "2";
				if(dictName.equals(PAY_TYPE_NOW_ALI_SCAN)){
					payPlatform="1";
				}
				nowResult = NowPayUtil.scanPay(orderNum, DecimalUtil.yuanToCents(payMoney+"")
						,ip,orderMap.get("rateKey1"), orderMap.get("rateKey2"),merchantOrderNum);
				if("10000".equals(nowResult.get("code"))){
					resultMap.put("webStr", nowResult.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							nowResult.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", nowResult.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_SD_WEIXIN_SCAN:
			case PAY_TYPE_SD_ALI_SCAN:
				String payType = "2";
				if(dictName.equals(PAY_TYPE_SD_ALI_SCAN)){
					payType="1";
				}
				Map<String,String> shandeMap = ShandePay.shandePay(orderMap.get("rateKey1"), orderMap.get("rateKey3"), orderNum
						, payMoney+"", payType, orderMap.get("rateKey5"), orderMap.get("rateKey2"),merchantOrderNum);
				if("10000".equals(shandeMap.get("code"))){
					resultMap.put("webStr", shandeMap.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							shandeMap.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", shandeMap.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_ZZ_ALI_WAP:
				String webStr = AliZzPay.pay(orderNum, payMoney+"", ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"),merchantOrderNum);
				resultMap.put("webStr", webStr);
				break;
			case PAY_TYPE_WUKA_QQ_SCAN:
			case PAY_TYPE_WUKA_WEIXIN_SCAN:
				String plat = "3";
				if(dictName.equals(PAY_TYPE_WUKA_WEIXIN_SCAN)){
					plat = "2";
				}
				Map<String,String> wukaMap = WukaPay.pay(orderNum, payMoney+"", plat, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				if("10000".equals(wukaMap.get("code"))){
					resultMap.put("webStr", wukaMap.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							wukaMap.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", wukaMap.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_WUKA_WEIXIN_WAP:
			case PAY_TYPE_WUKA_ALI_WAP:
				plat = "1";
				if(dictName.equals(PAY_TYPE_WUKA_WEIXIN_WAP)){
					plat = "2";
				}
				wukaMap = WukaPay.h5Pay(orderNum, payMoney+"", plat, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				if("10000".equals(wukaMap.get("code"))){
					resultMap.put("webStr", wukaMap.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							wukaMap.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", wukaMap.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_ZSXM_WEIXIN_SCAN:
				dictNameLike="SCAN";
				Map<String,String> zsxmMap = ZsxmPay.pay(orderNum, payMoney+"", merchantOrderNum
						, ip, dictNameLike, "", "", orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				if("10000".equals(zsxmMap.get("code"))){
					resultMap.put("webStr", zsxmMap.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							zsxmMap.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", zsxmMap.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			case PAY_TYPE_ZSXM_ALI_SCAN:
				zsxmMap = ZsxmPay.aliPay(orderNum, payMoney+"", merchantOrderNum
						, ip, orderMap.get("rateKey1"), orderMap.get("rateKey2"));
				if("10000".equals(zsxmMap.get("code"))){
					resultMap.put("webStr", zsxmMap.get("webStr"));
				}else{
					//发送错误信息邮件
					MailRun.send(
							orderMap.get("dictName"), 
							zsxmMap.get("msg"), 
							orderMap.get("orderNum"), 
							orderMap.get("rateId"),
							orderMap.get("appName"),
							orderMap.get("payMoney"), 
							orderMap.get("companyName"), 
							orderMap.get("rateCompanyName"), 
							orderMap.get("payWayName"));
					resultMap.put("msg", zsxmMap.get("msg"));
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				break;
			default:
				break;
		}
		if (dictName.equals(PayRateDictValue.PAY_TYPE_PABANk_GZH_WEIXIN_SCAN)) {
			
			RedisDBUtil.redisDao.setString("gzh_scan"+orderNum, resultMap.get("webStr"), 3600);
			String property = PropertiesUtil.getProperty("rate", "gzh_scan_url");
			property += "?orderNum=" + orderNum;
			
			
			String url = ReadProChange.getValue("luoma_url")+ URLEncoder.encode(property);
			System.out.println("罗马转之前链接："+url);
			String result = HttpUtil.HttpGet(url, null);
			
			System.out.println("罗马转之后链接："+result);
			
			JSONObject jsonObject = JSONObject.parseObject(result);
			String lmResult = jsonObject.getString("ticket_url");
			resultMap.put("webStr", lmResult);
		}
		//公众号支付时调用加密
		if(is_GZH == 1) {
			orderMap.put("address", ip);
			String baseUrl = PropertiesUtil.getProperty("rate", "wechat_gzh_pay_huidiao_url");
			String openIdUrl = "https://open.weixin.qq.com/connect/oauth2/authorize";
			String backUri = baseUrl;
			// 微信appid
			String appid = orderMap.get("gzhAppId");
			// 授权后要跳转的链接
			backUri = backUri + "?orderNum=" + orderNum;
			// URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
			backUri = URLEncoder.encode(backUri);
			// scope 参数视各自需求而定，这里用scope=snsapi_base
			// 不弹出授权页面直接授权目的只获取统一支付接口的openid snsapi_userinfo是弹出窗口
			String url = openIdUrl + "?appid=" + appid + "&redirect_uri=" + backUri
					+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
			if(orderMap.containsKey("appName")){
				
				String lmurl = ReadProChange.getValue("luoma_url")+ URLEncoder.encode(url);
				System.out.println("罗马转之前链接："+lmurl);
				String result = HttpUtil.HttpGet(lmurl, null);
				
				System.out.println("罗马转之后链接："+result);
				
				if(result.equals("")){
					resultMap.put("status", PayFinalUtil.RATE_FAIL);
					return resultMap;
				}
				RedisDBUtil.redisDao.hmset(orderNum, orderMap);
				JSONObject jsonObject = JSONObject.parseObject(result);
				String lmResult = jsonObject.getString("ticket_url");
				resultMap.put("webStr", lmResult);
			}else{
				//存入redis
				RedisDBUtil.redisDao.hmset(orderNum, orderMap);
				resultMap.put("webStr", url);
			}
		}else{
			System.out.println("=====>请求上游耗时:" + (System.currentTimeMillis() - time));
		}
		resultMap.put("orderNum", orderNum);
		resultMap.put("status", PayFinalUtil.PAY_STATUS_SUSSESS);
		return resultMap;
	}
	
	/**
	 * 调用app支付通道
	 * @param orderMap
	 * @param appId
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static Map<String,String> appRatePay(String dictName,Map<String,String> orderMap
			,String ip,String appType,String dictNameLike) throws Exception{
		Map<String, String> resultMap = new HashMap<>();
		String orderNum = orderMap.get("orderNum").toString();
		String merchantOrderNum = orderMap.get("merchantOrderNum").toString();
		double payMoney = Double.valueOf(orderMap.get("payMoney"));
		String yuanToCents = DecimalUtil.yuanToCents(orderMap.get("payMoney").toString());
		//String orderName = orderMap.get("orderName").toString();
		String hfbStr = "";
		Integer type = null;
		long time = System.currentTimeMillis();
		if(dictName.equals(PAY_TYPE_ALIPAY_APP)){
			BigDecimal b = new BigDecimal(payMoney);
			double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			// 调用支付宝支付
			String aliPay = AppAliPay.sign(orderNum, "" + f1
					, merchantOrderNum,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
			resultMap.put("aliPay", aliPay);
			type = 1;
		}else if(dictName.equals(PAY_TYPE_WFT_WEIXIN)){
			Map<String, String> wxMap = WftWeChatPay.pay(merchantOrderNum,
					orderNum, "" + DecimalUtil.yuanToCents(payMoney+""), ip,appType
					,orderMap.get("rateKey1"),orderMap.get("rateKey2"),dictName,dictNameLike);
			if (wxMap.containsKey("token_id")||wxMap.containsKey("pay_info")) {
				resultMap.put("token_id", wxMap.get("token_id"));
				resultMap.put("services", wxMap.get("services"));
				resultMap.put("webStr", wxMap.get("pay_info"));
				type = 2;
			} else {
				//发送邮件
				MailRun.send(dictName,"威富通wap下单请求错误",orderNum,orderMap.get("rateId"),orderMap.get("appName")
						,""+payMoney,resultMap.get("companyName"),orderMap.get("rateCompanyName")
						,orderMap.get("payWayName"));
				resultMap.put("status", PayFinalUtil.RATE_FAIL);
				return resultMap;
			}
		}else if(dictName.equals(PAY_TYPE_WFT_WEIXIN_APP)){
			Map<String, String> wxMap = WftWeChatPay.appPay(merchantOrderNum,
					orderNum, "" + DecimalUtil.yuanToCents(payMoney+""), ip
					,orderMap.get("rateKey1"),orderMap.get("rateKey2"),orderMap.get("rateKey3"));
			if (wxMap.containsKey("token_id")||wxMap.containsKey("pay_info")) {
				resultMap.put("token_id", wxMap.get("token_id"));
				resultMap.put("services", wxMap.get("services"));
				resultMap.put("app_id", wxMap.get("app_id"));
				type = 2;
			} else {
				//发送邮件
				MailRun.send(dictName,"威富通wap下单请求错误",orderNum,orderMap.get("rateId"),orderMap.get("appName")
						,""+payMoney,resultMap.get("companyName"),orderMap.get("rateCompanyName")
						,orderMap.get("payWayName"));
				resultMap.put("status", PayFinalUtil.RATE_FAIL);
				return resultMap;
			}
		}else if (dictName.equals(PAY_TYPE_GF_WEIXIN_APP)) {
			//WeChatPay.pay(orderMap.get("rateKey1"), orderMap.get("rateKey3"), body, orderNum, yuanToCents, ip, null, notify_url, "APP", orderMap.get("rateKey2"));
			
		}else if(dictName.equals(PAY_TYPE_HFB_WX_SDK)){
			hfbStr = HfbPay.sdkPay(orderNum, payMoney, ip, merchantOrderNum, new Date(),2
					,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
			if("".equals(hfbStr)){
				//发送邮件
				MailRun.send(dictName,"汇付宝微信sdk下单请求错误",orderNum,orderMap.get("rateId"),orderMap.get("appName")
						,""+payMoney,orderMap.get("companyName"),orderMap.get("rateCompanyName")
						,orderMap.get("payWayName"));
				resultMap.put("status", PayFinalUtil.RATE_FAIL);
				return resultMap;
			}
			resultMap.put("hfbStr", hfbStr);
			type = 3;
		}else if(dictName.equals(PAY_TYPE_HFB_ALI_SDK)){
			hfbStr = HfbPay.sdkPay(orderNum, payMoney, ip, merchantOrderNum, new Date(),1
					,orderMap.get("rateKey1"),orderMap.get("rateKey2"));
			if("".equals(hfbStr)){
				//发送邮件
				MailRun.send(dictName,"汇付宝支付宝sdk下单请求错误",orderNum,orderMap.get("rateId"),orderMap.get("appName")
						,""+payMoney,orderMap.get("companyName"),orderMap.get("rateCompanyName")
						,orderMap.get("payWayName"));
				
				resultMap.put("status", PayFinalUtil.RATE_FAIL);
				return resultMap;
			}
			resultMap.put("hfbStr", hfbStr);
			type = 4;
		}else if(dictName.equals(PAY_TYPE_HFB_ALI_SDK)){
			Map<String, String> xySZWFTWXWapPay = XYSZBankPay.xySZWFTWXWapPay(merchantOrderNum, orderNum, yuanToCents, ip, appType, orderMap.get("rateKey1"), orderMap.get("rateKey2"), dictNameLike);
			System.out.println("兴业深圳微信WAP" + xySZWFTWXWapPay.toString());
			if (xySZWFTWXWapPay.containsKey("token_id")||xySZWFTWXWapPay.containsKey("pay_info")) {
				resultMap.put("token_id", xySZWFTWXWapPay.get("token_id"));
				resultMap.put("services", xySZWFTWXWapPay.get("services"));
				resultMap.put("webStr", xySZWFTWXWapPay.get("pay_info"));
				type = 5;
			}else {
				MailRun.send(orderMap.get("dictName"), xySZWFTWXWapPay.get("msg"), orderMap.get("orderNum"), orderMap.get("rateId"),orderMap.get("appName"),
						orderMap.get("payMoney"), orderMap.get("companyName"), orderMap.get("rateCompanyName"), orderMap.get("payWayName"));
				resultMap.put("status", PayFinalUtil.RATE_FAIL);
				return resultMap;
			}
		}
		System.out.println("=====>请求上游耗时:" + (System.currentTimeMillis() - time));
		if(type!=null){
			resultMap.put("type", type.toString());
		}
		resultMap.put("orderNum", orderNum);
		resultMap.put("status", PayFinalUtil.PAY_STATUS_SUSSESS);
		return resultMap;
	}
	
	public static void main(String[] args) {
		System.out.println(URLEncoder.encode("https://testpayapi.aijinfu.cn/payDetail/test.do"));
	}
}
