package com.pay.business.df.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pay.business.df.entity.DfPayOrder;
import com.core.teamwork.base.mapper.BaseMapper;

/**
 * @author cyl
 * @version 
 */
public interface DfPayOrderMapper extends BaseMapper<DfPayOrder>{

	int getCount2(Map<String, Object> map);

	List<DfPayOrder> pageQueryByObject2(HashMap<String, Object> map);

	DfPayOrder selectOrderDetail(Map<String, Object> map);

}