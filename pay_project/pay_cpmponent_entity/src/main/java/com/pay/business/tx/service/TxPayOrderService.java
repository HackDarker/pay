package com.pay.business.tx.service;

import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.tx.entity.TxPayOrder;
import com.pay.business.tx.mapper.TxPayOrderMapper;

/**
 * @author cyl
 * @version 
 */
public interface TxPayOrderService extends BaseService<TxPayOrder,TxPayOrderMapper>  {
	/**
	 * 根据商户id提现
	 * @param id
	 */
	public void setEnchashmentByCom(String id);
	
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
	 * 获取提现订单列表对象
	 * 
	 * @param map
	 * @return List<TxPayOrder> 提现订单列表对象
	 */
	PageObject<TxPayOrder> getPageObject(Map<String, Object> map);

	public Map<String,Object> queryToday(Map<String, Object> map);

	public PageObject<TxPayOrder> searchTxOrderList(Map<String, Object> map);
	
	Map<String, Object> selectOrderKey(String orderNum);
}
