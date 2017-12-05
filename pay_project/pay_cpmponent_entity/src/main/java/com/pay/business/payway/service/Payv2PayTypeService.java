package com.pay.business.payway.service;

import java.util.List;

import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.mapper.Payv2PayTypeMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface Payv2PayTypeService extends BaseService<Payv2PayType,Payv2PayTypeMapper>  {	
	/**
	 * 获取所有的支付方式
	 * 
	 * @return List<Payv2PayType>支付方式列表
	 */
	List<Payv2PayType> payTypeList();
}
