package com.pay.business.df.service;

import java.text.ParseException;
import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.mapper.DfPayOrderMapper;

/**
 * @author cyl
 * @version 
 */
public interface DfPayOrderService extends BaseService<DfPayOrder,DfPayOrderMapper>  {

	PageObject<DfPayOrder> getPageObject(Map<String, Object> map);

	DfPayOrder selectOrderDetail(Map<String, Object> map);
	
	public Map<String, Object> dfCreateOrder(Map<String, Object> map, Map<String, Object> param) throws InterruptedException, ParseException;
	/**
	 * 单笔代付查询（提供给商户）
	 * @param map
	 * @return
	 */
	Map<String,Object> singleQuery(Map<String,Object> map,Map<String, Object> pbc) throws Exception;
}
