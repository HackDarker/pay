package com.pay.business.df.service;

import java.util.List;
import java.util.Map;

import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.mapper.DfPayCompanyRateMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface DfPayCompanyRateService extends BaseService<DfPayCompanyRate,DfPayCompanyRateMapper>  {

	void plusTotalAmount(Map<String, Object> map);
	/**
	 * 查询代付通道信息
	 * 
	 * @param map
	 * @return List<DfPayCompanyRate> 代付通道信息列表
	 */
	List<DfPayCompanyRate> getRates(Map<String, Object> map);
	
	/**
	 * 根据渠道查询商户下最大代付费
	 * @param map
	 * @return
	 */
	Double getRateBychannelId(Map<String,Object> map);
}
