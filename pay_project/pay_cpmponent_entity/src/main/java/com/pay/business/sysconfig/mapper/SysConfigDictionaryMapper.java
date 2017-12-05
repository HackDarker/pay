package com.pay.business.sysconfig.mapper;

import java.util.List;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.sysconfig.entity.SysConfigDictionary;

/**
 * @author buyuer
 * @version 
 */
public interface SysConfigDictionaryMapper extends BaseMapper<SysConfigDictionary>{
	List<SysConfigDictionary> selectAll();
}