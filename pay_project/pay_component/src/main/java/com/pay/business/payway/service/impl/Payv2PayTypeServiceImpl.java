package com.pay.business.payway.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.mapper.Payv2PayTypeMapper;
import com.pay.business.payway.service.Payv2PayTypeService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("payv2PayTypeService")
public class Payv2PayTypeServiceImpl extends BaseServiceImpl<Payv2PayType, Payv2PayTypeMapper> implements Payv2PayTypeService {
	// 注入当前dao对象
    @Autowired
    private Payv2PayTypeMapper payv2PayTypeMapper;

    public Payv2PayTypeServiceImpl() {
        setMapperClass(Payv2PayTypeMapper.class, Payv2PayType.class);
    }

	public List<Payv2PayType> payTypeList() {
		return payv2PayTypeMapper.payTypeList();
	}
    
 
}
