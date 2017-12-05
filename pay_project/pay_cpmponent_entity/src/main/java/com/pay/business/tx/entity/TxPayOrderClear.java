package com.pay.business.tx.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.tx_pay_order_clear       
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id，关联payv2_buss_company
rate_id              Long(19)                               //通道id,关联payv2_pay_way_rate
balance              Double(16,2)                0.00       //交易额
t1_balance           Double(16,2)                0.00       //T1交易额
tx_balance           Double(16,2)                0.00       //提现交易额
tx_service_amount    Double(16,2)                0.00       //提现手续费
pay_service_amount   Double(16,2)                0.00       //交易手续费
tx_count             Integer(10)                 0          //提现次数
arrival_amount       Double(16,2)                0.00       //到账金额
clear_time           Date(19)                               //账单时间
create_time          Date(19)                               //创建时间
*/
public class TxPayOrderClear implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long rateId;
	private	Double balance;
	private	Double t1Balance;
	private	Double txBalance;
	private	Double txServiceAmount;
	private	Double payServiceAmount;
	private	Integer txCount;
	private	Double arrivalAmount;
	private	Date clearTime;
	private	Date createTime;
	
	private String companyName;
	private String payWayName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPayWayName() {
		return payWayName;
	}

	public void setPayWayName(String payWayName) {
		this.payWayName = payWayName;
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
	* balance  Double(16,2)  0.00  //交易额    
	*/
	public Double getBalance(){
		return balance;
	}
	
	/**
	* balance  Double(16,2)  0.00  //交易额    
	*/
	public void setBalance(Double balance){
		this.balance = balance;
	}
	
	/**
	* t1_balance  Double(16,2)  0.00  //T1交易额    
	*/
	public Double getT1Balance(){
		return t1Balance;
	}
	
	/**
	* t1_balance  Double(16,2)  0.00  //T1交易额    
	*/
	public void setT1Balance(Double t1Balance){
		this.t1Balance = t1Balance;
	}
	
	/**
	* tx_balance  Double(16,2)  0.00  //提现交易额    
	*/
	public Double getTxBalance(){
		return txBalance;
	}
	
	/**
	* tx_balance  Double(16,2)  0.00  //提现交易额    
	*/
	public void setTxBalance(Double txBalance){
		this.txBalance = txBalance;
	}
	
	/**
	* tx_service_amount  Double(16,2)  0.00  //提现手续费    
	*/
	public Double getTxServiceAmount(){
		return txServiceAmount;
	}
	
	/**
	* tx_service_amount  Double(16,2)  0.00  //提现手续费    
	*/
	public void setTxServiceAmount(Double txServiceAmount){
		this.txServiceAmount = txServiceAmount;
	}
	
	/**
	* pay_service_amount  Double(16,2)  0.00  //交易手续费    
	*/
	public Double getPayServiceAmount(){
		return payServiceAmount;
	}
	
	/**
	* pay_service_amount  Double(16,2)  0.00  //交易手续费    
	*/
	public void setPayServiceAmount(Double payServiceAmount){
		this.payServiceAmount = payServiceAmount;
	}
	
	/**
	* tx_count  Integer(10)  0  //提现次数    
	*/
	public Integer getTxCount(){
		return txCount;
	}
	
	/**
	* tx_count  Integer(10)  0  //提现次数    
	*/
	public void setTxCount(Integer txCount){
		this.txCount = txCount;
	}
	
	/**
	* arrival_amount  Double(16,2)  0.00  //到账金额    
	*/
	public Double getArrivalAmount(){
		return arrivalAmount;
	}
	
	/**
	* arrival_amount  Double(16,2)  0.00  //到账金额    
	*/
	public void setArrivalAmount(Double arrivalAmount){
		this.arrivalAmount = arrivalAmount;
	}
	
	/**
	* clear_time  Date(19)  //账单时间    
	*/
	public Date getClearTime(){
		return clearTime;
	}
	
	/**
	* clear_time  Date(19)  //账单时间    
	*/
	public void setClearTime(Date clearTime){
		this.clearTime = clearTime;
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

	public Long getRateId() {
		return rateId;
	}

	public void setRateId(Long rateId) {
		this.rateId = rateId;
	}
	
}