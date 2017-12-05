package com.pay.business.merchant.mapper;

import java.util.HashMap;
import java.util.List;

import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface Payv2BussCompanyCodeMapper extends BaseMapper<Payv2BussCompanyCode>{	
	/**
	 * 获取收款码分页列表
	 * 
	 * @param map
	 * @return
	 */
	List<Payv2BussCompanyCode> pageCode(HashMap<String,Object> map);
}