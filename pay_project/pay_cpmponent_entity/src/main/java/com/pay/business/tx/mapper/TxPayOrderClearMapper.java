package com.pay.business.tx.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.tx.entity.TxPayOrderClear;

/**
 * @author cyl
 * @version 
 */
public interface TxPayOrderClearMapper extends BaseMapper<TxPayOrderClear>{
	void insertClear(Map<String,Object> map);

	int getCompanyClearListCount(Map<String, Object> map);

	List<TxPayOrderClear> getCompanyClearList(HashMap<String, Object> map);

	int getRateClearListCount(Map<String, Object> map);

	List<TxPayOrderClear> getRateClearList(HashMap<String, Object> map);
	/**
	 * 获取总数
	 * 
	 * @param map
	 * @return int 总数
	 */
	int getCount2(Map<String,Object> map);
	/**
	 * 获取D0每天通道账单分页列表
	 * 
	 * @return List<TxPayOrderClear> 分页对象
	 */
	List<TxPayOrderClear> pageQueryByObject2(Map<String, Object> map);
	/**
	 * 获取支付通道维度的D0账单列表
	 * 
	 * @param map
	 * @return List<TxPayOrderClear> D0账单列表
	 */
	List<TxPayOrderClear> getRatesList(Map<String,Object> map);
}