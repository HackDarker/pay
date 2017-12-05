package com.pay.business.jk.service;

import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.mapper.JkCompanyInfoMapper;

/**
 * @author cyl
 * @version 
 */
public interface JkCompanyInfoService extends BaseService<JkCompanyInfo,JkCompanyInfoMapper>  {
	/**
	 * 检查商户和余额是否可用
	 * @param map
	 * @return
	 */
	public JkCompanyInfo companyCheck(String mchNum);
	/**
	 * 商户接口信息分页列表
	 * 
	 * @param map
	 * @return PageObject<JkCompanyInfo>
	 */
	PageObject<JkCompanyInfo> getPageObject(Map<String, Object> map);
	/**
	 * 借口商户后台获取账户余额关键数据与接口信息
	 * 
	 * @param companyId
	 * @return JkCompanyInfo 借口信息对象
	 */
	JkCompanyInfo cruxBalace(String companyId);
}
