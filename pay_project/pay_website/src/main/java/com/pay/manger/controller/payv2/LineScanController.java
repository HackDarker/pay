package com.pay.manger.controller.payv2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.properties.PropertiesUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.payv2.service.PaymentService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PayFinalUtil;

@Controller
@RequestMapping("/lineScan/*")
public class LineScanController {
	private static final Log logger = LogFactory.getLog(Payv2PayController.class);
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	@Autowired
	private PaymentService paymentService;
	
	public final static String[] agent = { "Android", "iPhone", "iPod", "iPad",
		"Windows Phone", "MQQBrowser" };
	
	@ResponseBody
	@RequestMapping(value = "/toCompany")
	public ModelAndView toCompany(HttpServletRequest request,HttpServletResponse response
			,@RequestParam Map<String, Object> map) throws Exception{
		ModelAndView mv = new ModelAndView();
		String redirect = PropertiesUtil.getProperty("rate", "redirect_url")+"?resultCode=";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			boolean isNotNull = ObjectUtil.checkObject(new String[] {"companyKey"}, map);
			if (!isNotNull){
				logger.debug("参数不能为空,或者有误");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.PARAM_ERROR_MONEY);
			}
			Payv2BussCompany pbc = payv2BussCompanyService.queryCodeByKey(map.get("companyKey").toString());
			if(pbc==null){
				logger.debug("商户号无效");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.COMPANY_KEY_INVALID);
			}
			if(!StringUtils.isNoneBlank(pbc.getCodeUrl())){
				logger.debug("收款码无效");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.COMPANY_CODE_ERROR);
			}
			
			boolean con = payv2BussCompanyCodeService.checkComRate(pbc.getId()
					, getPayType(request));
			if(!con){
				logger.debug("商户不支持此支付方式");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.RATE_TYPE_ERROR);
			}
			mv = new ModelAndView("pay/scan");
			mv.addObject("obj", pbc);
			return mv;
			
		} catch (Exception e) {
			e.printStackTrace();
			return errorReturn(mv, resultMap, redirect, ParameterEunm.ERROR_500_CODE);
		}
	}
	
	/**
	 * 线下扫码支付
	 */
	@RequestMapping("/toPay")
	public ModelAndView toPay(@RequestParam Map<String, Object> map, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		String redirect = PropertiesUtil.getProperty("rate", "redirect_url")+"?resultCode=";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isNotNull = ObjectUtil.checkObject(new String[] { "payMoney","companyKey"}, map);
		if (!isNotNull){
			logger.debug("参数不能为空,或者有误");
			return errorReturn(mv, resultMap, redirect, ParameterEunm.PARAM_ERROR_MONEY);
		}
		Payv2BussCompany pbc = payv2BussCompanyService.queryCodeByKey(map.get("companyKey").toString());
		if(pbc==null){
			logger.debug("商户号无效");
			return errorReturn(mv, resultMap, redirect, ParameterEunm.COMPANY_KEY_INVALID);
		}
		if(!StringUtils.isNoneBlank(pbc.getCodeUrl())){
			logger.debug("收款码无效");
			return errorReturn(mv, resultMap, redirect, ParameterEunm.COMPANY_CODE_ERROR);
		}
		if(pbc.getIsRemark()==1){
			boolean isRemark = ObjectUtil.checkObject(new String[] { "remark" }, map);
			if(!isRemark){
				logger.debug("商户设置备注信息必填");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.COMPANY_REMARK_ERROR);
			}
		}
		try {
			if (isNotNull) {
				if(DecimalUtil.isBigDecimal(map.get("payMoney").toString())
						&&DecimalUtil.isZero(map.get("payMoney").toString())){
					Integer payPlatform = getPayType(request);
					map.put("address", IpAddressUtil.getIpAddress(request));
					map.put("method", "线下扫码接口");
					map.put("userAgent", request.getHeader("User-Agent"));
					String dictName = "SCAN";
					if(payPlatform==2){
						dictName = "GZH";
					}
					map.put("payPlatform", payPlatform);
					Map<String, String> m = paymentService.payment(map, pbc, dictName);
					
					if(m.get("status").equals(PayFinalUtil.PAY_STATUS_FAIL)){
						//已超过限额,请检查微信通道单笔额度和每日额度
						return errorReturn(mv, resultMap, redirect, ParameterEunm.RATE_ORDER_ERROR);
					}else if(PayFinalUtil.PAY_TYPE_FAIL.equals(m.get("status"))){
						//未配置支付通道或支付类型不支持
						return errorReturn(mv, resultMap, redirect, ParameterEunm.RATE_TYPE_ERROR);
					}else if(PayFinalUtil.RATE_FAIL.equals(m.get("status"))){
						//支付通道下单出错
						return errorReturn(mv, resultMap, redirect, ParameterEunm.RATE_FAIL);
					}else{
						mv = new ModelAndView("pay/transfer");
						if(m.containsKey("alipayStr")){
							mv.addObject("alipayStr", m.get("alipayStr"));
							return mv;
						}
						if(m.containsKey("webStr")){
							mv.addObject("webStr", m.get("webStr"));
						}
						m.remove("status");
						resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, m);
						resultMap.remove("message");
						return mv;
					}
				}else{
					logger.debug("支付金额错误");
					return errorReturn(mv, resultMap, redirect, ParameterEunm.MOENY_ERROR);
				}
			}else {
				logger.debug("参数不能为空,或者有误");
				return errorReturn(mv, resultMap, redirect, ParameterEunm.PARAM_ERROR_MONEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return errorReturn(mv, resultMap, redirect, ParameterEunm.ERROR_500_CODE);
		}
	}
	
	private ModelAndView errorReturn(ModelAndView mv,Map<String,Object> resultMap
			,String redirect,String error){
		resultMap = ReMessage.resultBack(error,null);
		redirect=redirect+resultMap.get("resultCode")+"&message="+resultMap.get("message").toString();
		mv.setViewName(redirect);
		return mv;
	}
	
	public static boolean checkAgentIsMobile(String ua) {
		boolean flag = false;
		if(ua==null){
			return false;
		}
		if (!ua.contains("Windows NT")
				|| (ua.contains("Windows NT") && ua
						.contains("compatible; MSIE 9.0;"))) {
			// 排除 苹果桌面系统
			if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
				for (String item : agent) {
					if (ua.toLowerCase().contains(item.toLowerCase())) {
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	}
	
	private static Integer getPayType(HttpServletRequest request){
		String ua = request.getHeader("user-agent").toLowerCase();
		System.out.println("浏览器标识:"+ua);
		Integer payPlatform=1;
		if(checkAgentIsMobile(ua)) {
			if (ua.indexOf("micromessenger") > 0) {// 是微信浏览器
				System.out.println("微信浏览器");
				payPlatform=2;
			} 
			else if(ua.indexOf("alipay") > 0){
				System.out.println("支付宝浏览器");
				payPlatform=1;
			}
			else if(ua.indexOf("qq") > 0){
				System.out.println("QQ浏览器");
				payPlatform=3;
			}
			else {
				System.out.println("其他浏览器");
			}
		} else {
			System.out.println("电脑");
		}
		return payPlatform;
	}
}
