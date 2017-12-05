package com.pay.manger.controller.payv2;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.wftpay.weChatSubscrPay.utils.XmlUtils;

@Controller
@RequestMapping("/returnUrl/*")
public class ReturnUrlController {
	@Autowired
	private Payv2PayOrderService payv2PayOrderService;
	@Autowired
	private Payv2BussCompanyAppService Payv2BussCompanyAppService;
	
	//支付宝回跳页面
	@RequestMapping("/alipay.do")
	public ModelAndView alipay(@RequestParam Map<String, String> map, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("pay/returnApp");
		/*String aliStr = "{total_amount=0.02, "
				+ "timestamp=2017-07-14 14:42:41, "
				+ "sign=Emruyqvwc9XHm4N55GITG05EFKDYu+NIivy+JGfKOIUt5EmBpkYSu8EPwJWLLs3j86Zh5VEeLwyMIno8XgBVm0wPgTIBpYXERbWg+HFNwNrvsaT96j82ilt9+vzWZAiblDKNpSjHnx+4wb50//VjTuiSeyXA+yNQ6+3evlI7Ifw=, "
				+ "trade_no=2017071421001004440229034319, "
				+ "sign_type=RSA, auth_app_id=2017021005611761, "
				+ "charset=utf-8, "
				+ "seller_id=2088621176118104, "
				+ "method=alipay.trade.wap.pay.return, "
				+ "app_id=2017021005611761, "
				+ "out_trade_no=DD2017071414421982067525, "
				+ "version=1.0}";*/
		try {
			Map<String,Object> paramMap = new HashMap<>();
			String orderNum = map.get("out_trade_no");
			Payv2PayOrder order = payv2PayOrderService.getOrderInfo(orderNum);
			if(order!=null){
				paramMap.put("id", order.getAppId());
				Payv2BussCompanyApp pbca = Payv2BussCompanyAppService.queryDetailById(order.getAppId());
				paramMap.clear();
				paramMap.put("result_code", PayFinalUtil.SUSSESS_CODE); // 成功
				paramMap.put("pay_money", map.get("total_amount"));
				paramMap.put("pay_discount_money", map.get("total_amount"));
				paramMap.put("pay_time", DateStr(order.getCreateTime()));
				paramMap.put("order_num", map.get("out_trade_no"));
				paramMap.put("buss_order_num", order.getMerchantOrderNum());
				//商户不传或传空，回调不返回
				if(order.getRemark()!=null&&!"".equals(order.getRemark())){
					paramMap.put("remark", URLEncoder.encode(order.getRemark())); // 自定义字段
				}
				// 参数签名
				String sign = PaySignUtil.getSign(paramMap, pbca.getAppSecret());
				paramMap.put("sign", sign);
				
				String returnUrl = order.getReturnUrl();
				if(returnUrl!=null&&returnUrl.length()>5){
					if(returnUrl.startsWith("http:")
							||returnUrl.startsWith("https:")){
						returnUrl = "redirect:"+returnUrl;
					}else{
						returnUrl = "redirect://"+returnUrl;
					}
					returnUrl = returnUrl+"?"+PaySignUtil.getParamStr(paramMap)+"&sign="+sign;
					mv.setViewName(returnUrl);
				}
				paramMap.put("payPrice", map.get("total_amount"));
				paramMap.put("companyKey", pbca.getCompanyKey());
				paramMap.put("returnUrl", returnUrl);
			}
			mv.addObject("map", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new ModelAndView("pay/return");
		}
		
		return mv;
	}
	
	//汇付宝回跳页面
	@RequestMapping("/hfb.do")
	public ModelAndView hfb(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		//String zf="{pay_amt=0.01, sign=4b12115d722aeaf5701052bdb069b425, result=1, jnet_bill_no=H1707143553305AL, pay_type=30, remark=jinfu, agent_bill_id=DD2017071410222107699496, fbtn=, agent_id=2105871, pay_message=}";
		ModelAndView mv = new ModelAndView("pay/returnApp");
		try {
			Map<String,Object> paramMap = new HashMap<>();
			String orderNum = map.get("agent_bill_id").toString();
			Payv2PayOrder order = payv2PayOrderService.getOrderInfo(orderNum);
			if(order!=null){
				paramMap.put("id", order.getAppId());
				Payv2BussCompanyApp pbca = Payv2BussCompanyAppService.queryDetailById(order.getAppId());
				paramMap.clear();
				if("1".equals(map.get("result"))){
					paramMap.put("result_code", PayFinalUtil.SUSSESS_CODE); // 成功
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", map.get("pay_amt"));
				}else{
					paramMap.put("result_code", PayFinalUtil.CANCEL_CODE); // 失败
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", "0.00");
				}
				//商户不传或传空，回调不返回
				if(order.getRemark()!=null&&!"".equals(order.getRemark())){
					paramMap.put("remark", URLEncoder.encode(order.getRemark())); // 自定义字段
				}
				paramMap.put("pay_time", DateStr(order.getCreateTime()));
				paramMap.put("order_num", map.get("agent_bill_id"));
				paramMap.put("buss_order_num", order.getMerchantOrderNum());
				// 参数签名
				String sign = PaySignUtil.getSign(paramMap, pbca.getAppSecret());
				paramMap.put("sign", sign);
				
				String returnUrl = order.getReturnUrl();
				if(returnUrl!=null&&returnUrl.length()>5){
					if(returnUrl.startsWith("http:")
							||returnUrl.startsWith("https:")){
						returnUrl = "redirect:"+returnUrl;
					}else{
						returnUrl = "redirect://"+returnUrl;
					}
				}
				if(returnUrl!=null&&!"".equals(returnUrl)){
					returnUrl = returnUrl+"?"+PaySignUtil.getParamStr(paramMap)+"&sign="+sign;
					mv.setViewName(returnUrl);
				}
				if("1".equals(map.get("result"))){
					paramMap.put("payPrice", map.get("pay_amt"));
				}else{
					paramMap.put("payPrice", order.getPayMoney());
				}
				paramMap.put("companyKey", pbca.getCompanyKey());
			}
			mv.addObject("map", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new ModelAndView("pay/return");
		}
		return mv;
	}
	
	//平安回跳页面
	@RequestMapping("/pingan.do")
	public ModelAndView pingan(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		//amount=1&ord_no=91503649027370871187&out_no=DD20170825161601536476691
		//&rand_str=JaKeloHbwt5elNq02mxfknWFLBsBovz7Fjm1I4cexitT5TT8grnAOkgAWIbkeKsT4OVMS71qqvjwocEFE2fsmw2jfdDtY5m3UiQNqRdQmwmLJ1qn3GPqcSJr5mU4sh7m
		//&status=2&timestamp=1503649041&sign=28512e5256413224bd9870a1ccff1265;
		ModelAndView mv = new ModelAndView("pay/returnApp");
		try {
			Map<String,Object> paramMap = new HashMap<>();
			String orderNum = map.get("out_no").toString();
			Payv2PayOrder order = payv2PayOrderService.getOrderInfo(orderNum);
			if(order!=null){
				paramMap.put("id", order.getAppId());
				Payv2BussCompanyApp pbca = Payv2BussCompanyAppService.queryDetailById(order.getAppId());
				paramMap.clear();
				String pay_amt = DecimalUtil.centsToYuan(map.get("amount").toString());
				if("1".equals(map.get("status"))){
					paramMap.put("result_code", PayFinalUtil.SUSSESS_CODE); // 成功
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", pay_amt);
				}else{
					paramMap.put("result_code", PayFinalUtil.CANCEL_CODE); // 失败
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", "0.00");
				}
				//商户不传或传空，回调不返回
				if(order.getRemark()!=null&&!"".equals(order.getRemark())){
					paramMap.put("remark", URLEncoder.encode(order.getRemark())); // 自定义字段
				}
				paramMap.put("pay_time", DateStr(order.getCreateTime()));
				paramMap.put("order_num", orderNum);
				paramMap.put("buss_order_num", order.getMerchantOrderNum());
				// 参数签名
				String sign = PaySignUtil.getSign(paramMap, pbca.getAppSecret());
				paramMap.put("sign", sign);
				
				String returnUrl = order.getReturnUrl();
				if(returnUrl!=null&&returnUrl.length()>5){
					if(returnUrl.startsWith("http:")
							||returnUrl.startsWith("https:")){
						returnUrl = "redirect:"+returnUrl;
					}else{
						returnUrl = "redirect://"+returnUrl;
					}
				}
				if(returnUrl!=null&&!"".equals(returnUrl)){
					returnUrl = returnUrl+"?"+PaySignUtil.getParamStr(paramMap)+"&sign="+sign;
					System.out.println(returnUrl);
					mv.setViewName(returnUrl);
				}
				if("1".equals(map.get("status"))){
					paramMap.put("payPrice", pay_amt);
				}else{
					paramMap.put("payPrice", order.getPayMoney());
				}
				paramMap.put("companyKey", pbca.getCompanyKey());
			}
			mv.addObject("map", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new ModelAndView("pay/return");
		}
		return mv;
	}
	
	private String DateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sdf.format(date);
		return str;
	}
	/**
	* @Title: wft 
	* @Description: 威富通：微信公众号同步通知URL
	* @param @param map
	* @param @param request
	* @param @return    设定文件 
	* @return ModelAndView    返回类型 
	* @throws
	*/
	@RequestMapping("/wft.do")
	public ModelAndView wft(@RequestParam Map<String, Object> map,HttpServletRequest req, HttpServletResponse resp){
		
		System.out.println(req.getHeader("referer"));
		/**
		 * 因：威富通不给返回任何东西··故没有什么用这个接口：暂保留
		 */
		ModelAndView mv = new ModelAndView("pay/return");
		map.put("result_code", PayFinalUtil.SUSSESS_CODE); // 成功
		mv.addObject("map", map);
		System.out.println(map);
		System.out.println("=========》威富通微信公众号支付结果回调开始");
		String resString = XmlUtils.parseRequst(req);
		System.out.println("========》回调通知内容：" + resString);
		return mv;
		
	}
	
	//现在支付回跳页面
	@RequestMapping("/nowReturn.do")
	public ModelAndView nowReturn(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		//String zf="{mhtCharset=UTF-8, appId=148999970238152, mhtOrderNo=1510040118958, signType=MD5, mhtReserved=, nowPayOrderNo=200601201711071535127042638, funcode=N002, signature=e1cd88f7658dd929cec5571f8b3801c9, transStatus=A004, version=1.0.0}";
		ModelAndView mv = new ModelAndView("pay/returnApp");
		try {
			Map<String,Object> paramMap = new HashMap<>();
			String orderNum = map.get("mhtOrderNo").toString();
			Payv2PayOrder order = payv2PayOrderService.getOrderInfo(orderNum);
			if(order!=null){
				paramMap.put("id", order.getAppId());
				Payv2BussCompanyApp pbca = Payv2BussCompanyAppService.queryDetailById(order.getAppId());
				paramMap.clear();
				if("A001".equals(map.get("transStatus"))){
					paramMap.put("result_code", PayFinalUtil.SUSSESS_CODE); // 成功
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", order.getPayMoney());
				}else{
					paramMap.put("result_code", PayFinalUtil.CANCEL_CODE); // 失败
					paramMap.put("pay_money", order.getPayMoney());
					paramMap.put("pay_discount_money", "0.00");
				}
				//商户不传或传空，回调不返回
				if(order.getRemark()!=null&&!"".equals(order.getRemark())){
					paramMap.put("remark", URLEncoder.encode(order.getRemark())); // 自定义字段
				}
				paramMap.put("pay_time", DateStr(order.getCreateTime()));
				paramMap.put("order_num", map.get("mhtOrderNo"));
				paramMap.put("buss_order_num", order.getMerchantOrderNum());
				// 参数签名
				String sign = PaySignUtil.getSign(paramMap, pbca.getAppSecret());
				paramMap.put("sign", sign);
				
				String returnUrl = order.getReturnUrl();
				if(returnUrl!=null&&returnUrl.length()>5){
					if(returnUrl.startsWith("http:")
							||returnUrl.startsWith("https:")){
						returnUrl = "redirect:"+returnUrl;
					}else{
						returnUrl = "redirect://"+returnUrl;
					}
				}
				if(returnUrl!=null&&!"".equals(returnUrl)){
					returnUrl = returnUrl+"?"+PaySignUtil.getParamStr(paramMap)+"&sign="+sign;
					mv.setViewName(returnUrl);
				}
				paramMap.put("payPrice", order.getPayMoney());
				paramMap.put("companyKey", pbca.getCompanyKey());
			}
			mv.addObject("map", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new ModelAndView("pay/return");
		}
		return mv;
	}
	
	//浦发回跳页面
	@RequestMapping("/pfReturn.do")
	public ModelAndView pfReturn(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		//String zf="{orderNum=DD2017556465165323251}";
		ModelAndView mv = new ModelAndView("pay/returnApp");
		try {
			Map<String,Object> paramMap = new HashMap<>();
			String orderNum = map.get("orderNum").toString();
			Payv2PayOrder order = payv2PayOrderService.getOrderInfo(orderNum);
			if(order!=null){
				paramMap.put("id", order.getAppId());
				Payv2BussCompanyApp pbca = Payv2BussCompanyAppService.queryDetailById(order.getAppId());
				paramMap.clear();
				paramMap.put("result_code", PayFinalUtil.UNKNOWN_CODE); // 成功
				paramMap.put("pay_money", order.getPayMoney());
				
				//商户不传或传空，回调不返回
				if(order.getRemark()!=null&&!"".equals(order.getRemark())){
					paramMap.put("remark", URLEncoder.encode(order.getRemark())); // 自定义字段
				}
				paramMap.put("pay_time", DateStr(order.getCreateTime()));
				paramMap.put("order_num", map.get("orderNum"));
				paramMap.put("buss_order_num", order.getMerchantOrderNum());
				// 参数签名
				String sign = PaySignUtil.getSign(paramMap, pbca.getAppSecret());
				paramMap.put("sign", sign);
				
				String returnUrl = order.getReturnUrl();
				if(returnUrl!=null&&returnUrl.length()>5){
					if(returnUrl.startsWith("http:")
							||returnUrl.startsWith("https:")){
						returnUrl = "redirect:"+returnUrl;
					}else{
						returnUrl = "redirect://"+returnUrl;
					}
				}
				if(returnUrl!=null&&!"".equals(returnUrl)){
					returnUrl = returnUrl+"?"+PaySignUtil.getParamStr(paramMap)+"&sign="+sign;
					mv.setViewName(returnUrl);
				}
				paramMap.put("payPrice", order.getPayMoney());
				paramMap.put("companyKey", pbca.getCompanyKey());
			}
			mv.addObject("map", paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			mv = new ModelAndView("pay/return");
		}
		return mv;
	}
}