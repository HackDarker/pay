package com.pay.business.sysconfig.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.sys_log                  
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
sys_type             Integer(10)                 1          //系统类型1官方后台2.渠道商后台3.商户后台
method_name          String(50)                             //方法名
packages_url         String(50)                             //包名路劲
param                String(65535)                          //请求参数
execute_type         Integer(10)                 1          //执行类型
execute_name         String(50)                             //执行名称
ip                   String(10)                             //ip地址
executor             Long(19)                               //操作人
create_time          Date(19)                               //操作时间
*/
public class SysLog implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Integer sysType;
	private	String methodName;
	private	String packagesUrl;
	private	String param;
	private	Integer executeType;
	private	String executeName;
	private	String ip;
	private	Long executor;
	private	Date createTime;
	
	private String executorName;

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	/**
	* id  Long(19)  NOTNULL  //    
	*/
	public Long getId(){
		return id;
	}
	
	/**
	* id  Long(19)  NOTNULL  //    
	*/
	public void setId(Long id){
		this.id = id;
	}
	
	/**
	* sys_type  Integer(10)  1  //系统类型1官方后台2.渠道商后台3.商户后台    
	*/
	public Integer getSysType(){
		return sysType;
	}
	
	/**
	* sys_type  Integer(10)  1  //系统类型1官方后台2.渠道商后台3.商户后台    
	*/
	public void setSysType(Integer sysType){
		this.sysType = sysType;
	}
	
	/**
	* method_name  String(50)  //方法名    
	*/
	public String getMethodName(){
		return methodName;
	}
	
	/**
	* method_name  String(50)  //方法名    
	*/
	public void setMethodName(String methodName){
		this.methodName = methodName;
	}
	
	/**
	* packages_url  String(50)  //包名路劲    
	*/
	public String getPackagesUrl(){
		return packagesUrl;
	}
	
	/**
	* packages_url  String(50)  //包名路劲    
	*/
	public void setPackagesUrl(String packagesUrl){
		this.packagesUrl = packagesUrl;
	}
	
	/**
	* param  String(65535)  //请求参数    
	*/
	public String getParam(){
		return param;
	}
	
	/**
	* param  String(65535)  //请求参数    
	*/
	public void setParam(String param){
		this.param = param;
	}
	
	/**
	* execute_type  Integer(10)  1  //执行类型    
	*/
	public Integer getExecuteType(){
		return executeType;
	}
	
	/**
	* execute_type  Integer(10)  1  //执行类型    
	*/
	public void setExecuteType(Integer executeType){
		this.executeType = executeType;
	}
	
	/**
	* execute_name  String(50)  //执行名称    
	*/
	public String getExecuteName(){
		return executeName;
	}
	
	/**
	* execute_name  String(50)  //执行名称    
	*/
	public void setExecuteName(String executeName){
		this.executeName = executeName;
	}
	
	/**
	* ip  String(10)  //ip地址    
	*/
	public String getIp(){
		return ip;
	}
	
	/**
	* ip  String(10)  //ip地址    
	*/
	public void setIp(String ip){
		this.ip = ip;
	}
	
	/**
	* executor  Long(19)  //操作人    
	*/
	public Long getExecutor(){
		return executor;
	}
	
	/**
	* executor  Long(19)  //操作人    
	*/
	public void setExecutor(Long executor){
		this.executor = executor;
	}
	
	/**
	* create_time  Date(19)  //操作时间    
	*/
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
	* create_time  Date(19)  //操作时间    
	*/
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	
}