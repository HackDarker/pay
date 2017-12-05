package com.pay.business.merchant.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.payv2_buss_company_code  
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id,关联表payv2_buss_company
channel_id           Long(19)                               //渠道id,关联表payv2_channel
token                String(100)                            //商户手机token
code_url             String(255)                            //二维码链接
notify_url           String(255)                            //商户服务器回调地址
is_remark            Integer(10)                 2          //备注是否必填1是2否
create_time          Date(19)                               //创建时间
*/
public class Payv2BussCompanyCode implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long channelId;
	private	String token;
	private	String codeUrl;
	private	String notifyUrl;
	private	Integer isRemark;
	private	Date createTime;
	
	private String channelName;
	private String companyName;
	

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
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
	* company_id  Long(19)  //商户id,关联表payv2_buss_company    
	*/
	public Long getCompanyId(){
		return companyId;
	}
	
	/**
	* company_id  Long(19)  //商户id,关联表payv2_buss_company    
	*/
	public void setCompanyId(Long companyId){
		this.companyId = companyId;
	}
	
	/**
	* channel_id  Long(19)  //渠道id,关联表payv2_channel    
	*/
	public Long getChannelId(){
		return channelId;
	}
	
	/**
	* channel_id  Long(19)  //渠道id,关联表payv2_channel    
	*/
	public void setChannelId(Long channelId){
		this.channelId = channelId;
	}
	
	/**
	* code_url  String(255)  //二维码链接    
	*/
	public String getCodeUrl(){
		return codeUrl;
	}
	
	/**
	* code_url  String(255)  //二维码链接    
	*/
	public void setCodeUrl(String codeUrl){
		this.codeUrl = codeUrl;
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

	public String getNotifyUrl() {
		return notifyUrl;
	}

	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public Integer getIsRemark() {
		return isRemark;
	}

	public void setIsRemark(Integer isRemark) {
		this.isRemark = isRemark;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}