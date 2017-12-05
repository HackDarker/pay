package com.pay.business.jk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.jk.entity.JkCompanyRecharge;
import com.pay.business.jk.mapper.JkCompanyRechargeMapper;
import com.pay.business.jk.service.JkCompanyRechargeService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("jkCompanyRechargeService")
public class JkCompanyRechargeServiceImpl extends BaseServiceImpl<JkCompanyRecharge, JkCompanyRechargeMapper> implements JkCompanyRechargeService {
	// 注入当前dao对象
    @Autowired
    private JkCompanyRechargeMapper jkCompanyRechargeMapper;

    public JkCompanyRechargeServiceImpl() {
        setMapperClass(JkCompanyRechargeMapper.class, JkCompanyRecharge.class);
    }
    
 
}
