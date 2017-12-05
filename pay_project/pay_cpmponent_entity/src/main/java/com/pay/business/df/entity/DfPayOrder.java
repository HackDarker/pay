package com.pay.business.df.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.df_pay_order             
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
df_order_num         String(64)                             //代付订单号
batch_num            String(60)                             //批次号
channel_id           Long(19)                               //渠道id，关联payv2_channel表
df_abstract          String(65535)                          //摘要
remark               String(65535)                          //自定义字段（回调原样返回）
merchant_batch_num   String(64)                             //商户批次号
batch_remark         String(65535)                          //批次说明
df_merchant_order_num String(64)                             //商户代付订单号
company_id           Long(19)                               //商户id,关联payv2_buss_company表
df_type              Integer(10)                 1          //订单类型1.余额代付2.垫资代付
df_rate_id           Long(19)                               //代付通道id
account_type         Integer(10)                 1          //账号类型1借记卡2贷记卡3公户
account_num          String(30)                             //银行账号
account_name         String(30)                             //开户名称
bank_name            String(50)                             //开户行全称
bank_branch_num      String(50)                             //开户行支行联行号
bank_transaction     String(50)                             //上游流水号
df_pay_money         Double(9,2)                            //代付金额
df_pay_way_money     Double(9,2)        NOTNULL  0.00       //代付通道费（以元为单位，小于分不收费）
df_cost_rate_money   Double(9,2)                 0.00       //成本代付费（以元为单位，小于分不收费）
df_status            Integer(10)                 1          //代付状态,1代付成功,2代付中3代付失败
req_time             Date(19)                               //商户请求时间
pay_time             Date(19)                               //交易时间
notify_url           String(255)                            //回调地址url（服务器）
callback_time        Date(19)                               //订单回调时间
update_time          Date(19)                               //修改时间
*/
public class DfPayOrder implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	String dfOrderNum;
	private	String batchNum;
	private	Long channelId;
	private	String dfAbstract;
	private	String remark;
	private	String merchantBatchNum;
	private	String batchRemark;
	private	String dfMerchantOrderNum;
	private	Long companyId;
	private	Integer dfType;
	private	Long dfRateId;
	private	Integer accountType;
	private	String accountNum;
	private	String accountName;
	private	String bankName;
	private String mobile;
	private	String bankBranchName;
	private	String bankBranchNum;
	private	String bankTransaction;
	private	Double dfPayMoney;
	private	Double dfPayWayMoney;
	private	Double dfCostRateMoney;
	private	Integer dfStatus;
	private	Date reqTime;
	private	Date payTime;
	private	String notifyUrl;
	private	Date callbackTime;
	private	Date updateTime;
	
	private String channelName;
	private String companyName;
	private String dfRateName;
	private String dfPayWayName;

	public String getDfPayWayName() {
		return dfPayWayName;
	}

	public void setDfPayWayName(String dfPayWayName) {
		this.dfPayWayName = dfPayWayName;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getDfRateName() {
		return dfRateName;
	}

	public void setDfRateName(String dfRateName) {
		this.dfRateName = dfRateName;
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
	* df_order_num  String(64)  //代付订单号    
	*/
	public String getDfOrderNum(){
		return dfOrderNum;
	}
	
	/**
	* df_order_num  String(64)  //代付订单号    
	*/
	public void setDfOrderNum(String dfOrderNum){
		this.dfOrderNum = dfOrderNum;
	}
	
	/**
	* batch_num  String(60)  //批次号    
	*/
	public String getBatchNum(){
		return batchNum;
	}
	
	/**
	* batch_num  String(60)  //批次号    
	*/
	public void setBatchNum(String batchNum){
		this.batchNum = batchNum;
	}
	
	/**
	* channel_id  Long(19)  //渠道id，关联payv2_channel表    
	*/
	public Long getChannelId(){
		return channelId;
	}
	
	/**
	* channel_id  Long(19)  //渠道id，关联payv2_channel表    
	*/
	public void setChannelId(Long channelId){
		this.channelId = channelId;
	}
	
	/**
	* df_abstract  String(65535)  //摘要    
	*/
	public String getDfAbstract(){
		return dfAbstract;
	}
	
	/**
	* df_abstract  String(65535)  //摘要    
	*/
	public void setDfAbstract(String dfAbstract){
		this.dfAbstract = dfAbstract;
	}
	
	/**
	* remark  String(65535)  //自定义字段（回调原样返回）    
	*/
	public String getRemark(){
		return remark;
	}
	
	/**
	* remark  String(65535)  //自定义字段（回调原样返回）    
	*/
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	/**
	* merchant_batch_num  String(64)  //商户批次号    
	*/
	public String getMerchantBatchNum(){
		return merchantBatchNum;
	}
	
	/**
	* merchant_batch_num  String(64)  //商户批次号    
	*/
	public void setMerchantBatchNum(String merchantBatchNum){
		this.merchantBatchNum = merchantBatchNum;
	}
	
	/**
	* batch_remark  String(65535)  //批次说明    
	*/
	public String getBatchRemark(){
		return batchRemark;
	}
	
	/**
	* batch_remark  String(65535)  //批次说明    
	*/
	public void setBatchRemark(String batchRemark){
		this.batchRemark = batchRemark;
	}
	
	/**
	* df_merchant_order_num  String(64)  //商户代付订单号    
	*/
	public String getDfMerchantOrderNum(){
		return dfMerchantOrderNum;
	}
	
	/**
	* df_merchant_order_num  String(64)  //商户代付订单号    
	*/
	public void setDfMerchantOrderNum(String dfMerchantOrderNum){
		this.dfMerchantOrderNum = dfMerchantOrderNum;
	}
	
	/**
	* company_id  Long(19)  //商户id,关联payv2_buss_company表    
	*/
	public Long getCompanyId(){
		return companyId;
	}
	
	/**
	* company_id  Long(19)  //商户id,关联payv2_buss_company表    
	*/
	public void setCompanyId(Long companyId){
		this.companyId = companyId;
	}
	
	/**
	* df_type  Integer(10)  1  //订单类型1.余额代付2.垫资代付    
	*/
	public Integer getDfType(){
		return dfType;
	}
	
	/**
	* df_type  Integer(10)  1  //订单类型1.余额代付2.垫资代付    
	*/
	public void setDfType(Integer dfType){
		this.dfType = dfType;
	}
	
	/**
	* df_rate_id  Long(19)  //代付通道id    
	*/
	public Long getDfRateId(){
		return dfRateId;
	}
	
	/**
	* df_rate_id  Long(19)  //代付通道id    
	*/
	public void setDfRateId(Long dfRateId){
		this.dfRateId = dfRateId;
	}
	
	/**
	* account_type  Integer(10)  1  //账号类型1借记卡2贷记卡3公户    
	*/
	public Integer getAccountType(){
		return accountType;
	}
	
	/**
	* account_type  Integer(10)  1  //账号类型1借记卡2贷记卡3公户    
	*/
	public void setAccountType(Integer accountType){
		this.accountType = accountType;
	}
	
	/**
	* account_num  String(30)  //银行账号    
	*/
	public String getAccountNum(){
		return accountNum;
	}
	
	/**
	* account_num  String(30)  //银行账号    
	*/
	public void setAccountNum(String accountNum){
		this.accountNum = accountNum;
	}
	
	/**
	* account_name  String(30)  //开户名称    
	*/
	public String getAccountName(){
		return accountName;
	}
	
	/**
	* account_name  String(30)  //开户名称    
	*/
	public void setAccountName(String accountName){
		this.accountName = accountName;
	}
	
	/**
	* bank_name  String(50)  //开户行全称    
	*/
	public String getBankName(){
		return bankName;
	}
	
	/**
	* bank_name  String(50)  //开户行全称    
	*/
	public void setBankName(String bankName){
		this.bankName = bankName;
	}
	
	/**
	* bank_branch_num  String(50)  //开户行支行联行号    
	*/
	public String getBankBranchNum(){
		return bankBranchNum;
	}
	
	/**
	* bank_branch_num  String(50)  //开户行支行联行号    
	*/
	public void setBankBranchNum(String bankBranchNum){
		this.bankBranchNum = bankBranchNum;
	}
	
	/**
	* bank_transaction  String(50)  //上游流水号    
	*/
	public String getBankTransaction(){
		return bankTransaction;
	}
	
	/**
	* bank_transaction  String(50)  //上游流水号    
	*/
	public void setBankTransaction(String bankTransaction){
		this.bankTransaction = bankTransaction;
	}
	
	/**
	* df_pay_money  Double(9,2)  //代付金额    
	*/
	public Double getDfPayMoney(){
		return dfPayMoney;
	}
	
	/**
	* df_pay_money  Double(9,2)  //代付金额    
	*/
	public void setDfPayMoney(Double dfPayMoney){
		this.dfPayMoney = dfPayMoney;
	}
	
	/**
	* df_pay_way_money  Double(9,2)  NOTNULL  0.00  //代付通道费（以元为单位，小于分不收费）    
	*/
	public Double getDfPayWayMoney(){
		return dfPayWayMoney;
	}
	
	/**
	* df_pay_way_money  Double(9,2)  NOTNULL  0.00  //代付通道费（以元为单位，小于分不收费）    
	*/
	public void setDfPayWayMoney(Double dfPayWayMoney){
		this.dfPayWayMoney = dfPayWayMoney;
	}
	
	/**
	* df_cost_rate_money  Double(9,2)  0.00  //成本代付费（以元为单位，小于分不收费）    
	*/
	public Double getDfCostRateMoney(){
		return dfCostRateMoney;
	}
	
	/**
	* df_cost_rate_money  Double(9,2)  0.00  //成本代付费（以元为单位，小于分不收费）    
	*/
	public void setDfCostRateMoney(Double dfCostRateMoney){
		this.dfCostRateMoney = dfCostRateMoney;
	}
	
	/**
	* df_status  Integer(10)  1  //代付状态,1代付成功,2代付中3代付失败    
	*/
	public Integer getDfStatus(){
		return dfStatus;
	}
	
	/**
	* df_status  Integer(10)  1  //代付状态,1代付成功,2代付中3代付失败    
	*/
	public void setDfStatus(Integer dfStatus){
		this.dfStatus = dfStatus;
	}
	
	/**
	* req_time  Date(19)  //商户请求时间    
	*/
	public Date getReqTime(){
		return reqTime;
	}
	
	/**
	* req_time  Date(19)  //商户请求时间    
	*/
	public void setReqTime(Date reqTime){
		this.reqTime = reqTime;
	}
	
	/**
	* pay_time  Date(19)  //交易时间    
	*/
	public Date getPayTime(){
		return payTime;
	}
	
	/**
	* pay_time  Date(19)  //交易时间    
	*/
	public void setPayTime(Date payTime){
		this.payTime = payTime;
	}
	
	/**
	* notify_url  String(255)  //回调地址url（服务器）    
	*/
	public String getNotifyUrl(){
		return notifyUrl;
	}
	
	/**
	* notify_url  String(255)  //回调地址url（服务器）    
	*/
	public void setNotifyUrl(String notifyUrl){
		this.notifyUrl = notifyUrl;
	}
	
	/**
	* callback_time  Date(19)  //订单回调时间    
	*/
	public Date getCallbackTime(){
		return callbackTime;
	}
	
	/**
	* callback_time  Date(19)  //订单回调时间    
	*/
	public void setCallbackTime(Date callbackTime){
		this.callbackTime = callbackTime;
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

	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}
}