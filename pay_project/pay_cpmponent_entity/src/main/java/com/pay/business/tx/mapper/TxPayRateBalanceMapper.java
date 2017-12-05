package com.pay.business.tx.mapper;

import java.util.List;
import java.util.Map;

import com.pay.business.tx.entity.TxPayRateBalance;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface TxPayRateBalanceMapper extends BaseMapper<TxPayRateBalance>{
	//如果不存在新增，存在更新
	public void insertOrUpdate(TxPayRateBalance txPayRateBalance);	
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
	public List<TxPayRateBalance> selectCompanyByTx(Map<String, Object> map);
	
	void updateClearZero();
	/**
	 * 获取可提现交易金额，获取今日交易金额，今日转T1交易金额 三个关键数据
	 * 
	 * @param map
	 * @return TxPayRateBalance 提现余额对象
	 */
	TxPayRateBalance getCurDate(Map<String, Object> map);


}