package com.pay.business.payway.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.payv2_pay_type           
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
pay_way_id           Long(19)                               //支付平台id，关联payv2_pay_way
pay_type_name        String(30)                             //支付方式名称
dict_name            String(30)                             //字典匹配值
create_time          Date(19)                               //创建时间
*/
public class Payv2PayType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long payWayId;
	private	String payTypeName;
	private	String dictName;
	private	Date createTime;
	
	private String wayName;

	public String getWayName() {
		return wayName;
	}

	public void setWayName(String wayName) {
		this.wayName = wayName;
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
	* pay_way_id  Long(19)  //支付平台id，关联payv2_pay_way    
	*/
	public Long getPayWayId(){
		return payWayId;
	}
	
	/**
	* pay_way_id  Long(19)  //支付平台id，关联payv2_pay_way    
	*/
	public void setPayWayId(Long payWayId){
		this.payWayId = payWayId;
	}
	
	/**
	* pay_type_name  String(30)  //支付方式名称    
	*/
	public String getPayTypeName(){
		return payTypeName;
	}
	
	/**
	* pay_type_name  String(30)  //支付方式名称    
	*/
	public void setPayTypeName(String payTypeName){
		this.payTypeName = payTypeName;
	}
	
	/**
	* dict_name  String(30)  //字典匹配值    
	*/
	public String getDictName(){
		return dictName;
	}
	
	/**
	* dict_name  String(30)  //字典匹配值    
	*/
	public void setDictName(String dictName){
		this.dictName = dictName;
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