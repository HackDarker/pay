package com.pay.business.jk.service;

import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.entity.JkOrder;
import com.pay.business.jk.mapper.JkOrderMapper;

/**
 * @author cyl
 * @version 
 */
public interface JkOrderService extends BaseService<JkOrder,JkOrderMapper>  {
	/**
	 * 下单接口
	 * @param map
	 * @param jci
	 * @param ip
	 * @return
	 */
	public String urlCon(Map<String,Object> map,JkCompanyInfo jci,String ip);
}
