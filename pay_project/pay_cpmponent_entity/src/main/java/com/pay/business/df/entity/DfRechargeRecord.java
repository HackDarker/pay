package com.pay.business.df.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.df_recharge_record       
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id，关联payv2_buss_company
df_rate_id           Long(19)                               //代付通道id,关联df_pay_way_rate
rate_money           Double(16,2)                0.00       //充值金额
create_user_by       Long(19)                               //操作人
create_time          Date(19)                               //操作时间
*/
public class DfRechargeRecord implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long dfRateId;
	private	Double rateMoney;
	private	Long createUserBy;
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
	* df_rate_id  Long(19)  //代付通道id,关联df_pay_way_rate    
	*/
	public Long getDfRateId(){
		return dfRateId;
	}
	
	/**
	* df_rate_id  Long(19)  //代付通道id,关联df_pay_way_rate    
	*/
	public void setDfRateId(Long dfRateId){
		this.dfRateId = dfRateId;
	}
	
	/**
	* rate_money  Double(16,2)  0.00  //充值金额    
	*/
	public Double getRateMoney(){
		return rateMoney;
	}
	
	/**
	* rate_money  Double(16,2)  0.00  //充值金额    
	*/
	public void setRateMoney(Double rateMoney){
		this.rateMoney = rateMoney;
	}
	
	/**
	* create_user_by  Long(19)  //操作人    
	*/
	public Long getCreateUserBy(){
		return createUserBy;
	}
	
	/**
	* create_user_by  Long(19)  //操作人    
	*/
	public void setCreateUserBy(Long createUserBy){
		this.createUserBy = createUserBy;
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