package com.pay.business.tx.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.tx_pay_rate_balance      
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
company_id           Long(19)                               //商户id，关联payv2_buss_company
rate_id              Long(19)                               //通道id，关联表payv2_pay_way_rate
balance              Double(16,2)                0.00       //当日交易额（凌晨定时清零）
tx_balance           Double(16,2)                0.00       //提现交易额
t1_balance           Double(16,2)                0.00       //T1交易额
create_time          Date(19)                               //创建时间
*/
public class TxPayRateBalance implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	Long companyId;
	private	Long rateId;
	private	Double balance;
	private	Double txBalance;
	private	Double t1Balance;
	private	Date createTime;
	
	private String payWayName;
	private String companyName;

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
	* rate_id  Long(19)  //通道id，关联表payv2_pay_way_rate    
	*/
	public Long getRateId(){
		return rateId;
	}
	
	/**
	* rate_id  Long(19)  //通道id，关联表payv2_pay_way_rate    
	*/
	public void setRateId(Long rateId){
		this.rateId = rateId;
	}
	
	/**
	* tx_balance  Double(16,2)  0.00  //提现余额    
	*/
	public Double getTxBalance(){
		return txBalance;
	}
	
	/**
	* tx_balance  Double(16,2)  0.00  //提现余额    
	*/
	public void setTxBalance(Double txBalance){
		this.txBalance = txBalance;
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

	public String getPayWayName() {
		return payWayName;
	}

	public void setPayWayName(String payWayName) {
		this.payWayName = payWayName;
	}

	public Double getT1Balance() {
		return t1Balance;
	}

	public void setT1Balance(Double t1Balance) {
		this.t1Balance = t1Balance;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
}