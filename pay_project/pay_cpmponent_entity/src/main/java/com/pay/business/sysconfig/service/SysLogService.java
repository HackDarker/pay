package com.pay.business.sysconfig.service;

import java.util.Map;

import com.core.teamwork.base.service.BaseService;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.sysconfig.entity.SysLog;
import com.pay.business.sysconfig.mapper.SysLogMapper;
import com.pay.business.util.LogTypeEunm;

/**
 * @author cyl
 * @version 
 */
public interface SysLogService extends BaseService<SysLog,SysLogMapper>  {
	/**
	 * 添加操作日志
	 * @param sysType		系统类型1官方后台2.渠道商后台3.商户后台
	 * @param eunm			操作类型
	 * @param ip			ip
	 * @param executor		操作者userId
	 * @param param			请求参数
	 */
	public void addSysLog(Integer sysType,LogTypeEunm eunm,String ip,Long executor,Map<String,Object> param);
	PageObject<SysLog> getSysLogPageList(Map<String, Object> map);
	void addSysLogByString(Integer sysType,LogTypeEunm eunm,String ip,Long executor,Map<String, String> param);
}
