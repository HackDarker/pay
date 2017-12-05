package com.pay.business.df.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfRechargeRecord;
import com.pay.business.df.mapper.DfPayCompanyRateMapper;
import com.pay.business.df.mapper.DfPayWayRateMapper;
import com.pay.business.df.mapper.DfRechargeRecordMapper;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("dfPayCompanyRateService")
public class DfPayCompanyRateServiceImpl extends BaseServiceImpl<DfPayCompanyRate, DfPayCompanyRateMapper> implements DfPayCompanyRateService {
	// 注入当前dao对象
    @Autowired
    private DfPayCompanyRateMapper dfPayCompanyRateMapper;
    @Autowired
    private DfPayWayRateMapper dfPayWayRateMapper;
    @Autowired
    private DfRechargeRecordMapper dfRechargeRecordMapper;

    public DfPayCompanyRateServiceImpl() {
        setMapperClass(DfPayCompanyRateMapper.class, DfPayCompanyRate.class);
    }

	public void plusTotalAmount(Map<String, Object> map) {
		dfPayCompanyRateMapper.plusCompanyTotalAmount(map);
		dfPayWayRateMapper.plasRateTotalAmount(map);
		DfRechargeRecord record = new DfRechargeRecord();
		record.setCompanyId(Long.parseLong(map.get("companyId").toString()));
		record.setCreateTime(new Date());
		record.setCreateUserBy(Long.parseLong(map.get("adminId").toString()));
		record.setDfRateId(Long.parseLong(map.get("dfRateId").toString()));
		record.setRateMoney(Double.parseDouble(map.get("money").toString()));
		dfRechargeRecordMapper.insertByEntity(record);
	}

	public List<DfPayCompanyRate> getRates(Map<String, Object> map) {
		return dfPayCompanyRateMapper.getRates(map);
	}

	/**
	 * 根据渠道查询商户下最大代付费
	 */
	public Double getRateBychannelId(Map<String, Object> map) {
		return dfPayCompanyRateMapper.getRateBychannelId(map);
	}
    
 
}
