package com.pay.business.merchant.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2CompanyPayTypeMapper;
import com.pay.business.merchant.service.Payv2CompanyPayTypeService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("payv2CompanyPayTypeService")
public class Payv2CompanyPayTypeServiceImpl extends BaseServiceImpl<Payv2CompanyPayType, Payv2CompanyPayTypeMapper> implements Payv2CompanyPayTypeService {
	// 注入当前dao对象
    @Autowired
    private Payv2CompanyPayTypeMapper payv2CompanyPayTypeMapper;

    public Payv2CompanyPayTypeServiceImpl() {
        setMapperClass(Payv2CompanyPayTypeMapper.class, Payv2CompanyPayType.class);
    }

	public List<Payv2CompanyPayType> payTypeList(String companyId) {
		return payv2CompanyPayTypeMapper.payTypeList(companyId);
	}

	public List<Payv2CompanyPayType> selectCompanyHasThisPayType(
			Map<String, Object> resultMap) {
		return payv2CompanyPayTypeMapper.selectCompanyHasThisPayType(resultMap);
	}

	public int checkRateIsLargeValue(Map<String, Object> map) {
		Integer countInteger = payv2CompanyPayTypeMapper.checkRateIsLargeValue(map);
		if(countInteger != null)
			return countInteger.intValue();
		return 0;
	}
    
 
}
