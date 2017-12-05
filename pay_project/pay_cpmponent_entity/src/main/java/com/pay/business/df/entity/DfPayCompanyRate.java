package com.pay.business.df.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.df_pay_company_rate      
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id,关联表payv2_buss_company
df_rate_id           Long(19)                               //代付通道id，关联表payv2_pay_way_rate_df
pay_way_rate         Double(5,2)                 0.00       //商户代付费（每笔/元）
total_amount         Double(16,2)                0.00       //商户资金池
status               Integer(10)                 1          //状态1.开启2.关闭
create_user_by       Long(19)                               //创建者
update_user_by       Long(19)                               //修改人
create_time          Date(19)                               //创建时间
update_time          Date(19)                               //修改时间
*/
public class DfPayCompanyRate implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long dfRateId;
	private	Double payWayRate;
	private	Double totalAmount;
	private	Integer status;
	private	Long createUserBy;
	private	Long updateUserBy;
	private	Date createTime;
	private	Date updateTime;
	private String dfPayWayName;

	public String getDfPayWayName() {
		return dfPayWayName;
	}

	public void setDfPayWayName(String dfPayWayName) {
		this.dfPayWayName = dfPayWayName;
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
	* df_rate_id  Long(19)  //代付通道id，关联表payv2_pay_way_rate_df    
	*/
	public Long getDfRateId(){
		return dfRateId;
	}
	
	/**
	* df_rate_id  Long(19)  //代付通道id，关联表payv2_pay_way_rate_df    
	*/
	public void setDfRateId(Long dfRateId){
		this.dfRateId = dfRateId;
	}
	
	/**
	* pay_way_rate  Double(5,2)  0.00  //商户代付费（每笔/元）    
	*/
	public Double getPayWayRate(){
		return payWayRate;
	}
	
	/**
	* pay_way_rate  Double(5,2)  0.00  //商户代付费（每笔/元）    
	*/
	public void setPayWayRate(Double payWayRate){
		this.payWayRate = payWayRate;
	}
	
	/**
	* total_amount  Double(16,2)  0.00  //商户资金池    
	*/
	public Double getTotalAmount(){
		return totalAmount;
	}
	
	/**
	* total_amount  Double(16,2)  0.00  //商户资金池    
	*/
	public void setTotalAmount(Double totalAmount){
		this.totalAmount = totalAmount;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启2.关闭    
	*/
	public Integer getStatus(){
		return status;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启2.关闭    
	*/
	public void setStatus(Integer status){
		this.status = status;
	}
	
	/**
	* create_user_by  Long(19)  //创建者    
	*/
	public Long getCreateUserBy(){
		return createUserBy;
	}
	
	/**
	* create_user_by  Long(19)  //创建者    
	*/
	public void setCreateUserBy(Long createUserBy){
		this.createUserBy = createUserBy;
	}
	
	/**
	* update_user_by  Long(19)  //修改人    
	*/
	public Long getUpdateUserBy(){
		return updateUserBy;
	}
	
	/**
	* update_user_by  Long(19)  //修改人    
	*/
	public void setUpdateUserBy(Long updateUserBy){
		this.updateUserBy = updateUserBy;
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
	
	/**
	* update_time  Date(19)  //修改时间    
	*/
	public Date getUpdateTime(){
		return updateTime;
	}
	
	/**
	* update_time  Date(19)  //修改时间    
	*/
	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	
}