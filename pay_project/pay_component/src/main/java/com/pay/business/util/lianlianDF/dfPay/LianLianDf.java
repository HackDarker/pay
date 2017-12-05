package com.pay.business.util.lianlianDF.dfPay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lianlianpay.security.utils.LianLianPaySecurity;
import com.lianpay.api.util.TraderRSAUtil;
import com.pay.business.util.lianlianDF.bean.PaymentRequestBean;
import com.pay.business.util.lianlianDF.bean.PaymentResponseBean;
import com.pay.business.util.lianlianDF.bean.QueryPaymentRequestBean;
import com.pay.business.util.lianlianDF.bean.QueryPaymentResponseBean;
import com.pay.business.util.lianlianDF.constant.PaymentConstant;
import com.pay.business.util.lianlianDF.constant.PaymentStatusEnum;
import com.pay.business.util.lianlianDF.constant.RetCodeEnum;
import com.pay.business.util.lianlianDF.util.HttpUtil;
import com.pay.business.util.lianlianDF.util.SignUtil;

/**
 * 
 * @Title: LianLianDf.java
 * @Package com.pay.business.util.lianlianDF.dfPay
 * @Description: 连连支付-代付业务 注意：实时付款对接流程：开通商户号，登录商户站上传商户生成的公钥，
 *               商户服务器IP需要向连连报备（群里咨询，会发报备申请表），
 *               代码对接好了后，如在连连商户账户没钱，需先在连连商户站充值（可采用线下加款方式）， 用正式商户号和真实数据测试
 * @author ZHOULIBO
 * @date 2017年10月17日 上午11:06:33
 * @version V1.0
 */
