package com.pay.business.jk.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.core.teamwork.base.mapper.BaseMapper;
import com.pay.business.jk.entity.JkCompanyInfo;

/**
 * @author cyl
 * @version 
 */
public interface JkCompanyInfoMapper extends BaseMapper<JkCompanyInfo>{
	JkCompanyInfo companyCheck(Map<String,Object> map);
	
	void updateCallInfo(JkCompanyInfo jci);
	
	Integer getCount2(Map<String,Object> map);
	
	List<JkCompanyInfo> JkInfoPage(HashMap<String,Object> map);
	
	/**
	 * 借口商户后台获取账户余额关键数据与接口信息
	 * 
	 * @param companyId
	 * @return JkCompanyInfo 借口信息对象
	 */
	JkCompanyInfo cruxBalace(String companyId);
}