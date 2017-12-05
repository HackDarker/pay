package com.pay.business.tx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.tx.entity.TxPayRateBalance;
import com.pay.business.tx.mapper.TxPayRateBalanceMapper;
import com.pay.business.tx.service.TxPayRateBalanceService;
import com.core.teamwork.base.service.impl.BaseServiceImpl;

/**
 * @author cyl
 * @version 
 */
@Service("txPayRateBalanceService")
public class TxPayRateBalanceServiceImpl extends BaseServiceImpl<TxPayRateBalance, TxPayRateBalanceMapper> implements TxPayRateBalanceService {
	// 注入当前dao对象
    @Autowired
    private TxPayRateBalanceMapper txPayRateBalanceMapper;

    public TxPayRateBalanceServiceImpl() {
        setMapperClass(TxPayRateBalanceMapper.class, TxPayRateBalance.class);
    }
    
    public Double getTxBalance(Map<String, Object> map) {
		return txPayRateBalanceMapper.getTxBalance(map);
	}

	public List<TxPayRateBalance> getTxRateMoney(Map<String, Object> map) {
		return txPayRateBalanceMapper.getTxRateMoney(map);
	}

	public List<TxPayRateBalance> selectCompanyByTx(Map<String, Object> map) {
		return txPayRateBalanceMapper.selectCompanyByTx(map);
	}

	public TxPayRateBalance getCurDate(Map<String, Object> map) {
		return txPayRateBalanceMapper.getCurDate(map);
	}
 
}