public class LianLianDf {
	private static final String LL0000="0000";
	private static final String LL4002="4002";
	private static final String LL4003="4003";
	private static final String LL4004="4004";
	private static final String LL8901="8901";
	/**
	 * lianLianPay 
	 * 连连代付-付款申请
	 * @param paymentRequestBean
	 * @param OID_PARTNER				商户号
	 * @param PUBLIC_KEY_ONLINE			连连代付公钥
	 * @param BUSINESS_PRIVATE_KEY		商户私钥
	 * @return    设定文件 
	 * Map<String,Object>    返回类型
	 */
	public static Map<String, Object> lianLianPay(PaymentRequestBean paymentRequestBean,String oidPartner,String publicKeyOnline,String businessPrivateKey) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			// 用商户自己的私钥加签
			paymentRequestBean.setSign(SignUtil.genRSASign(JSON.parseObject(JSON.toJSONString(paymentRequestBean)),businessPrivateKey));
			String jsonStr = JSON.toJSONString(paymentRequestBean);
			System.out.println("连连代付：实时支付请求报文为：" + jsonStr);
			// 用银通公钥对请求参数json字符串加密
			// 报Illegal key
			// size异常时，可参考这个网页解决问题http://www.wxdl.cn/java/security-invalidkey-exception.html
			String encryptStr = LianLianPaySecurity.encrypt(jsonStr,publicKeyOnline);
			if (StringUtils.isEmpty(encryptStr)) {
				// 加密异常
				System.out.println("连连代付：公钥加密异常为：" + encryptStr);
				returnMap.put("code", "10001");
				returnMap.put("msg", encryptStr);
				return returnMap;
			}
			JSONObject json = new JSONObject();
			json.put("oid_partner", paymentRequestBean.getOid_partner());
			json.put("pay_load", encryptStr);
			System.out.println("连连代付：请求报文JSON格式为："+json);
			String response = HttpUtil.doPost(PaymentConstant.PAY_URL, json, "UTF-8");
			System.out.println("连连代付：付款接口返回响应报文：" + response);
			if (StringUtils.isEmpty(response)) {
				// 出现异常时调用订单查询，明确订单状态，不能私自设置订单为失败状态，以免造成这笔订单在连连付款成功了，而商户设置为失败
				returnMap=queryPaymentAndDealBusiness(paymentRequestBean.getNo_order(),oidPartner,publicKeyOnline,businessPrivateKey);
			} else {
				PaymentResponseBean paymentResponseBean = JSONObject.parseObject(response, PaymentResponseBean.class);
				// 对返回0000时验证签名
				String retCode=paymentResponseBean.getRet_code();
				System.out.println("连连代付：付款接口返回响应code为："+retCode);
				if (LL0000.equals(retCode)) {
					// 先对结果验签
					boolean signCheck = TraderRSAUtil.checksign(publicKeyOnline, SignUtil.genSignData(JSONObject.parseObject(response)),
							paymentResponseBean.getSign());
					if (!signCheck) {
						// 传送数据被篡改，可抛出异常，再人为介入检查原因
						// logger.error("返回结果验签异常,可能数据被篡改");
						System.out.println("连连代付：返回结果验签异常,可能数据被篡改");
						returnMap.put("code", "10001");
						returnMap.put("msg", "连连代付：返回结果验签异常,可能数据被篡改");
						return returnMap;
					}
					System.out.println("连连支付：交易成功！订单处于付款中，订单号为：" + paymentRequestBean.getNo_order());
					// 注意：已生成连连支付单，付款处理中（交易成功，不是指付款成功，是指跟连连流程正常），商户可以在这里处理自已的业务逻辑（或者不处理，在异步回调里处理逻辑）,最终的付款状态由异步通知回调告知
					returnMap.put("code", "10000");
					returnMap.put("msg", "操作成功");
					//返回需要的参数:
					//连连支付 流水号
					returnMap.put("bank_transaction", paymentResponseBean.getOid_paybill());
					returnMap.put("statusState", 2+ "," + paymentResponseBean.getRet_msg());
					//没有返回交易时间；默认为我们的商户付款时间
					returnMap.put("rsptime",paymentRequestBean.getDt_order());
					return returnMap;
				} else if (LL4002.equals(retCode) || LL4003.equals(retCode)|| LL4004.equals(retCode)) {
					// 当调用付款申请接口返回4002，4003，4004,会同时返回验证码，用于确认付款接口
					// 对于疑似重复订单，需先人工审核这笔订单是否正常的付款请求，
					//而不是系统产生的重复订单，确认后再调用确认付款接口或者在连连商户站后台操作疑似订单，api不调用确认付款接口
					// 对于疑似重复订单，也可不做处理，
					// 返回其他code时，可将订单置为失败
					returnMap.put("code", "10001");
					returnMap.put("msg","连连代付：订单失败；原因："+response);
				} else if (RetCodeEnum.isNeedQuery(retCode)) {
					// 出现1002，2005，4006，4007，4009，9999这6个返回码时（或者对除了0000之后的code都查询一遍查询接口）
					// 调用付款结果查询接口，明确订单状态，不能私自设置订单为失败状态，以免造成这笔订单在连连付款成功了，而商户设置为失败
					// 第一次测试对接时，返回{"ret_code":"4007","ret_msg":"敏感信息解密异常"},可能原因报文加密用的公钥改动了,
					// demo中的公钥是连连公钥，商户生成的公钥用于上传连连商户站用于连连验签，生成的私钥用于加签
					returnMap=queryPaymentAndDealBusiness(paymentRequestBean.getNo_order(),oidPartner,publicKeyOnline,businessPrivateKey);
				} else {
					// 返回其他code时，可将订单置为失败
					returnMap.put("code", "10001");
					returnMap.put("msg","连连代付：订单失败；原因："+response);
					System.out.println("连连代付：订单失败；原因："+response);
				}
			}
		} catch (Exception e) {
			System.out.println("连连代付:支付失败原因为：" + e);
			returnMap.put("code", "10001");
			returnMap.put("msg", e);
			return returnMap;
		}
		return returnMap;
	}
	/**
	 * 
	 * queryPaymentAndDealBusiness 
	 * 异常时，先查询订单状态，再根据订单状态处理业务逻辑
	 * @param orderNo
	 * @param OID_PARTNER
	 * @param PUBLIC_KEY_ONLINE
	 * @param BUSINESS_PRIVATE_KEY
	 * @return    设定文件 
	 * Map<String,Object>    返回类型
	 */
	public static Map<String, Object> queryPaymentAndDealBusiness(String orderNo,String oidPartner,String publicKeyOnline,String businessPrivateKey) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		QueryPaymentRequestBean queryRequestBean = new QueryPaymentRequestBean();
		queryRequestBean.setNo_order(orderNo);
		queryRequestBean.setOid_partner(oidPartner);
		// queryRequestBean.setPlatform("test.com");
		queryRequestBean.setApi_version(PaymentConstant.API_VERSION);
		queryRequestBean.setSign_type(PaymentConstant.SIGN_TYPE);
		queryRequestBean.setSign(SignUtil.genRSASign(JSON.parseObject(JSON.toJSONString(queryRequestBean)),businessPrivateKey));
		String queryResult = HttpUtil.doPost(PaymentConstant.QUERY_URL, JSON.parseObject(JSON.toJSONString(queryRequestBean)), "UTF-8");
		System.out.println("连连代付：实时付款查询接口响应报文：" + queryResult);
		if (StringUtils.isEmpty(queryResult)) {
			// 可抛异常，查看原因
			// logger.error("实时付款查询接口响应异常");
			returnMap.put("code", "10001");
			returnMap.put("msg", "连连代付：实时付款查询接口响应异常:" + queryResult);
			System.out.println("连连代付：实时付款查询接口响应异常:" + queryResult);
			return returnMap;
		}
		QueryPaymentResponseBean queryPaymentResponseBean = JSONObject.parseObject(queryResult, QueryPaymentResponseBean.class);
		// 先对结果验签
		boolean signCheck = TraderRSAUtil.checksign(publicKeyOnline, SignUtil.genSignData(JSONObject.parseObject(queryResult)),
				queryPaymentResponseBean.getSign());
		if (!signCheck) {
			// 传送数据被篡改，可抛出异常，再人为介入检查原因
//			logger.error("返回结果验签异常,可能数据被篡改");
			returnMap.put("code", "10001");
			returnMap.put("msg", "连连代付：返回结果验签异常,可能数据被篡改;请人工干预");
			System.out.println("连连代付：返回结果验签异常,可能数据被篡改;请人工干预");
			return returnMap;
		}
		String retCode=queryPaymentResponseBean.getRet_code();
		if (LL0000.equals(retCode)) {
			PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.getPaymentStatusEnumByValue(queryPaymentResponseBean.getResult_pay());
			// TODO商户根据订单状态处理自已的业务逻辑
			switch (paymentStatusEnum) {
			case PAYMENT_APPLY:
				// 付款申请，这种情况一般不会发生，如出现，请直接找连连技术处理
				returnMap.put("code", "10001");
				returnMap.put("msg", "连连代付：订单状态为：付款申请，这种情况一般不会发生，如出现，请直接找连连技术处理");
				break;
			case PAYMENT_CHECK:
				// 复核状态 
				// 返回4002，4003，4004时，订单会处于复核状态，这时还未创建连连支付单，没提交到银行处理，如需对该订单继续处理，需商户先人工审核这笔订单是否是正常的付款请求，没问题后再调用确认付款接口
				// 如果对于复核状态的订单不做处理，可当做失败订单
				returnMap.put("code", "10004");
				returnMap.put("msg", "连连代付：订单状态为：复核状态，如果对于复核状态的订单不做处理，可当做失败订单");
				break;
			case PAYMENT_SUCCESS:
				// 成功
				returnMap.put("bank_transaction", queryPaymentResponseBean.getOid_paybill());
				returnMap.put("statusState",1+ "," + queryPaymentResponseBean.getRet_msg());
				//没有返回交易时间；默认为我们的商户付款时间
				returnMap.put("rsptime",queryPaymentResponseBean.getSettle_date());
				returnMap.put("code", "10002");
				returnMap.put("msg", "连连代付：订单状态为：付款成功");
				break;
			case PAYMENT_FAILURE:
				// 失败
				returnMap.put("code", "10003");
				returnMap.put("msg", "连连代付：订单状态为：付款失败");
				break;
			case PAYMENT_DEALING:
				// 处理中
				returnMap.put("code", "10000");
				returnMap.put("msg", "连连代付：订单状态为：付款处理中");
				break;
			case PAYMENT_RETURN:
				// 退款
				// 可当做失败（退款情况
				// 极小概率下会发生，个别银行处理机制是先扣款后打款给用户时再检验卡号信息是否正常，异常时会退款到连连商户账上）
				returnMap.put("code", "10005");
				returnMap.put("msg", "连连代付：订单状态为：退款，可当做失败（退款情况 极小概率下会发生，个别银行处理机制是先扣款后打款给用户时再检验卡号信息是否正常，异常时会退款到连连商户账上）");
				break;
			case PAYMENT_CLOSED:
				// 关闭  可当做失败 ，对于复核状态的订单不做处理会将订单关闭
				returnMap.put("code", "10006");
				returnMap.put("msg", "连连代付：订单状态为：关闭状态，可当做失败 ，对于复核状态的订单不做处理会将订单关闭");
				break;
			default:
				break;
			}
		} else if (LL8901.equals(retCode)) {
			// 订单不存在，这种情况可以用原单号付款，最好不要换单号，如换单号，在连连商户站确认下改订单是否存在，避免系统并发时返回8901，实际有一笔订单
			returnMap.put("code", "10007");
			returnMap.put("msg", "连连代付：订单不存在;这种情况可以用原单号付款，最好不要换单号，如换单号，在连连商户站确认下改订单是否存在，避免系统并发时返回8901，实际有一笔订单");
		} else {
			// 查询异常（极端情况下才发生,对于这种情况，可人工介入查询，在连连商户站查询或者联系连连客服，查询订单状态）
			System.out.println("连连代付：查询异常：（极端情况下才发生,对于这种情况，可人工介入查询，在连连商户站查询或者联系连连客服，查询订单状态）");
			returnMap.put("code", "10001");
			returnMap.put("msg", "连连代付：订查询异常;对于这种情况，可人工介入查询，在连连商户站查询或者联系连连客服，查询订单状态");
		}
		System.out.println("连连代付：查询msg为:"+returnMap.get("msg"));
		return returnMap;
	}
}
