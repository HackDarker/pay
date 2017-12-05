package com.pay.business.merchant.mapper;

import java.util.List;
import java.util.Map;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;

/**
 * @author cyl
 * @version 
 */
public interface Payv2BussSupportPayWayMapper extends BaseMapper<Payv2BussSupportPayWay>{

	public List<Payv2BussSupportPayWay> selectByObjectForCompany(Payv2BussSupportPayWay t);

	public List<Map<String, Object>> queryPayWayIdAndNameByCompanyId(
			Long companyId);
	
	/**
	 * 根据商户ID获取分组后的支付渠道
	 * @param t
	 * @return
	 */
	public List<Payv2BussSupportPayWay> selectPayWayIdByGroup(Payv2BussSupportPayWay t);
	
	/**
	 * 根据支付类型和商户查询商户支持支付通道
	 * @param map
	 * @return
	 */
	public List<Payv2BussSupportPayWay> queryByCompany(Map<String,Object> map);
	
	/**
	 * 
	 * 根据支付方式、支付类型、商户查询商户支持支付通道
	 * @param companyId
	 * @param payWayId
	 * @param payType
	 * @return List<Payv2BussSupportPayWay>
	 */
	public List<Payv2BussSupportPayWay> queryByCompany2(Map<String, Object> map);

	public Integer selectMaxSortNumByCompany(Map<String, Object> paramMap);
	/**
	 * 修改商户的支付费率
	 * 
	 * @param map
	 */
	void updatePayWayRate(Map<String, Object> map);
	
	/**
	 * 关闭通道-批量删除商户的通道
	 * @param ids
	 */
	void batchDelete(String ids);
}