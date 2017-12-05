package com.pay.business.merchant.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.pay.business.payway.entity.Payv2PayWayRate;

/**
TABLE:.payv2_company_pay_type   
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id，关联payv2_buss_company
pay_way_id           Long(19)                               //支付平台id,关联payv2_pay_way
pay_type_id          Long(19)                               //支付方式id,关联payv2_pay_type
pay_way_rate         Double(16,2)                0.00       //手续费率
create_time          Date(19)                               //创建时间
*/
public class Payv2CompanyPayType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long payWayId;
	private	Long payTypeId;
	private	Double payWayRate;
	private	Date createTime;
	
	private String wayName;
	private String payTypeName;
	
	private List<Payv2BussSupportPayWay> wayList;
	private List<Payv2PayWayRate> allWayList;

	public List<Payv2PayWayRate> getAllWayList() {
		return allWayList;
	}

	public void setAllWayList(List<Payv2PayWayRate> allWayList) {
		this.allWayList = allWayList;
	}

	public List<Payv2BussSupportPayWay> getWayList() {
		return wayList;
	}

	public void setWayList(List<Payv2BussSupportPayWay> wayList) {
		this.wayList = wayList;
	}

	public String getWayName() {
		return wayName;
	}

	public void setWayName(String wayName) {
		this.wayName = wayName;
	}

	public String getPayTypeName() {
		return payTypeName;
	}

	public void setPayTypeName(String payTypeName) {
		this.payTypeName = payTypeName;
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
	* pay_way_id  Long(19)  //支付平台id,关联payv2_pay_way    
	*/
	public Long getPayWayId(){
		return payWayId;
	}
	
	/**
	* pay_way_id  Long(19)  //支付平台id,关联payv2_pay_way    
	*/
	public void setPayWayId(Long payWayId){
		this.payWayId = payWayId;
	}
	
	/**
	* pay_type_id  Long(19)  //支付方式id,关联payv2_pay_type    
	*/
	public Long getPayTypeId(){
		return payTypeId;
	}
	
	/**
	* pay_type_id  Long(19)  //支付方式id,关联payv2_pay_type    
	*/
	public void setPayTypeId(Long payTypeId){
		this.payTypeId = payTypeId;
	}
	
	/**
	* pay_way_rate  Double(16,2)  0.00  //手续费率    
	*/
	public Double getPayWayRate(){
		return payWayRate;
	}
	
	/**
	* pay_way_rate  Double(16,2)  0.00  //手续费率    
	*/
	public void setPayWayRate(Double payWayRate){
		this.payWayRate = payWayRate;
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