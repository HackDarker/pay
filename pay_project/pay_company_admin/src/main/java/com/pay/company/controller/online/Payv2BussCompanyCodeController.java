package com.pay.company.controller.online;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2BussCompanyCodeMapper;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2CompanyPayTypeService;
import com.pay.business.util.ParameterEunm;
import com.pay.company.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/bussComapnyCode/*")
public class Payv2BussCompanyCodeController extends BaseManagerController<Payv2BussCompanyCode, Payv2BussCompanyCodeMapper> {

	private static final Logger logger = Logger.getLogger(Payv2BussCompanyCodeController.class);
	@Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	@Autowired
	private Payv2CompanyPayTypeService payv2CompanyPayTypeService;
	
	/**
	 * 获取商户收款码信息
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("codeInfo")
	public Map<String,Object> codeInfo(){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			// 获取商户收款码详情
			Map<String,Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", getAdmin().getId());
			Payv2BussCompanyCode payv2BussCompanyCode = payv2BussCompanyCodeService.detail(paramMap);
			List<Payv2CompanyPayType> payTypeList = payv2CompanyPayTypeService.query(paramMap);
	        if(payTypeList.size() == 0){
	        	resultMap.put("isHasCode", 0);
	        }else{
	        	resultMap.put("isHasCode", 1);
	        }
			resultMap.put("payv2BussCompanyCode", payv2BussCompanyCode);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, resultMap);
		}
		return resultMap;
	}	
	/**
	 * 修改商户收款码信息
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("codeInfoEdit")
	public Map<String,Object> codeInfoEdit(@RequestParam Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			payv2BussCompanyCodeService.update(map);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, resultMap);
		}
		return resultMap;
	}
	/**
	 * 查看商户收款码
	 * 
	 * @param codeUrl
	 */
	@ResponseBody
	@RequestMapping("getCode")
	public Map<String,Object> getCode(){
		Map<String,Object> resultMap = new HashMap<String,Object>();			
		 Payv2BussCompany company = getAdmin();
		try {
			resultMap = payv2BussCompanyCodeService.getCode(company);
			if(resultMap.containsKey("codeFileUrl")){
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
			}else{
				resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, resultMap,resultMap.get("message").toString());
			}
        }catch (Exception e) {
			logger.error("生成商户收款码错误");
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null);
		}
		return resultMap;
	}
	/**
	 * 下载商户收款码
	 * 
	 * @param codeUrl
	 */
	@ResponseBody
	@RequestMapping("downloadCode")
	public Map<String,Object> downloadCode(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> resultMap = new HashMap<String,Object>();			
		Payv2BussCompany company = getAdmin();
		try {
			resultMap = payv2BussCompanyCodeService.downloadCode(company, request, response);			
			if(resultMap.containsKey("message")){
				resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, resultMap,resultMap.get("message").toString());				
			}else{
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
			}
        }catch (Exception e) {
			logger.error("下载商户收款码错误");
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null);
		}
		return resultMap;
	}
}
