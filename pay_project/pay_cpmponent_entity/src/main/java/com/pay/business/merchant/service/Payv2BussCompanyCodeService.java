package com.pay.business.merchant.service;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.pay.business.merchant.mapper.Payv2BussCompanyCodeMapper;
import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;

/**
 * @author cyl
 * @version 
 */
public interface Payv2BussCompanyCodeService extends BaseService<Payv2BussCompanyCode,Payv2BussCompanyCodeMapper>  {
	/**
	 * 检查商户是否支持此支付方式的通道
	 * @param companyId
	 * @param payPlatform
	 * @return
	 */
	public boolean checkComRate(Long companyId,Integer payPlatform);
	
	public Map<String, String> createOrder(Map<String, Object> map, Payv2BussCompany pbc
			, Integer payViewType,String dictName) ;
	
	/**
	 * 获取收款码分页列表
	 * 
	 * @return
	 */
	PageObject<Payv2BussCompanyCode> pageCode(Map<String,Object> map);
	/**
	 * 查看商户收款码
	 * 
	 * @param company 商户对象
	 * @param codeUrl	 
	 * @return 
	 * @throws Exception 
	 */
	Map<String, Object> getCode(Payv2BussCompany company) throws Exception;
	/**
	 * 下载商户收款码
	 * 
	 * @param company 商户对象
	 * @param codeUrl	 
	 * @return 
	 * @throws Exception 
	 */
	Map<String, Object> downloadCode(Payv2BussCompany company,HttpServletRequest request,HttpServletResponse response) throws Exception;
}
