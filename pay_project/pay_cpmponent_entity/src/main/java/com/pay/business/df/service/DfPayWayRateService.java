package com.pay.business.df.service;

import java.util.List;
import java.util.Map;

import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.mapper.DfPayWayRateMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface DfPayWayRateService extends BaseService<DfPayWayRate,DfPayWayRateMapper>  {

	void updateDfWayRate(Map<String, Object> map);

	void batchUpdate(Map<String, Object> paramMap);

	List<DfPayWayRate> selectByObject(DfPayWayRate rate);

}
