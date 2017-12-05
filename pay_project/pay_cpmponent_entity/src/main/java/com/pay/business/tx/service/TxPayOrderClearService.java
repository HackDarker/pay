package com.pay.business.tx.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.tx.entity.TxPayOrderClear;
import com.pay.business.tx.mapper.TxPayOrderClearMapper;

/**
 * @author cyl
 * @version 
 */
public interface TxPayOrderClearService extends BaseService<TxPayOrderClear,TxPayOrderClearMapper>  {
	//定时0点统计DO通道账单
	public void job(Date date);

	public PageObject<TxPayOrderClear> PagequeryForCompany(
			Map<String, Object> map);

	public PageObject<TxPayOrderClear> PagequeryForRate(Map<String, Object> map);
	/**
	 * 获取D0每天通道账单分页列表
	 * 
	 * @return PageObject<TxPayOrderClear> 分页对象
	 */
	PageObject<TxPayOrderClear> getPageObject(Map<String,Object> map);
	/**
	 * 获取支付通道维度的D0账单列表
	 * 
	 * @param map
	 * @return List<TxPayOrderClear> D0账单列表
	 */
	List<TxPayOrderClear> getRatesList(Map<String,Object> map);
}
