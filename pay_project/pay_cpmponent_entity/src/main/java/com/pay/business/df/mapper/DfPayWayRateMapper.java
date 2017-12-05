package com.pay.business.df.mapper;

import java.util.Map;

import com.pay.business.df.entity.DfPayWayRate;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface DfPayWayRateMapper extends BaseMapper<DfPayWayRate>{

	void updateDfWayRate(Map<String, Object> map);

	void batchUpdate(Map<String, Object> paramMap);

	void plasRateTotalAmount(Map<String, Object> map);

}