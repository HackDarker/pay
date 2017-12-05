package com.pay.business.df.mapper;

import java.util.List;
import java.util.Map;

import com.pay.business.df.entity.DfPayCompanyRate;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface DfPayCompanyRateMapper extends BaseMapper<DfPayCompanyRate>{

	void plusCompanyTotalAmount(Map<String, Object> map);
	
	List<DfPayCompanyRate> getRates(Map<String, Object> map);

	Double getRateBychannelId(Map<String,Object> map);
}