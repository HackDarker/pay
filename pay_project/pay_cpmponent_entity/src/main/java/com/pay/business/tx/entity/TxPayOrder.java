package com.pay.business.tx.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.tx_pay_order             
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
order_num            String(64)                             //提现订单号
company_id           Long(19)                               //商户id，关联payv2_buss_company
rate_id              Long(19)                               //通道id，关联payv2_pay_way_rate
pay_platform         Integer(10)                 1          //支付平台1支付宝2微信3QQ
account_bank         String(50)                             //收款银行
account_name         String(50)                             //收款人姓名
account_card         String(50)                             //收款账号
status               Integer(10)                 2          //状态1提现成功2提现中3提现失败
amount               Double(16,2)                0.00       //提现交易额
service_amount       Double(16,2)                0.00       //提现手续费
pay_service_amount   Double(16,2)                0.00       //交易手续费
arrival_amount       Double(16,2)                0.00       //到账金额
tx_time              Date(19)                               //提现时间
*/
public class TxPayOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	String orderNum;
	private	Long companyId;
	private	Long rateId;
	private	Integer payPlatform;
	private	String accountBank;
	private	String accountName;
	private	String accountCard;
	private	Integer status;
	private	Double amount;
	private	Double serviceAmount;
	private	Double payServiceAmount;
	private	Double arrivalAmount;
	private	Date txTime;

	private	String payWayWame;//支付通道名称（商户显示的）
	private String companyName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getPayWayWame() {
		return payWayWame;
	}

	public void setPayWayWame(String payWayWame) {
		this.payWayWame = payWayWame;
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
	* order_num  String(64)  //提现订单号    
	*/
	public String getOrderNum(){
		return orderNum;
	}
	
	/**
	* order_num  String(64)  //提现订单号    
	*/
	public void setOrderNum(String orderNum){
		this.orderNum = orderNum;
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
	* rate_id  Long(19)  //通道id，关联payv2_pay_way_rate    
	*/
	public Long getRateId(){
		return rateId;
	}
	
	/**
	* rate_id  Long(19)  //通道id，关联payv2_pay_way_rate    
	*/
	public void setRateId(Long rateId){
		this.rateId = rateId;
	}
	
	/**
	* pay_platform  Integer(10)  1  //支付平台1支付宝2微信3QQ    
	*/
	public Integer getPayPlatform(){
		return payPlatform;
	}
	
	/**
	* pay_platform  Integer(10)  1  //支付平台1支付宝2微信3QQ    
	*/
	public void setPayPlatform(Integer payPlatform){
		this.payPlatform = payPlatform;
	}
	
	/**
	* account_bank  String(50)  //收款银行    
	*/
	public String getAccountBank(){
		return accountBank;
	}
	
	/**
	* account_bank  String(50)  //收款银行    
	*/
	public void setAccountBank(String accountBank){
		this.accountBank = accountBank;
	}
	
	/**
	* account_name  String(50)  //收款人姓名    
	*/
	public String getAccountName(){
		return accountName;
	}
	
	/**
	* account_name  String(50)  //收款人姓名    
	*/
	public void setAccountName(String accountName){
		this.accountName = accountName;
	}
	
	/**
	* account_card  String(50)  //收款账号    
	*/
	public String getAccountCard(){
		return accountCard;
	}
	
	/**
	* account_card  String(50)  //收款账号    
	*/
	public void setAccountCard(String accountCard){
		this.accountCard = accountCard;
	}
	
	/**
	* status  Integer(10)  2  //状态1提现成功2提现中3提现失败    
	*/
	public Integer getStatus(){
		return status;
	}
	
	/**
	* status  Integer(10)  2  //状态1提现成功2提现中3提现失败    
	*/
	public void setStatus(Integer status){
		this.status = status;
	}
	
	/**
	* amount  Double(16,2)  0.00  //提现金额    
	*/
	public Double getAmount(){
		return amount;
	}
	
	/**
	* amount  Double(16,2)  0.00  //提现金额    
	*/
	public void setAmount(Double amount){
		this.amount = amount;
	}
	
	/**
	* service_amount  Double(16,2)  0.00  //手续费    
	*/
	public Double getServiceAmount(){
		return serviceAmount;
	}
	
	/**
	* service_amount  Double(16,2)  0.00  //手续费    
	*/
	public void setServiceAmount(Double serviceAmount){
		this.serviceAmount = serviceAmount;
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
	* tx_time  Date(19)  //提现时间    
	*/
	public Date getTxTime(){
		return txTime;
	}
	
	/**
	* tx_time  Date(19)  //提现时间    
	*/
	public void setTxTime(Date txTime){
		this.txTime = txTime;
	}

	public Double getPayServiceAmount() {
		return payServiceAmount;
	}

	public void setPayServiceAmount(Double payServiceAmount) {
		this.payServiceAmount = payServiceAmount;
	}
}