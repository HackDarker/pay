package com.pay.business.jk.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.jk_order                 
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
info_id              Long(19)                               //商户信息id，关联jk_company_info
company_id           Long(19)                               //商户id，关联payv2_buss_company
url                  String(255)                            //商户url
con_url              String(255)                            //转化后url
ip                   String(20)                             //调用ip
service_balance      Double(9,2)                 0.00       //服务费
create_time          Date(19)                               //创建时间
*/
public class JkOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long infoId;
	private	Long companyId;
	private	String url;
	private	String conUrl;
	private	String ip;
	private	Double serviceBalance;
	private	Date createTime;

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
	* info_id  Long(19)  //商户信息id，关联jk_company_info    
	*/
	public Long getInfoId(){
		return infoId;
	}
	
	/**
	* info_id  Long(19)  //商户信息id，关联jk_company_info    
	*/
	public void setInfoId(Long infoId){
		this.infoId = infoId;
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
	* url  String(255)  //商户url    
	*/
	public String getUrl(){
		return url;
	}
	
	/**
	* url  String(255)  //商户url    
	*/
	public void setUrl(String url){
		this.url = url;
	}
	
	/**
	* con_url  String(255)  //转化后url    
	*/
	public String getConUrl(){
		return conUrl;
	}
	
	/**
	* con_url  String(255)  //转化后url    
	*/
	public void setConUrl(String conUrl){
		this.conUrl = conUrl;
	}
	
	/**
	* ip  String(20)  //调用ip    
	*/
	public String getIp(){
		return ip;
	}
	
	/**
	* ip  String(20)  //调用ip    
	*/
	public void setIp(String ip){
		this.ip = ip;
	}
	
	/**
	* service_balance  Double(9,2)  0.00  //服务费    
	*/
	public Double getServiceBalance(){
		return serviceBalance;
	}
	
	/**
	* service_balance  Double(9,2)  0.00  //服务费    
	*/
	public void setServiceBalance(Double serviceBalance){
		this.serviceBalance = serviceBalance;
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
	
}