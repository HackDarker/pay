package com.pay.manger.controller.payv2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.sandpay.cashier.sdk.util.CryptoUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.entity.Payv2PayOrderRefund;
import com.pay.business.order.service.Payv2PayOrderRefundService;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.StringHandleUtil;
import com.pay.business.util.alipay.AppAlipayNotify;
import com.pay.business.util.alipayZz.AliZzPay;
import com.pay.business.util.alipayZz.ResponseBean;
import com.pay.business.util.guofu.GuoFuPay;
import com.pay.business.util.minshengbank.xm.EpaySignUtil;
import com.pay.business.util.nowpay.NowPayUtil;
import com.pay.business.util.pfSZ.PFSZBankPay;
import com.pay.business.util.shande.ShandeSign;
import com.pay.business.util.swtPay.SWTPay;
import com.pay.business.util.wftpay.SignUtils;
import com.pay.business.util.wftpay.XmlUtils;
import com.pay.business.util.wukaPay.WukaSign;
import com.pay.business.util.zsxmPay.ZsxmSignUtils;
import com.pay.manger.util.ReturnMsgTips;

/**
* @ClassName: PayWayController 
* @Description:支付集控制器
* @author qiuguojie
* @date 2016年12月9日 下午8:21:52
*/
@Controller
@RequestMapping("/payDetail/*")
public class PayWayController{
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;
	@Autowired
	private Payv2PayOrderService payv2PayOrderService;
	@Autowired
	private Payv2PayOrderRefundService refundService;
	
	
	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date parse = sdf.parse("20170824114449");
		System.out.println(parse);
		//String str = "{{"interfaceType":"2","memberCode":"1010001459","orderNum":"1503545102968","payMoney":"0.01","payNum":"20170824112550431680","payTime":"20170824112603","payType":"3","platformType":"3","respType":"2","resultCode":"0000","resultMsg":"交易成功","signStr":"IlshjojzwIrm5FcdArdE22rDEclo4w3liFsy3p5de  tueOfm0RgC5B0C8ETi5vClftVjGxxi4DsSQVUgH8 Yg=="}}"
		/*//支付宝退款
		AlipayClient alipayClient = new DefaultAlipayClient(PayConfigApi.MOBILEPAY_SERVICE,
				AppAlipayConfig.app_id,AppAlipayConfig.private_key,"json",AppAlipayConfig.input_charset,
				AppAlipayConfig.ali_public_key);
		AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
		request.setBizContent("{" +
		"    \"out_trade_no\":\"DD2017060919433124869316\"," +
		"    \"refund_amount\":5," +
		"    \"refund_reason\":\"正常退款\"," +
		"    \"out_request_no\":\"HZ01RF001\"" +
		"  }");
		AlipayTradeRefundResponse response = alipayClient.execute(request);
		if(response.isSuccess()){
			System.out.println(response.getGmtRefundPay());
			System.out.println("调用成功");
		} else {
			System.out.println("调用失败");
		}*/
	}
	
	/**
	* @throws Exception 
	* @Description:支付信息
	* @param request
	* @param response
	* @param map
	* @return    设定文件 
	* @return Map<String,Object>    返回类型 
	* @date 2016年9月2日 下午4:37:51 
	* @throws
	*/
	@ResponseBody
	@RequestMapping(value = "/payOrderDetail")
	public Map<String,Object> payOrderDetail(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String, Object> map) throws Exception{
		response.setHeader("Access-Control-Allow-Origin", "*");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean appKeyCon = ObjectUtil.checkObject(new String[] { "appKey" }, map);
		if(appKeyCon){
			
			//经纬度和用户手机标识不进行签名  --------------------
			String userDeviceToken = null;
			if(map.containsKey("userDeviceToken")){
				userDeviceToken = map.get("userDeviceToken").toString();
				map.remove("userDeviceToken");
			}
			String lon = null;
			String lat = null;
			if(map.containsKey("lon")&&map.containsKey("lat")){
				lon = map.get("lon").toString();
				map.remove("lon");
				lat = map.get("lat").toString();
				map.remove("lat");
			}
			//---------------------------------------
			
			//检查appKey是否有效!先查询app审核是否通过，再查询app所属商户状态是否通过
			Payv2BussCompanyApp pbca = payv2BussCompanyAppService.checkApp(map.get("appKey").toString());
			if(pbca!=null){
				//验签
				boolean con = PaySignUtil.checkSign(map, pbca.getAppSecret());
				if(con){
					//验签后把经纬度和用户手机标识加上-------------------
					if(userDeviceToken!=null){
						map.put("userDeviceToken", userDeviceToken);
					}
					if(lon!=null&&lat!=null){
						map.put("lon", lon);
						map.put("lat", lat);
					}
					//-------------------------------------------
					
					boolean isNotNull = ObjectUtil.checkObject(new String[] { "bussOrderNum","appKey","payMoney","orderName","notifyUrl" }, map);
					try {
						if (isNotNull&&DecimalUtil.isBigDecimal(map.get("payMoney").toString())) {
							Map<String, Object> m = payv2PayOrderService.payOrderDetail(map,pbca);
							if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL_OK)){
								resultMap = ReMessage.resultBack(ParameterEunm.PAY_FAILED_SUCCESS, null);
							}else if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL)){
								resultMap = ReMessage.resultBack(ParameterEunm.ORDER_ERROR, null);
							}else{
								m.remove("status");
								resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
							}
						} else {
							logger.debug("参数不能为空,或者有误");
							resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.debug("服务器异常,请稍后再试");
						resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
					}
				}else{
					logger.debug("商户签名错误");
					resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
				}
			}else{
				logger.debug("appKey无效");
				resultMap = ReMessage.resultBack(ParameterEunm.APP_KEY_INVALID,null);
			}
		}else{
			logger.debug("appKey无效");
			resultMap = ReMessage.resultBack(ParameterEunm.APP_KEY_INVALID,null);
		}
		return resultMap;
	}
	
	/**
	* @throws Exception 
	* @Description:支付信息   h5支付
	* @param request
	* @param response
	* @param map
	* @return    设定文件 
	* @return Map<String,Object>    返回类型 
	* @date 2016年9月2日 下午4:37:51 
	* @throws
	*/
	@ResponseBody
	@RequestMapping(value = "/payOrderH5Detail")
	public Map<String,Object> payOrderH5Detail(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String, Object> map) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean appKeyCon = ObjectUtil.checkObject(new String[] { "appKey" }, map);
		if(appKeyCon){
			//检查appKey是否有效!先查询app审核是否通过，再查询app所属商户状态是否通过
			Payv2BussCompanyApp pbca = payv2BussCompanyAppService.checkApp(map.get("appKey").toString());
			if(pbca!=null){
				//验签
				boolean con = PaySignUtil.checkSign(map, pbca.getAppSecret());
				if(con){
					
					boolean isNotNull = ObjectUtil.checkObject(new String[] { "bussOrderNum","appKey","payMoney","orderName" }, map);
					try {
						if (isNotNull&&DecimalUtil.isBigDecimal(map.get("payMoney").toString())) {
							Map<String, Object> m = payv2PayOrderService.payOrderH5Detail(map,pbca);
							if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL_OK)){
								resultMap = ReMessage.resultBack(ParameterEunm.PAY_FAILED_SUCCESS, null);
							}else if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL)){
								resultMap = ReMessage.resultBack(ParameterEunm.ORDER_ERROR, null);
							}else{
								m.remove("status");
								resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
							}
						} else {
							logger.debug("参数不能为空,或者有误");
							resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
						}
					} catch (Exception e) {
						resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null, null);
					}
				}else{
					logger.debug("商户签名错误");
					resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
				}
			}else{
				logger.debug("参数不能为空,或者有误");
				resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			}
		}else{
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
		}
		return resultMap;
	}
	
	/**
	 * 支付信息签名
	 * */
	@RequestMapping("/payForOrder")
	@ResponseBody
	public Map<String,Object> payForOrder(@RequestParam Map<String, Object> map,HttpServletResponse response
			,HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"orderNum","payWayId" }, map);
		try {
			if(isNotNull){
				map.put("ip", IpAddressUtil.getIpAddress(request));
				Map<String,String> m = payv2PayOrderService.payForOrder(map);
				if("-1".equals(m.get("status").toString())){
					logger.debug("参数不能为空,或者有误");
					resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
					return resultMap;
				}
				m.remove("status");
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
			}else{
				logger.debug("参数不能为空,或者有误");
				resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null,
					ReturnMsgTips.ERROR_SERVER_ERROR);
		}
		return resultMap;
	}
	
	/**
	 * 手机网站h5支付
	 * @param request
	 * @param response
	 * @throws AlipayApiException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	@RequestMapping(value="/payH5Alipay")
	@ResponseBody
	public Map<String,Object> payH5Alipay(@RequestParam Map<String, Object> map,HttpServletRequest request,HttpServletResponse response){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		response.setHeader("Access-Control-Allow-Origin", "*");
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"orderNum","payWayId","appType" }, map);
		if(isNotNull){
			try {
				map.put("ip", IpAddressUtil.getIpAddress(request));
				Map<String, String> m = payv2PayOrderService.payH5Alipay(map);
				if("-1".equals(m.get("status").toString())){
					logger.debug("参数不能为空,或者有误");
					resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
					return resultMap;
				}
				m.remove("status");
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
			} catch (Exception e) {
				e.printStackTrace();
				resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
			}
		}else{
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARA_FAILED_CODE,null);
		}
		return resultMap;
	}
	
	/**
	 * 支付宝回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/aliPayCallBack")
	public void aliPayCallBack(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		try {
			String out_trade_no = map.get("out_trade_no");// 商户订单号
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
			if(payv2PayOrder!=null){
				boolean veryfy = AppAlipayNotify.verify(map,payv2PayOrder.getRateKey3());
				System.out.println("用于将支付宝回调的数据验签:"+veryfy);
				if(veryfy){//为真验证没问题
					boolean con = payv2PayOrderService.aliPayCallBack(map,payv2PayOrder);
					if(con){
						System.out.println("通知商户成功"+con);
						out.write("success");
					}else{
						System.out.println("通知商户失败"+con);
						out.write("error");
					}
				}else{
					out.write("error");
				}
			}else{
				out.write("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 支付宝h5回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/aliPayH5CallBack")
	public void aliPayH5CallBack(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		try {
			String out_trade_no = map.get("out_trade_no");// 商户订单号
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
			if(payv2PayOrder!=null){
				boolean veryfy = AlipaySignature.rsaCheckV1(map, payv2PayOrder.getRateKey3(), "utf-8");
				System.out.println("h5用于将支付宝回调的数据验签:"+veryfy);
				if(veryfy){//为真验证没问题
					boolean con = payv2PayOrderService.aliPayCallBack(map,payv2PayOrder);
					if(con){
						System.out.println("通知商户成功"+con);
						out.write("success");
					}else{
						System.out.println("通知商户失败"+con);
						out.write("error");
					}
				}else{
					out.write("error");
				}
			}else{
				out.write("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 威富通微信回调
	 * @param map
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/wftWechatPayCallBack")
	public void wftWechatPayCallBack(HttpServletRequest request,HttpServletResponse response) throws Exception{
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
            String resString = XmlUtils.parseRequst(request);
            //System.out.println("通知内容：" + resString);
            
            String respString = "fail";
            if(resString != null && !"".equals(resString)){
                Map<String,String> map = XmlUtils.toMap(resString.getBytes(), "utf-8");
                String out_trade_no = map.get("out_trade_no");// 商户订单号
                Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
                if(payv2PayOrder!=null){
	                String res = XmlUtils.toXml(map);
	                //System.out.println("通知内容：" + res);
	                if(map.containsKey("sign")){
	                    if(!SignUtils.checkParam(map, payv2PayOrder.getRateKey2())){
	                        res = "验证签名不通过";
	                        logger.debug(res);
	                        respString = "fail";
	                    }else{
	                        String status = map.get("status");
	                        if(status != null && "0".equals(status)){
	                            String result_code = map.get("result_code");
	                            if(result_code != null && "0".equals(result_code)){
	                            	//后台数据订单状态更新等操作
	                            	boolean con = payv2PayOrderService.wftWechatPayCallBack(map);
	                            	if(con){
	                            		respString = "success";
	                            	}
	                            } 
	                        } 
	                    }
	                }
                }
            }
            response.getWriter().write(respString);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * 兴业深圳回调
	 * @param map
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/aliScanCallBack")
	public void ms(HttpServletRequest request,HttpServletResponse response) throws Exception{
		String resString = XmlUtils.parseRequst(request);
		Map<String,String> map = XmlUtils.toMap(resString.getBytes(), "utf-8");
		System.out.println("兴业深圳回调：" + map.toString());
		try {
			String orderNum = map.get("out_trade_no");
			Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(orderNum);
			if(null != payOrder){
				Map<String, String> params = new HashMap<String, String>();
				params.put("out_trade_no", orderNum);
				if(SignUtils.checkParam(map, payOrder.getRateKey2())){
					System.out.println("================兴业深圳验签通过================");
					if("0".equals(map.get("pay_result").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					params.put("trade_no", map.get("transaction_id"));
					params.put("out_transaction_id", map.get("transaction_id"));
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String time = map.get("time_end");
					Date date = sdf.parse(time);
					params.put("gmt_payment", DateUtil.DateToString(date, "yyyy-MM-dd HH:mm:ss"));
					//params.put("gmt_payment",DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
					params.put("total_amount", DecimalUtil.centsToYuan(map.get("total_fee")));
					
					boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
					System.out.println("================兴业深圳写数据库================");
					if(!bool){
						System.out.println("================兴业深圳写数据库或者回调商户失败================");
						response.getWriter().write("fail");
					}else {
						response.getWriter().write("success");
					}
				}else{
					System.out.println("验签失败");
					response.getWriter().write("fail");
				}
			}else{
				response.getWriter().write("fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("fail");
		} finally {
			response.getWriter().close();
		}
		
	}
	
	/**
	 * 支付宝回调（点游）
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/dyAliPayCallBack")
	public void dyAliPayCallBack(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		try{
			String out_trade_no = map.get("out_trade_no");// 商户订单号
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
			if(payv2PayOrder!=null){
				boolean veryfy = AppAlipayNotify.verify(map,payv2PayOrder.getRateKey3());
				System.out.println("用于将支付宝回调的数据验签:"+veryfy);
				if(veryfy){//为真验证没问题
					boolean con = payv2PayOrderService.dyAliPayCallBack(map,payv2PayOrder);
					if(con){
						System.out.println("通知商户成功"+con);
						out.write("success");
					}else{
						System.out.println("通知商户失败"+con);
						out.write("error");
					}
				}else{
					out.write("error");
				}
			}else{
				out.write("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 订单查询
	 * */
	@RequestMapping("/queryOrderNum")
	@ResponseBody
	public Map<String,Object> queryOrderNum(@RequestParam Map<String, Object> map,HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			resultMap = payv2PayOrderService.queryOrderNum(map);
			if(resultMap.containsKey("status")&&resultMap.get("status").equals(PayFinalUtil.PAYORDER_QUERY_FAIL)){
				resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null,
						"订单不存在");
				return resultMap;
			}
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null,
					ReturnMsgTips.ERROR_SERVER_ERROR);
		}
		return resultMap;
	}
	
	/**
	 * 订单查询  （提供给商户服务器查询订单）
	 * */
	@RequestMapping("/queryOrder")
	@ResponseBody
	public Map<String,Object> queryOrder(@RequestParam Map<String, Object> map,HttpServletResponse response) {
		long time1 = System.currentTimeMillis();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"appKey"}, map);
		try {
			if(isNotNull){
				//检查appKey是否有效!先查询app审核是否通过，再查询app所属商户状态是否通过
				Payv2BussCompanyApp pbca = payv2BussCompanyAppService.checkApp(map.get("appKey").toString());
				if(pbca!=null){
					//验签
					boolean con = PaySignUtil.checkSign(map, pbca.getAppSecret());
					if(con){
						//商家订单和支付集订单同时不存在
						if(!map.containsKey("orderNum")&&!map.containsKey("bussOrderNum")){
							logger.debug("参数不能为空,或者有误");
							resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
						}else{
							Map<String, Object> m = payv2PayOrderService.queryOrder(map,pbca);
							
							if(m.get("status").equals("1")){
								Payv2PayOrder payOrder = (Payv2PayOrder)m.get("payOrder");
								resultMap.put("order_name", payOrder.getOrderName());
								resultMap.put("buss_order_num", payOrder.getMerchantOrderNum());
								resultMap.put("order_num", payOrder.getOrderNum());
								resultMap.put("pay_money", payOrder.getPayMoney());
								resultMap.put("pay_discount_money", payOrder.getPayDiscountMoney());
								resultMap.put("pay_time", DateStr(payOrder.getPayTime()==null?payOrder.getCreateTime():payOrder.getPayTime()));
								resultMap.put("create_time", DateStr(payOrder.getCreateTime()));
								//参数签名
								String sign = PaySignUtil.getSign(resultMap, pbca.getAppSecret());
								resultMap.put("sign", sign);
								resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
							}else if(m.get("status").equals("2")){ //订单未支付
								resultMap = ReMessage.resultBack(ParameterEunm.NOTPAY_FAILED_CODE,null);
							}else if(m.get("status").equals("3")){//参数不能为空,或者有误
								resultMap = ReMessage.resultBack(ParameterEunm.TRANSACTION_NOT_EXIST,null);
							}else{  //支付失败
								resultMap = ReMessage.resultBack(ParameterEunm.PAY_FAILED_CODE,null);
							}
						}
					}else{
						logger.debug("商户签名错误");
						resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
					}
				}else{
					logger.debug("appKey无效");
					resultMap = ReMessage.resultBack(ParameterEunm.APP_KEY_INVALID,null);
				}
			}else{
				logger.debug("参数不能为空,或者有误");
				resultMap = ReMessage.resultBack(ParameterEunm.PARA_FAILED_CODE,null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
		}
		logger.debug("订单查询请求耗时："+(System.currentTimeMillis()-time1));
		return resultMap;
	}
	
	/**
	* @Description:支付信息   线下扫码支付
	* @param request
	* @param response
	* @param map
	* @return    设定文件 
	* @return Map<String,Object>    返回类型 
	* @date 2016年9月2日 下午4:37:51 
	* @throws
	*/
	@ResponseBody
	@RequestMapping(value = "/payOrderShopDetail")
	public Map<String,Object> payOrderShopDetail(HttpServletRequest request,HttpServletResponse response,@RequestParam Map<String, Object> map){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean appKeyCon = ObjectUtil.checkObject(new String[] { "appKey" }, map);
		if(appKeyCon){
			try {
				Map<String, Object> m = payv2PayOrderService.payOrderShopDetail(map);
				if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL_OK)){
					resultMap = ReMessage.resultBack(ParameterEunm.PAY_FAILED_SUCCESS, null);
				}else if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL)){
					resultMap = ReMessage.resultBack(ParameterEunm.ORDER_ERROR, null);
				}else{
					m.remove("status");
					resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
				}
			} catch (Exception e) {
				e.printStackTrace();
				resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null, null);
			}
		}else{
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
		}
		return resultMap;
	}
	
	/**
	 * 天付宝回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/tfbCallBack")
	public void tfbCallBack(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		PrintWriter out = response.getWriter();
		out.write("success");
		if(out!=null){
			out.close();
		}
	}
	
	/**
	 * 测试回调专用
	 */
	@RequestMapping(value="/test")
	public void test(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//{mhtCharset=UTF-8, appId=148999970238152, mhtOrderNo=1510020557256, signType=MD5, mhtReserved=,
		//nowPayOrderNo=200601201711071009116728204, funcode=N002, transStatus=A001, 
		//signature=32716b53cc5bbabdae99a1f3b938cda0, version=1.0.0}
		System.out.println("回调！：" + map.toString());
		
		BufferedReader reader = request.getReader(); 
		StringBuilder reportBuilder = new StringBuilder(); 
		String tempStr = ""; 
		while((tempStr = reader.readLine()) != null){
		       reportBuilder.append(tempStr); 
		} 
		String reportContent = reportBuilder.toString();
		System.out.println("reportContent:"+reportContent);
		
		
		PrintWriter out = response.getWriter();
		out.write("success");
		if(out!=null){
			out.close();
		}
	}
	
	/**
	 * 测试回调专用
	 */
	@RequestMapping(value="/returnTest")
	public void returnTest(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//{mhtCharset=UTF-8, appId=148999970238152, mhtOrderNo=1510020557256, signType=MD5, mhtReserved=,
		//nowPayOrderNo=200601201711071009116728204, funcode=N002, transStatus=A001, 
		//signature=32716b53cc5bbabdae99a1f3b938cda0, version=1.0.0}
		System.out.println("return回调！：" + map.toString());
		PrintWriter out = response.getWriter();
		out.write("success");
		if(out!=null){
			out.close();
		}
	}
	
	/**
	 * 国付回调
	 * @throws IOException 
	 */
	@RequestMapping("/guofuCallBack")
	public void guofuCallBack(@RequestParam Map<String, String> map, HttpServletResponse response) throws IOException{
		System.out.println("国付回调：" + map.toString());
		//amount=0.01, channelOrderno=13590497016011201708221119501018, channelTraceno=199520378478201708226176402891, merchName=一站网络(测试), merchno=820440348160001, orderno=82170822100000350828, payType=8, 
		//signature=AB514D06C70B9D5E533DBBF7EBAC68E8, status=1, traceno=1503367297796, transDate=2017-08-22, transTime=10:02:45
		try {
			String orderNum = map.get("traceno");
			Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(orderNum);
			if(null != payOrder){
				Map<String, String> params = new HashMap<String, String>();
				String signature = GuoFuPay.signature(map, payOrder.getRateKey2(), "GBK");
				//验签
				if (signature.equals(map.get("signature"))) {
					//支付成功
					if("1".equals(map.get("status").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					String date = map.get("transDate") + " " + map.get("transTime");
					
					params.put("gmt_payment", date);
					params.put("trade_no", map.get("orderno"));
					params.put("total_amount", map.get("amount"));
					boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
					System.out.println("================国付回调写数据库================");
					if(!bool){
						System.out.println("================国付回调写数据库或者回调商户失败================");
						response.getWriter().write("fail");
					}
					response.getWriter().write("success");
				}else{
					System.out.println("验签失败");
					response.getWriter().write("fail");
				}
			}else{
				System.out.println("获取不到订单");
				response.getWriter().write("fail");
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("fail");
		}finally {
			response.getWriter().close();
		}
	}
	
	/**
	 * 民生厦门回调
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/xmmsCallBack")
	@ResponseBody
	public Map<String, String> xmmsCallBack(HttpServletResponse response, HttpServletRequest request) throws IOException{
		
		Map<String, String> resultMap= new HashMap<String, String>();
		resultMap.put("resCode", "0001");
		try {
			String paramString = getParamString(request);
			System.out.println("民生厦门回调：" + paramString);
			Map<String, String> map = (Map)JSON.parse(paramString);
			//String orderNum = map.get("orderNum");
			Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(map.get("orderNum"));
			if(null != payOrder){
				String signature = EpaySignUtil.sign(payOrder.getRateKey2(), map);
				//验签(天坑，签名里可能会带空格)
				if (signature.trim().equals(map.get("signStr").trim())) {
					Map<String, String> params = new HashMap<String, String>();
					//支付成功
					if("0000".equals(map.get("resultCode").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					//String date = map.get("transDate") + " " + map.get("transTime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date parse = sdf.parse(map.get("payTime"));
					params.put("gmt_payment", sdfs.format(parse));
					params.put("trade_no", map.get("payNum"));
					params.put("total_amount", map.get("payMoney"));
					boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
					System.out.println("================民生厦门回调写数据库================");
					if(!bool){
						System.out.println("================民生厦门回调写数据库或者回调商户失败================");
					}else {
						resultMap.put("resCode", "0000");
					}
					
				}else{
					System.out.println("验签失败");
				}
			}else{
				System.out.println("获取不到订单");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 骏狐回调
	 * @throws IOException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/junhuCallBack")
	@ResponseBody
	public Map<String, String> junhuCallBack(HttpServletResponse response, HttpServletRequest request) throws IOException{
		
		Map<String, String> resultMap= new HashMap<String, String>();
		resultMap.put("resCode", "0001");
		try {
			String paramString = getParamString(request);
			System.out.println("骏狐回调：" + paramString);
			Map<String, String> map = (Map)JSON.parse(paramString);
			//String orderNum = map.get("orderNum");
			Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(map.get("orderNum"));
			if(null != payOrder){
				String signature = EpaySignUtil.sign(payOrder.getRateKey2(), map);
				//验签(天坑，签名里可能会带空格)
				if (signature.trim().equals(map.get("signStr").trim())) {
					Map<String, String> params = new HashMap<String, String>();
					//支付成功
					if("0000".equals(map.get("resultCode").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					//String date = map.get("transDate") + " " + map.get("transTime");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date parse = sdf.parse(map.get("payTime"));
					params.put("gmt_payment", sdfs.format(parse));
					params.put("trade_no", map.get("payNum"));
					params.put("total_amount", map.get("payMoney"));
					boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
					System.out.println("================骏狐回调写数据库================");
					if(!bool){
						System.out.println("================骏狐回调写数据库或者回调商户失败================");
					}else {
						resultMap.put("resCode", "0000");
					}
					
				}else{
					System.out.println("验签失败");
				}
			}else{
				System.out.println("获取不到订单");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 商务通回调
	 */
	@RequestMapping("/swtCallBack")
	public void swtCallBack(@RequestParam Map<String, String> map, HttpServletResponse response) throws IOException{
		logger.info("商务通支付回调："+map.toString());
		try {
			Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(map.get("orderId"));
			if(null!=payOrder){
				String sign = SWTPay.paySign(payOrder.getRateKey2(), map);
				if(sign.equals(map.get("signature"))){
					Map<String, String> params = new HashMap<String, String>();
					//支付成功
					if("1".equals(map.get("status").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date parse = sdf.parse(map.get("completeDate"));
					
					params.put("gmt_payment", sdfs.format(parse));
					params.put("trade_no", map.get("payOrdNo"));
					params.put("total_amount", DecimalUtil.centsToYuan(map.get("orderAmt")));
					boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
					System.out.println("================商务通回调写数据库================");
					if(!bool){
						System.out.println("================商务通回调写数据库或者回调商户失败================");
					}else {
						response.getWriter().write("success");
					}
				}else {
					System.out.println("验签失败");
					logger.info("商务通支付回调：验签失败！");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().close();
		}
	}
	
	/**
	 * 浦发深圳回调
	 * @throws IOException 
	 */
	@RequestMapping("/pfszCallBack")
	public void pfszCallBack(@RequestParam Map<String, Object> map, HttpServletResponse response) throws IOException {
		logger.info("浦发深圳回调" + map.toString());
		
		try {
			//退款回调
			if("02".equals(map.get("transId"))){
				logger.info("-----------浦发深圳退款回调-----------");
				String refundNum = map.get("orderNo")+"";
				Payv2PayOrderRefund orderRefund = new Payv2PayOrderRefund();
				orderRefund.setRefundNum(refundNum);
				orderRefund = refundService.selectSingle(orderRefund);
				if(null != orderRefund){
					Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(orderRefund.getOrderNum());
					if(PFSZBankPay.verify(map, payOrder.getRateKey6())){
						if("0000".equals(map.get("respCode")+"")){
							response.getWriter().write("success");
							/*orderRefund.setRefundStatus(1);
							orderRefund.setRefundTime(new Date());
							refundService.update(orderRefund);
							response.getWriter().write("success");*/
						}
					}else{
						System.out.println("验签失败");
						response.getWriter().write("fail");
					}
				}else{
					response.getWriter().write("fail");
				}
			}else {		//支付回调
				logger.info("-----------浦发深圳支付回调-----------");
				Payv2PayOrder payOrder = payv2PayOrderService.getOrderInfo(map.get("orderNo") + "");
				if(null != payOrder){
					Map<String, String> params = new HashMap<String, String>();
					params.put("out_trade_no", map.get("orderNo") + "");
					if(PFSZBankPay.verify(map, payOrder.getRateKey5())){
						System.out.println("================浦发深圳验签通过================");
						if("0000".equals(map.get("respCode")+"")){
							params.put("trade_status", "TRADE_SUCCESS");
						}
						params.put("trade_no", map.get("orderId")+"");
						params.put("out_transaction_id", map.get("transactionId")+"");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						String time = map.get("timeEnd") + "";
						Date date = sdf.parse(time);
						params.put("gmt_payment", DateUtil.DateToString(date, "yyyy-MM-dd HH:mm:ss"));
						//params.put("gmt_payment",DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
						params.put("total_amount", DecimalUtil.centsToYuan(map.get("transAmt")+""));
						
						boolean bool = payv2PayOrderService.aliPayCallBack(params, payOrder);
						System.out.println("================浦发深圳写数据库================");
						if(!bool){
							System.out.println("================浦发深圳写数据库或者回调商户失败================");
							response.getWriter().write("fail");
						}else {
							response.getWriter().write("success");
						}
					}else{
						System.out.println("验签失败");
						response.getWriter().write("fail");
					}
				}else{
					response.getWriter().write("fail");
				}
			}
		} catch (Exception e) {
			System.out.println("================浦发深圳回调异常================");
			e.printStackTrace();
			response.getWriter().write("fail");
		} finally {
			response.getWriter().close();
		}
	}
	
	/**
	 * 现在支付回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/nowCallBack")
	public void nowCallBack(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> map = new HashMap<>();
//		System.out.println("回调参数map:"+map);
		//appId=148999970238152&channelOrderNo=4200000011201711073011992180&deviceType=0601
		//&discountAmt=0&funcode=N001&mhtCharset=UTF-8&mhtCurrencyType=156&mhtOrderAmt=1
		//&mhtOrderName=1510024609887&mhtOrderNo=1510024609887&mhtOrderStartTime=20171107111649
		//&mhtOrderTimeOut=3600&mhtOrderType=01&nowPayOrderNo=200601201711071116446791303
		//&oriMhtOrderAmt=1&payChannelType=13&payConsumerId=o0kRqwLAl0Sap-0fbqEKJgjHm3RY
		//&payTime=20171107111714&signType=MD5&signature=44e6c9b00563b5f899f4f7825089cf2f
		//&transStatus=A001&version=1.0.0
		PrintWriter out = null;
//		System.out.println("parameter:"+request.getParameter("mhtOrderNo"));
		
		try{
			BufferedReader reader = request.getReader(); 
			StringBuilder reportBuilder = new StringBuilder(); 
			String tempStr = ""; 
			while((tempStr = reader.readLine()) != null){
			       reportBuilder.append(tempStr); 
			} 
			String reportContent = reportBuilder.toString();
			System.out.println("现在支付回调参数:"+reportContent);
			
			map = StringHandleUtil.toMap(reportContent);
			//去除签名值
	        String signature = map.remove("signature").toString();
	        
			out = response.getWriter();
			String out_trade_no = map.get("mhtOrderNo").toString();// 商户订单号
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
			if(payv2PayOrder!=null){
				if(signature.equals(NowPayUtil.getSign(map, payv2PayOrder.getRateKey2()))){
					Map<String, String> params = new HashMap<String, String>();
					if("A001".equals(map.get("transStatus").toString())){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					params.put("trade_no", map.get("nowPayOrderNo").toString());
					params.put("out_transaction_id", map.get("channelOrderNo").toString());
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String time = map.get("payTime").toString();
					Date date = sdf.parse(time);
					params.put("gmt_payment", DateUtil.DateToString(date, "yyyy-MM-dd HH:mm:ss"));
					//params.put("gmt_payment",DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
					params.put("total_amount", DecimalUtil.centsToYuan(map.get("mhtOrderAmt").toString()));
					
					boolean con = payv2PayOrderService.aliPayCallBack(params,payv2PayOrder);
					if(con){
						System.out.println("通知商户成功"+con);
						out.write("success=Y");
					}else{
						System.out.println("通知商户失败"+con);
						out.write("error");
					}
				}else{
					System.out.println("================现在支付验签失败================");
					out.write("error");
				}
			}else{
				out.write("error");
			}
		} catch (Exception e) {
			System.out.println("================现在支付回调异常================");
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 衫德支付回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/sdCallBack")
	public void sdCallBack(@RequestParam Map<String, Object> map,HttpServletRequest request,HttpServletResponse response){
		System.out.println("---衫德回调参数map:"+map);
		//{sign=ijWOsqWk9wgHUC3gx8N5ff3HcQlrQX2GcTySb1LBbtMJPiBCVYB8QxbhqZ9WE1DlhQ73Sx4cjsDCaKDhazY3yOtv
		//tqvA96XY1P0zgjE+x3bwwev24Qw/gwDQVteIZwIxif8g4P2BGLrBwsG6l8zv4CeORaDOL671BpNlyyurHdnMEtxAMyCD9T+
		//HOzKSnv9M3chN941I9x7bbp7HbJxZHXtjqBzDPPwFsF/TBXjeCXXgd1dFpPmgfm98yDauI6rMqjDyP960o0vEcbsVlCpFm
		//9xIENYz5SiuQiGVgav/gDgqOjAtlX//Le8/SO6juXVod+R7yitkLI/RBwvYK9BA==, extend=, signType=01, 
		//data={"body":{"orderCode":"1510305503057","tradeNo":"2017111017180800000004072372"
		//,"clearDate":"20171110","orderStatus":"1","payTime":"20171110171808","buyerPayAmount"
		//:"000000000001","accNo":"","midFee":"000000000000","totalAmount":"000000000001","discAmount"
		//:"000000000000","bankserial":"4200000005201711103705917000"},"head":{"respCode":"000000"
		//,"respMsg":"成功","respTime":"20171110171843","version":"1.0"}}, charset=UTF-8}
		PrintWriter out = null;
		
		try{
			out = response.getWriter();
			String respData = map.get("data").toString();
			String respSign =map.get("sign").toString(); 
			
			JSONObject respJson = JSONObject.parseObject(respData);
			JSONObject bodyObject = respJson.getJSONObject("body");
			String orderCode = bodyObject.getString("orderCode");
			
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(orderCode);
			if(payv2PayOrder!=null){
				boolean verify =CryptoUtil.verifyDigitalSign(respData.getBytes("UTF-8")
						, Base64.decodeBase64(respSign), ShandeSign.getPubKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy06Nc6hNkJVetRauhD9P6WrBlbJWgevQsYMkImtCLdBrEP/aMiPXJqZLIcM0B3mEzSluge4yMrGHW6ZYAq972ja3QZpqE0fKlGYFNqxy6j1rah1kI9K7N1/Hb9SvgzbxO5+vZfYdUQRxDVgeB0yhsohVD2Ce10h+qe8hf3uKmqzwNAP0ZfaSLenHdzk6kHzVUqyts3uesHiOw1xDkjzLie7IPdCyw15czKowXGOgiGc9Ip8+HmInhmq3TOEOkhli7AD89HwSym79ORBWPVJ5GEi7jyFEQ+XXUu4QutfH9jn5XfUcuVk9O4vPb1VjWeh5geWlmQ5IgLljG3WSCC9towIDAQAB")
						, "SHA1WithRSA");
				if(verify&&!bodyObject.containsKey("refundAmount")){
					Map<String, String> params = new HashMap<String, String>();
					if("1".equals(bodyObject.getString("orderStatus"))){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					params.put("trade_no", bodyObject.getString("tradeNo"));
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					String time = bodyObject.getString("payTime");
					Date date = sdf.parse(time);
					params.put("gmt_payment", DateUtil.DateToString(date, "yyyy-MM-dd HH:mm:ss"));
					//params.put("gmt_payment",DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
					//12位数
					String totalAmount = bodyObject.getString("totalAmount");
					params.put("total_amount", DecimalUtil.centsToYuan(Integer.valueOf(totalAmount)+""));
					
					boolean con = payv2PayOrderService.aliPayCallBack(params,payv2PayOrder);
					if(con){
						out.write("respCode=000000");
					}else{
						out.write("error");
					}
				}else{
					System.out.println("================衫德验签失败================");
					out.write("error");
				}
			}else{
				System.out.println("================查询订单不存在================");
				out.write("error");
			}
			
		} catch (Exception e) {
			System.out.println("================衫德回调异常================");
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 支付宝转账
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/aliZzCallBack")
	public void aliZzCallBack(@RequestParam Map<String, Object> map,HttpServletRequest request,HttpServletResponse response){
		System.out.println("---支付宝转账map:"+map.toString());
		//{p8_charset=UTF-8, p4_status=1, p7_paychannelnum=, p11_remark=, p3_money=1, 
		//p10_sign=2F134304A907E8A9AF49F9F55CA5221C, p1_usercode=28C85D0FB0E5475B9498C86971FC851F, 
		//p2_order=1511228378038, p6_paymethod=4, p5_jtpayorder=20171121093957650918, p9_signtype=MD5}
		PrintWriter out = null;
		
		try{
			out = response.getWriter();
			ResponseBean rbean = new ResponseBean();
			String orderNum = request.getParameter("p2_order");
			String payMoney = request.getParameter("p3_money");
			String status = request.getParameter("p4_status");
			String trade_no = request.getParameter("p5_jtpayorder");
			
			rbean.setP1_usercode(request.getParameter("p1_usercode"));
			rbean.setP2_order(orderNum);
			rbean.setP3_money(payMoney);
			rbean.setP4_status(status);
			rbean.setP5_jtpayorder(trade_no);
			rbean.setP6_paymethod(request.getParameter("p6_paymethod"));
			rbean.setP7_paychannelnum(request.getParameter("p7_paychannelnum"));
			rbean.setP8_charset(request.getParameter("p8_charset"));
			rbean.setP9_signtype(request.getParameter("p9_signtype"));
			rbean.setP10_sign(request.getParameter("p10_sign"));
			rbean.setP11_remark(request.getParameter("p11_remark"));
			
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(orderNum);
			if(payv2PayOrder!=null){
				String sign = AliZzPay.GetSign1(rbean,payv2PayOrder.getRateKey2());
				if (sign.equals(rbean.getP10_sign())) {
					
					Map<String, String> params = new HashMap<String, String>();
					if("1".equals(status)){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					params.put("trade_no", trade_no);
					
					params.put("gmt_payment", DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
					//params.put("gmt_payment",DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD_HH_MM_SS));
					
					params.put("total_amount", payMoney);
					
					boolean con = payv2PayOrderService.aliPayCallBack(params,payv2PayOrder);
					if(con){
						out.write("success");
					}else{
						out.write("error");
					}
				} else {
					System.out.println(".........................支付宝转账WAP签名未通过.........................");
				}
			}else{
				System.out.println("================查询订单不存在================");
				out.write("error");
			}
		} catch (Exception e) {
			System.out.println("================衫德回调异常================");
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 无卡支付回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/wukaCallBack")
	public void wukaCallBack(@RequestParam Map<String, Object> map,HttpServletRequest request,HttpServletResponse response){
		System.out.println("---无卡支付map:"+map.toString());
		//merNo=Z00000000000***&orderNo=20170000000000043760&transAmt=20000&orderDate=20170419
		//&respCode=0000&respDesc=%E6%94%AF%E4%BB%98%E6%88%90%E5%8A%9F&payId=ZT300120170419154248044559
		//&payTime=20170419154248&signature=ef2f005fc90daf1cd6d7f0886644dcba
		PrintWriter out = null;
		try{
			out = response.getWriter();
			
			String orderNum = map.get("orderNo").toString();
			String signature = map.get("signature").toString();
			String respCode = map.get("respCode").toString();
			String payId = map.get("payId").toString();
			Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(orderNum);
			if(payv2PayOrder!=null){
				String sign = WukaSign.getCallSign(map,payv2PayOrder.getRateKey2());
				if (sign.equals(signature)) {
					Map<String, String> params = new HashMap<String, String>();
					if("0000".equals(respCode)){
						params.put("trade_status", "TRADE_SUCCESS");
					}
					params.put("trade_no", payId);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date parse = sdf.parse(map.get("payTime").toString());
					
					params.put("gmt_payment", sdfs.format(parse));
					
					params.put("total_amount", DecimalUtil.centsToYuan(map.get("transAmt").toString()));
					
					boolean con = payv2PayOrderService.aliPayCallBack(params,payv2PayOrder);
					if(con){
						out.write("success");
					}else{
						out.write("error");
					}
				}else {
					System.out.println(".........................无卡支付签名未通过.........................");
				}
				
			}else{
				System.out.println("================查询订单不存在================");
				out.write("error");
			}
		} catch (Exception e) {
			System.out.println("================无卡支付回调异常================");
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	/**
	 * 招行厦门支付回调
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	@RequestMapping(value="/zsxmCallBack")
	public void zsxmCallBack(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out = null;
		try{
			out = response.getWriter();
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			response.setHeader("Content-type", "text/html;charset=UTF-8");
            String resString = XmlUtils.parseRequst(request);
            String respString = "fail";
            if(resString != null && !"".equals(resString)){
                Map<String,String> map = XmlUtils.toMap(resString.getBytes(), "utf-8");
                System.out.println("---招行厦门map:"+map.toString());
                String out_trade_no = map.get("out_trade_no");// 商户订单号
                Payv2PayOrder payv2PayOrder = payv2PayOrderService.getOrderInfo(out_trade_no);
                if(payv2PayOrder!=null){
	                if(map.containsKey("sign")){
	                    if(!ZsxmSignUtils.checkParam(map, payv2PayOrder.getRateKey2())){
	                        System.out.println("验证签名不通过");
	                        respString = "fail";
	                    }else{
	                        String return_code = map.get("return_code");
	                        if(return_code != null && "SUCCESS".equals(return_code)){
	                            String result_code = map.get("result_code");
	                            if(result_code != null && "SUCCESS".equals(result_code)){
	                            	
	                            	Map<String, String> params = new HashMap<String, String>();
	            					params.put("trade_status", "TRADE_SUCCESS");
	            					
	            					params.put("trade_no", map.get("transaction_id"));
	            					
	            					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	            					SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            					Date parse = sdf.parse(map.get("time_end"));
	            					
	            					params.put("gmt_payment", sdfs.format(parse));
	            					params.put("out_transaction_id", map.get("out_transaction_id"));
	            					params.put("total_amount", DecimalUtil.centsToYuan(map.get("total_fee")));
	                            	
	                            	//后台数据订单状态更新等操作
	                            	boolean con = payv2PayOrderService.aliPayCallBack(params,payv2PayOrder);
	                            	if(con){
	                            		System.out.println("通知商户成功");
	                            		respString = "<xml><return_code>SUCCESS</return_code></xml>";
	                            	}else{
	                            		System.out.println("通知商户失败");
	                            	}
	                            } 
	                        } 
	                    }
	                }
                }else{
                	System.out.println("================查询订单不存在================");
                }
            }
            out.write(respString);
		} catch (Exception e) {
			System.out.println("================招行厦门回调异常================");
			e.printStackTrace();
			out.write("error");
		}finally{
			if(out!=null){
				out.close();
			}
		}
	}
	
	protected String getParamString(HttpServletRequest request) {
		StringBuffer buffer = new StringBuffer();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		try {
			inputStream = request.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}

			inputStream = null;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				inputStreamReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return buffer.toString();
	}
	
	private String DateStr(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		return str;
	}
	
	@RequestMapping(value="/aaaaaaa")
	public static void CheckAgent(@RequestParam Map<String, String> map,HttpServletRequest request,HttpServletResponse response)    
	{    
	    String agent = request.getHeader("User-Agent");
	    String[] keywords = { "Android", "iPhone", "iPod", "iPad", "Windows Phone", "MQQBrowser" };    
	  
	       //排除 Windows 桌面系统    
	        if (agent.indexOf("Windows NT")==-1 || (agent.indexOf("Windows NT")!=-1 && agent.indexOf("compatible; MSIE 9.0;")!=-1))    
	        {    
	            //排除 苹果桌面系统    
	            if (agent.indexOf("Windows NT")==-1 && agent.indexOf("Macintosh")==-1)    
	            {    
	            	for (String string : keywords) 
	                {    
	                    if (agent.indexOf(string)!=-1)    
	                    {    
	                        break;    
	                    }    
	                }    
	            }    
	        }    
	  
	}
}
