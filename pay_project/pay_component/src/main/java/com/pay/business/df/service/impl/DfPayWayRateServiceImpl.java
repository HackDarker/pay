package com.pay.business.df.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.mapper.DfPayWayRateMapper;
import com.pay.business.df.service.DfPayWayRateService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("dfPayWayRateService")
public class DfPayWayRateServiceImpl extends BaseServiceImpl<DfPayWayRate, DfPayWayRateMapper> implements DfPayWayRateService {
	// 注入当前dao对象
    @Autowired
    private DfPayWayRateMapper dfPayWayRateMapper;

    public DfPayWayRateServiceImpl() {
        setMapperClass(DfPayWayRateMapper.class, DfPayWayRate.class);
    }

	public void updateDfWayRate(Map<String, Object> map) {
		dfPayWayRateMapper.updateDfWayRate(map);
	}

	public void batchUpdate(Map<String, Object> paramMap) {
		dfPayWayRateMapper.batchUpdate(paramMap);
	}

	public List<DfPayWayRate> selectByObject(DfPayWayRate rate) {
		return dfPayWayRateMapper.selectByObject(rate);
	}
    
 
}
