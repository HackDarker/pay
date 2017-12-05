package com.pay.business.sysconfig.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.sysconfig.entity.SysLog;
import com.pay.business.sysconfig.mapper.SysLogMapper;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.LogTypeEunm;

/**
 * @author cyl
 * @version 
 */
@Service("sysLogService")
public class SysLogServiceImpl extends BaseServiceImpl<SysLog, SysLogMapper> implements SysLogService {
	// 注入当前dao对象
    @Autowired
    private SysLogMapper sysLogMapper;

    public SysLogServiceImpl() {
        setMapperClass(SysLogMapper.class, SysLog.class);
    }

	public PageObject<SysLog> getSysLogPageList(Map<String, Object> map) {
		int totalData = sysLogMapper.getSysLogPageListCount(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<SysLog> list = sysLogMapper.getSysLogPageList(helper.getMap());
		PageObject<SysLog> pageObject = helper.getPageObject();
		pageObject.setDataList(list);
		return pageObject;
	}
    
    /**
	 * 添加操作日志
	 * @param sysType		系统类型1官方后台2.渠道商后台3.商户后台
	 * @param eunm			操作类型
	 * @param ip			ip
	 * @param executor		操作者userId
	 * @param param			请求参数
	 */
	public void addSysLog(Integer sysType,LogTypeEunm eunm,String ip,Long executor,Map<String,Object> param){
		SysLog sysLog = new SysLog(); 
		sysLog.setSysType(sysType);
		sysLog.setExecuteName(eunm.getTypeName());
		sysLog.setExecuteType(eunm.getTypeNum());
		sysLog.setExecutor(executor);
		sysLog.setParam(param.toString());
		sysLog.setCreateTime(new Date());
		sysLog.setIp(ip);
        sysLogMapper.insertByEntity(sysLog);
	}

	public void addSysLogByString(Integer sysType, LogTypeEunm eunm, String ip, Long executor, Map<String, String> param) {
		SysLog sysLog = new SysLog(); 
		sysLog.setSysType(sysType);
		sysLog.setExecuteName(eunm.getTypeName());
		sysLog.setExecuteType(eunm.getTypeNum());
		sysLog.setExecutor(executor);
		sysLog.setParam(param.toString());
		sysLog.setCreateTime(new Date());
		sysLog.setIp(ip);
        sysLogMapper.insertByEntity(sysLog);
	}

}
