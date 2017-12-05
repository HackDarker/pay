package com.pay.business.jk.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.jk_company_info          
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id，关联payv2_buss_company
balance              Double(16,2)                0.00       //余额
call_count           Integer(10)                 0          //调用次数
service_money        Double(9,2)                 0.00       //服务费
last_call_ip         String(20)                             //最后调用ip
last_call_time       Date(19)                               //最后调用时间
status               Integer(10)                 1          //状态1.开启
create_time          Date(19)                               //创建时间
*/
public class JkCompanyInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Double balance;
	private	Integer callCount;
	private	Double serviceMoney;
	private	String lastCallIp;
	private	Date lastCallTime;
	private	Integer status;
	private	Date createTime;
	
	private String companyKey;
	private String companySecret;
	private String companyName;//商户名称
	private String channelName;//渠道商名称
	private String serviceBalance;//消费金额
	private Double addBalace;//充值金额
	
	public Double getAddBalace() {
		return addBalace;
	}

	public void setAddBalace(Double addBalace) {
		this.addBalace = addBalace;
	}

	public String getServiceBalance() {
		return serviceBalance;
	}

	public void setServiceBalance(String serviceBalance) {
		this.serviceBalance = serviceBalance;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
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
	* company_id  Long(19)  //商户id，关联payv2_buss_company    
	*/
	public Long getCompanyId(){
		return companyId;
	}
	
	/**
	* company_id  Long(19)  //商户id，关联payv2_buss_company    
	*/
	public void setCompanyId(Long companyId){
		this.companyId = companyId;
	}
	
	/**
	* balance  Double(16,2)  0.00  //余额    
	*/
	public Double getBalance(){
		return balance;
	}
	
	/**
	* balance  Double(16,2)  0.00  //余额    
	*/
	public void setBalance(Double balance){
		this.balance = balance;
	}
	
	/**
	* call_count  Integer(10)  0  //调用次数    
	*/
	public Integer getCallCount(){
		return callCount;
	}
	
	/**
	* call_count  Integer(10)  0  //调用次数    
	*/
	public void setCallCount(Integer callCount){
		this.callCount = callCount;
	}
	
	/**
	* service_money  Double(9,2)  0.00  //服务费    
	*/
	public Double getServiceMoney(){
		return serviceMoney;
	}
	
	/**
	* service_money  Double(9,2)  0.00  //服务费    
	*/
	public void setServiceMoney(Double serviceMoney){
		this.serviceMoney = serviceMoney;
	}
	
	/**
	* last_call_ip  String(20)  //最后调用ip    
	*/
	public String getLastCallIp(){
		return lastCallIp;
	}
	
	/**
	* last_call_ip  String(20)  //最后调用ip    
	*/
	public void setLastCallIp(String lastCallIp){
		this.lastCallIp = lastCallIp;
	}
	
	/**
	* last_call_time  Date(19)  //最后调用时间    
	*/
	public Date getLastCallTime(){
		return lastCallTime;
	}
	
	/**
	* last_call_time  Date(19)  //最后调用时间    
	*/
	public void setLastCallTime(Date lastCallTime){
		this.lastCallTime = lastCallTime;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启    
	*/
	public Integer getStatus(){
		return status;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启    
	*/
	public void setStatus(Integer status){
		this.status = status;
	}
	
	/**
	* create_time  Date(19)  //创建时间    
	*/
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
	* create_time  Date(19)  //创建时间    
	*/
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}

	public String getCompanyKey() {
		return companyKey;
	}

	public void setCompanyKey(String companyKey) {
		this.companyKey = companyKey;
	}

	public String getCompanySecret() {
		return companySecret;
	}

	public void setCompanySecret(String companySecret) {
		this.companySecret = companySecret;
	}
	
}