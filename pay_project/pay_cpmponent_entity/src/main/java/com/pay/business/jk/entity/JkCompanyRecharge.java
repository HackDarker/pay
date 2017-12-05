package com.pay.business.jk.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.jk_company_recharge      
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
info_id              Long(19)                               //商户信息id，关联jk_company_info
company_id           Long(19)                               //商户id，关联payv2_buss_company
add_balance          Long(19)                    0          //充值金额
create_time          Date(19)                               //创建时间
*/
public class JkCompanyRecharge implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long infoId;
	private	Long companyId;
	private	Double addBalance;
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
	* add_balance  Long(19)  0  //充值金额    
	*/
	public Double getAddBalance(){
		return addBalance;
	}
	
	/**
	* add_balance  Long(19)  0  //充值金额    
	*/
	public void setAddBalance(Double addBalance){
		this.addBalance = addBalance;
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