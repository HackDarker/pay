package com.pay.business.merchant.mapper;

import java.util.List;
import java.util.Map;

import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface Payv2CompanyPayTypeMapper extends BaseMapper<Payv2CompanyPayType>{
	/**
	 * 获取商户支持的支付平台与支付方式
	 * 
	 * @param channelId 渠道商ID
	 * @return  List<Payv2ChannelWay> 商户支持的支付方式列表
	 */
	List<Payv2CompanyPayType> payTypeList(String companyId);

	List<Payv2CompanyPayType> selectCompanyHasThisPayType(
			Map<String, Object> resultMap);

	Integer checkRateIsLargeValue(Map<String, Object> map);
}