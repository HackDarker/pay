package com.pay.business.order.service;

import com.pay.business.order.entity.Payv2PayOrderNotify;
import com.pay.business.order.mapper.Payv2PayOrderNotifyMapper;
import com.core.teamwork.base.service.BaseService;

/**
 * @author cyl
 * @version 
 */
public interface Payv2PayOrderNotifyService extends BaseService<Payv2PayOrderNotify,Payv2PayOrderNotifyMapper>  {
	public void insetNotify(String orderNum,String notifyUrl,String param,long time,String result);
}
