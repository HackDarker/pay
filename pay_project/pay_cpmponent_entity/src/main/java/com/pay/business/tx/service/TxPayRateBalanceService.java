package com.pay.business.tx.service;

import java.util.List;
import java.util.Map;

import com.pay.business.tx.entity.TxPayRateBalance;
import com.pay.business.tx.mapper.TxPayRateBalanceMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface TxPayRateBalanceService extends BaseService<TxPayRateBalance,TxPayRateBalanceMapper>  {
	/**
	 * 查询可提现金额
	 * 
	 * @param map
	 * @return Double 可提现金额
	 */
	Double getTxBalance(Map<String, Object> map);
	/**
	 * 提现金额详情（商户在每个渠道下的提现金额）
	 * 
	 * @return List<TxPayRateBalance> 提现余额列表对象
	 */
	List<TxPayRateBalance> getTxRateMoney(Map<String, Object> map);
	List<TxPayRateBalance> selectCompanyByTx(Map<String, Object> map);
	/**
	 * 获取可提现交易金额，获取今日交易金额，今日转T1交易金额 三个关键数据
	 * 
	 * @param map
	 * @return TxPayRateBalance 提现余额对象
	 */
	TxPayRateBalance getCurDate(Map<String, Object> map);
}
