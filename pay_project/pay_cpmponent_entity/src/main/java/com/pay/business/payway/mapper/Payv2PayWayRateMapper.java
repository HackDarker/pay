package com.pay.business.payway.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.entity.Payv2PayWayRateVO;

/**
 * @author cyl
 * @version 
 */
public interface Payv2PayWayRateMapper extends BaseMapper<Payv2PayWayRate>{
	
	/**
	 * 根据类型查询支付通道
	 * @param t
	 * @return
	 */
	public Payv2PayWayRate getRateByType(Payv2PayWayRate t);
	
	/**
	 * 提现用 
	 */
	public Payv2PayWayRate getEnchashment(String id);
	
	/**
	 * 根据商户和支付方式查询通道
	 * @param map
	 * @return
	 */
	public List<Payv2PayWayRate> queryByCompany(Map<String,Object> map);
	
	/**
	 * 根据id查询支付通道
	 * @param id
	 * @return
	 */
	public Payv2PayWayRate queryByid(Long id);
	
	/**
	 * 根据渠道商id和支付方式id查询通道
	 * @param map
	 * @return
	 */
	public List<Payv2PayWayRate> queryByChannelWayId(Map<String,Object> map);
	
	/**
	 * 根据支付方式查询通道
	 * @return
	 */
	public List<Payv2PayWayRate> queryByDicRate();
	
	/**
	 * 根据companyID获取其支持的支付渠道
	 * 
	 * @return List<Payv2PayWayRate> 返回类型
	 */
	List<Payv2PayWayRate> getPayWaysByCom(Map<String,Object> map);

	public void batchUpdate(Map<String, Object> paramMap);
	
	/**
	 * 通道导出
	 */
	List<Payv2PayWayRateVO> getExport(Map<String, Object> paramMap);

	public void updatePayWayRate(Map<String, Object> map);
	
	/**
	 * 获取可提现通道
	 */
	@Select("select rate.rate_key5 as key1, rate.rate_key6 as key2, sys.dict_name as name from payv2_pay_way_rate rate" 
			+ " left join sys_config_dictionary sys on rate.dic_id=sys.id"
			+ " where rate.rate_key5!='' and rate.rate_key6!='' and sys.dict_name like CONCAT('%','_MSXM','%')")
	List<Map<String, String>> getAutoEnchashmentRate();

	
	/**
	 * 获取D0通道
	 * 
	 * @param map
	 * @return List<Payv2PayWayRate> D0通道列表
	 */
	List<Payv2PayWayRate> getD0Rates(Map<String, Object> map);
	
	@Select("select max(id) from payv2_pay_way_rate")
	Integer getNextId();
}