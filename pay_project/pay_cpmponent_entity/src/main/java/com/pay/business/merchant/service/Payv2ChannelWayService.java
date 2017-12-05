package com.pay.business.merchant.service;

import java.util.List;

import com.core.teamwork.base.service.BaseService;
import com.pay.business.merchant.entity.Payv2ChannelWay;
import com.pay.business.merchant.mapper.Payv2ChannelWayMapper;

/**
 * @author cyl
 * @version 
 */
public interface Payv2ChannelWayService extends BaseService<Payv2ChannelWay,Payv2ChannelWayMapper>  {
	/**
	 * 获取渠道商支持的支付平台与支付方式
	 * 
	 * @param channelId 渠道商ID
	 * @return  List<Payv2ChannelWay> 渠道商支持的支付方式列表
	 */
	List<Payv2ChannelWay> payTypeList(String channelId);
}
