package com.pay.business.tx.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.tx.entity.TxPayOrder;

/**
 * @author cyl
 * @version 
 */
public interface TxPayOrderMapper extends BaseMapper<TxPayOrder>{
	List<Map<String, Object>> getEnchashmentRateByCom(@Param("id")String id);
	
	/**
	 * 获取提现中金额
	 * 
	 * @param map
	 * @return Double 提现中金额
	 */
	Double getIngAmount(Map<String, Object> map);
	/**
	 * 获取今日提现成功金额
	 * 
	 * @param map
	 * @return Double 提现成功金额
	 */
	Double getSucAmount(Map<String, Object> map);
	/**
	 * 获取今日提现手续费、到账金额
	 * 
	 * @param map
	 * @return TxPayOrder 提现订单对象
	 */
	TxPayOrder getNowAmount(Map<String, Object> map);
	/**
	 * 获取当前搜索条件下的提现订单总条目数
	 * 
	 * @param map
	 * @return int 总条目数
	 */
	int getCount2(Map<String, Object> map);
	/**
	 * 获取提现订单列表对象
	 * 
	 * @param map
	 * @return List<TxPayOrder> 提现订单列表对象
	 */
	List<TxPayOrder> getPageObject2(Map<String, Object> map);

	Map<String,Object> queryToday(Map<String, Object> map);

	int searchTxOrderListCount(Map<String, Object> map);

	List<TxPayOrder> searchTxOrderList(HashMap<String, Object> map);
	
	Map<String, Object> selectOrderKey(String orderNum);
}