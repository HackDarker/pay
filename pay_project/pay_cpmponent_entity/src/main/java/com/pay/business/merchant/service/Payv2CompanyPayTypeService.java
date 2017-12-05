package com.pay.business.merchant.service;

import java.util.List;
import java.util.Map;

import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2CompanyPayTypeMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface Payv2CompanyPayTypeService extends BaseService<Payv2CompanyPayType,Payv2CompanyPayTypeMapper>  {
	/**
	 * 获取商户支持的支付平台与支付方式
	 * 
	 * @param companyId 商户ID
	 * @return  List<Payv2ChannelWay> 商户支持的支付方式列表
	 */
	List<Payv2CompanyPayType> payTypeList(String companyId);

	List<Payv2CompanyPayType> selectCompanyHasThisPayType(
			Map<String, Object> resultMap);

	int checkRateIsLargeValue(Map<String, Object> map);
}
