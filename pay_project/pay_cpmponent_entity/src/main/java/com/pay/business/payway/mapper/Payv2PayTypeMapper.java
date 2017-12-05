package com.pay.business.payway.mapper;

import java.util.List;

import com.pay.business.payway.entity.Payv2PayType;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface Payv2PayTypeMapper extends BaseMapper<Payv2PayType>{
	/**
	 * 获取所有的支付方式
	 * 
	 * @return List<Payv2PayType>支付方式列表
	 */
	List<Payv2PayType> payTypeList();
}