package com.pay.manger.controller.payv2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.service.JkCompanyInfoService;
import com.pay.business.jk.service.JkOrderService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.StringHandleUtil;

@Controller
@RequestMapping("/jkInfo/*")
public class JkOrderController {
	private static final Log logger = LogFactory.getLog(Payv2PayController.class);
	
	@Autowired
	private JkCompanyInfoService jkCompanyInfoService;
	@Autowired
	private JkOrderService jkOrderService;
	
	@ResponseBody
	@RequestMapping(value = "/urlCon")
	public Map<String,Object> urlCon(HttpServletRequest request,HttpServletResponse response
			,@RequestParam Map<String, Object> map) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean paramStrCon = ObjectUtil.checkObject(new String[] {"paramStr"}, map);
		if(!paramStrCon){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			return resultMap;
		}
		Map<String, Object> paramMap = StringHandleUtil.toMap(map.get("paramStr").toString());
		if(paramMap.keySet().size()==0){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			return resultMap;
		}
		
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"mchNum","url","sign"}, paramMap);
		if (!isNotNull){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			return resultMap;
		}
		JkCompanyInfo jci = jkCompanyInfoService.companyCheck(paramMap.get("mchNum").toString());
		if(jci==null){
			logger.debug("商户号无效");
			resultMap = ReMessage.resultBack(ParameterEunm.COMPANY_KEY_INVALID,null);
			return resultMap;
		}
		if(jci.getBalance().doubleValue()<jci.getServiceMoney().doubleValue()){
			logger.debug("余额不足");
			resultMap = ReMessage.resultBack(ParameterEunm.BALANCE_ERROR,null);
			return resultMap;
		}
		try {
			//验签
			boolean con = PaySignUtil.checkSign(paramMap, jci.getCompanySecret());
			if(!con){
				logger.debug("商户签名错误");
				resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
				return resultMap;
			}
			String ip=IpAddressUtil.getIpAddress(request);
			String url = jkOrderService.urlCon(paramMap, jci, ip);
			resultMap.put("url", url);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
		}
		return resultMap;
	}
}
