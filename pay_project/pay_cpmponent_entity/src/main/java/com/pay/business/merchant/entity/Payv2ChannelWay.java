package com.pay.business.merchant.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.payv2_channel_way        
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
channel_id           Long(19)                               //渠道商id，关联payv2_channel表
pay_way_id           Long(19)                               //支付渠道id,关联payv2_pay_way表
pay_type_id          Long(19)                               //支付方式id，关联payv2_pay_type
pay_way_rate         Double(16,2)                0.00       //手续费率
create_time          Date(19)                               //创建时间
*/
public class Payv2ChannelWay implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long channelId;
	private	Long payWayId;
	private	Long payTypeId;
	private	Double payWayRate;
	private	Date createTime;
	
	private String wayName;//支付渠道名称
	private String payTypeName;//支付类型名称

	public String getwayName() {
		return wayName;
	}

	public void setwayName(String wayName) {
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
	* channel_id  Long(19)  //渠道商id，关联payv2_channel表    
	*/
	public Long getChannelId(){
		return channelId;
	}
	
	/**
	* channel_id  Long(19)  //渠道商id，关联payv2_channel表    
	*/
	public void setChannelId(Long channelId){
		this.channelId = channelId;
	}
	
	/**
	* pay_way_id  Long(19)  //支付渠道id,关联payv2_pay_way表    
	*/
	public Long getPayWayId(){
		return payWayId;
	}
	
	/**
	* pay_way_id  Long(19)  //支付渠道id,关联payv2_pay_way表    
	*/
	public void setPayWayId(Long payWayId){
		this.payWayId = payWayId;
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

	public Long getPayTypeId() {
		return payTypeId;
	}

	public void setPayTypeId(Long payTypeId) {
		this.payTypeId = payTypeId;
	}

	public Double getPayWayRate() {
		return payWayRate;
	}

	public void setPayWayRate(Double payWayRate) {
		this.payWayRate = payWayRate;
	}
	
}