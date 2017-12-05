package com.pay.business.merchant.mapper;

import java.util.List;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.merchant.entity.Payv2ChannelWay;

/**
 * @author cyl
 * @version 
 */
public interface Payv2ChannelWayMapper extends BaseMapper<Payv2ChannelWay>{
	/**
	 * 获取渠道商支持的支付平台与支付方式
	 * 
	 * @param channelId 渠道商ID
	 * @return  List<Payv2ChannelWay> 渠道商支持的支付方式列表
	 */
	List<Payv2ChannelWay> payTypeList(String channelId);
}