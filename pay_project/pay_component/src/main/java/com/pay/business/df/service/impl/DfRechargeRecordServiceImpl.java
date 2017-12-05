package com.pay.business.df.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.df.entity.DfRechargeRecord;
import com.pay.business.df.mapper.DfRechargeRecordMapper;
import com.pay.business.df.service.DfRechargeRecordService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("dfRechargeRecordService")
public class DfRechargeRecordServiceImpl extends BaseServiceImpl<DfRechargeRecord, DfRechargeRecordMapper> implements DfRechargeRecordService {
	// 注入当前dao对象
    @Autowired
    private DfRechargeRecordMapper dfRechargeRecordMapper;

    public DfRechargeRecordServiceImpl() {
        setMapperClass(DfRechargeRecordMapper.class, DfRechargeRecord.class);
    }
    
 
}
