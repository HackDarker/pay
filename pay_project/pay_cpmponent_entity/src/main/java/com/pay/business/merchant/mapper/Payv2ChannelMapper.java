package com.pay.business.merchant.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.merchant.entity.Payv2Channel;

/**
 * @author cyl
 * @version 
 */
public interface Payv2ChannelMapper extends BaseMapper<Payv2Channel>{

	/**
	 * 根据搜索条件获取总数
	 * 
	 * @param map
	 * @return int 总数
	 */
	int getCount2(Map<String,Object> map);
	/**
	 * 渠道商分页列表
	 * 
	 * @param map
	 * @return List<Payv2Channel>当前页渠道商列表
	 */
	List<Payv2Channel> pageQueryByObject2(HashMap<String,Object> map);
}