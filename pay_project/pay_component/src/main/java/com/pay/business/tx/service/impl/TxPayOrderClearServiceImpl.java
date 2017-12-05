package com.pay.business.tx.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.tx.entity.TxPayOrderClear;
import com.pay.business.tx.mapper.TxPayOrderClearMapper;
import com.pay.business.tx.mapper.TxPayOrderMapper;
import com.pay.business.tx.mapper.TxPayRateBalanceMapper;
import com.pay.business.tx.service.TxPayOrderClearService;

/**
 * @author cyl
 * @version 
 */
@Service("txPayOrderClearService")
public class TxPayOrderClearServiceImpl extends BaseServiceImpl<TxPayOrderClear, TxPayOrderClearMapper> implements TxPayOrderClearService {
	// 注入当前dao对象
    @Autowired
    private TxPayOrderClearMapper txPayOrderClearMapper;
    @Autowired
    private TxPayOrderMapper txPayOrderMapper;
    @Autowired
    private TxPayRateBalanceMapper txPayRateBalanceMapper;

    public TxPayOrderClearServiceImpl() {
        setMapperClass(TxPayOrderClearMapper.class, TxPayOrderClear.class);
    }

	public void job(Date date) {
		Map<String,Object> param = new HashMap<>();
		param.put("txTime", DateUtil.DateToString(date,"yyyy-MM-dd"));
		//List<TxPayOrder> orderSumList= txPayOrderMapper.queryOrderSum(param);
		
		txPayOrderClearMapper.insertClear(param);
		
		/*for (TxPayOrder txPayOrder : orderSumList) {
			TxPayOrderClear tc = new TxPayOrderClear();
			
			tc.setCompanyId(txPayOrder.getCompanyId());
			//提现交易额+22-24点T1金额+未提现金额
			tc.setBalance(txPayOrder.getAmount()+txPayOrder.getTxBalance()+txPayOrder.getT1Balance());
			//T1金额=22-24点+未提现金额
			tc.setT1Balance(txPayOrder.getT1Balance()+txPayOrder.getTxBalance());
			//已提现交易额
			tc.setTxBalance(txPayOrder.getAmount());
			//提现手续费
			tc.setTxServiceAmount(txPayOrder.getServiceAmount());
			//交易手续费
			tc.setPayServiceAmount(txPayOrder.getPayServiceAmount());
			//提现次数
			tc.setTxCount(txPayOrder.getTxCount());
			//到账金额
			tc.setArrivalAmount(txPayOrder.getArrivalAmount());
			//账单时间
			tc.setClearTime(date);
			//创建时间
			tc.setCreateTime(new Date());
			txPayOrderClearMapper.insertByEntity(tc);
		}*/
		
		//所有数据清零
		txPayRateBalanceMapper.updateClearZero();
		
	}

	public PageObject<TxPayOrderClear> PagequeryForCompany(
			Map<String, Object> map) {
		int totalData = txPayOrderClearMapper.getCompanyClearListCount(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<TxPayOrderClear> orderList = txPayOrderClearMapper.getCompanyClearList(helper.getMap());
		PageObject<TxPayOrderClear> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
		
	}
	public PageObject<TxPayOrderClear> PagequeryForRate(Map<String, Object> map) {
		int totalData = txPayOrderClearMapper.getRateClearListCount(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<TxPayOrderClear> orderList = txPayOrderClearMapper.getRateClearList(helper.getMap());
		PageObject<TxPayOrderClear> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
	}
	public PageObject<TxPayOrderClear> getPageObject(Map<String,Object> map) {
		int totalData = txPayOrderClearMapper.getCount2(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<TxPayOrderClear> orderList = txPayOrderClearMapper.pageQueryByObject2(helper.getMap());
		PageObject<TxPayOrderClear> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
	}

	public List<TxPayOrderClear> getRatesList(Map<String, Object> map) {
		return txPayOrderClearMapper.getRatesList(map);
	}
 
}
