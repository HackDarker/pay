package com.pay.business.order.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.payv2_pay_order_notify   
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
order_num            String(64)                             //订单号
notify_url           String(255)                            //回调地址
param                String(2000)                           //回调参数
time                 Integer(10)                 0          //消耗时间毫秒单位
result               String(65535)                          //接收信息
result_len           Integer(10)                            //接收信息char字符长度
call_time            Date(19)                               //回调时间
create_time          Date(19)                               //创建时间
*/
public class Payv2PayOrderNotify implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	String orderNum;
	private	String notifyUrl;
	private	String param;
	private	Integer time;
	private	String result;
	private	Integer resultLen;
	private	Date callTime;
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
	* order_num  String(64)  //订单号    
	*/
	public String getOrderNum(){
		return orderNum;
	}
	
	/**
	* order_num  String(64)  //订单号    
	*/
	public void setOrderNum(String orderNum){
		this.orderNum = orderNum;
	}
	
	/**
	* notify_url  String(255)  //回调地址    
	*/
	public String getNotifyUrl(){
		return notifyUrl;
	}
	
	/**
	* notify_url  String(255)  //回调地址    
	*/
	public void setNotifyUrl(String notifyUrl){
		this.notifyUrl = notifyUrl;
	}
	
	/**
	* param  String(2000)  //回调参数    
	*/
	public String getParam(){
		return param;
	}
	
	/**
	* param  String(2000)  //回调参数    
	*/
	public void setParam(String param){
		this.param = param;
	}
	
	/**
	* time  Integer(10)  0  //消耗时间毫秒单位    
	*/
	public Integer getTime(){
		return time;
	}
	
	/**
	* time  Integer(10)  0  //消耗时间毫秒单位    
	*/
	public void setTime(Integer time){
		this.time = time;
	}
	
	/**
	* result  String(65535)  //接收信息    
	*/
	public String getResult(){
		return result;
	}
	
	/**
	* result  String(65535)  //接收信息    
	*/
	public void setResult(String result){
		this.result = result;
	}
	
	/**
	* result_len  Integer(10)  //接收信息char字符长度    
	*/
	public Integer getResultLen(){
		return resultLen;
	}
	
	/**
	* result_len  Integer(10)  //接收信息char字符长度    
	*/
	public void setResultLen(Integer resultLen){
		this.resultLen = resultLen;
	}
	
	/**
	* call_time  Date(19)  //回调时间    
	*/
	public Date getCallTime(){
		return callTime;
	}
	
	/**
	* call_time  Date(19)  //回调时间    
	*/
	public void setCallTime(Date callTime){
		this.callTime = callTime;
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