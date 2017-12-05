package com.pay.business.util.tianxia;

import java.io.Serializable;

public class TianXiaVO implements Serializable {
	private static final long serialVersionUID = 853252585392949700L;
	
	public TianXiaVO(){}
	
	public TianXiaVO(String spSerialno, String tranAmt, String acctName, String acctId, String acctType, String mobile, String bankName, 
			String bankSettleNo, String bankBranchName, String businessNo, String memo){
		this.spSerialno = spSerialno;
		this.tranAmt = tranAmt;
		this.acctName = acctName;
		this.acctId = acctId;
		this.acctType = acctType;
		this.mobile = mobile;
		this.bankName = bankName;
		this.bankSettleNo = bankSettleNo;
		this.bankBranchName = bankBranchName;
		this.businessNo = businessNo;
		this.memo = memo;
	}
	
	/**
	 * 订单号 sp_serialno
	 */
	private String spSerialno;
	/**
	 * 交易金额(分) tran_amt
	 */
	private String tranAmt;
	/**
	 * 金额类型 cur_type
	 */
	@SuppressWarnings("unused")
	private String curType;
	/**
	 * 收款人姓名 acct_name
	 */
	private String acctName;
	/**
	 * 收款人帐号 acct_id
	 */
	private String acctId;
	/**
	 * 账号类型(0-借记卡，1-贷记卡，2-对公账号) acct_type
	 */
	private String acctType;
	/**
	 * 收款人手机号码
	 */
	private String mobile;
	/**
	 * 开户行名称 bank_name
	 */
	private String bankName;
	/**
	 * 开户行支行联行号 bank_settle_no
	 */
	private String bankSettleNo;
	/**
	 * 支行名称 bank_branch_name
	 */
	private String bankBranchName;
	/**
	 * 业务号码 business_no
	 */
	private String businessNo;
	/**
	 * 备注 memo
	 */
	private String memo;
	
	public String getSpSerialno() {
		return spSerialno;
	}
	
	public void setSpSerialno(String spSerialno) {
		this.spSerialno = spSerialno;
	}
	
	public String getTranAmt() {
		return tranAmt;
	}
	
	public void setTranAmt(String tranAmt) {
		this.tranAmt = tranAmt;
	}
	
	public String getAcctName() {
		return acctName;
	}
	
	public void setAcctName(String acctName) {
		this.acctName = acctName;
	}
	
	public String getAcctId() {
		return acctId;
	}
	
	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}
	
	public String getAcctType() {
		return acctType;
	}
	
	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}
	
	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	public String getBankName() {
		return bankName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getBankSettleNo() {
		return bankSettleNo;
	}
	
	public void setBankSettleNo(String bankSettleNo) {
		this.bankSettleNo = bankSettleNo;
	}
	
	public String getBankBranchName() {
		return bankBranchName;
	}
	
	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}
	
	public String getBusinessNo() {
		return businessNo;
	}
	
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	
	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	public String getCurType() {
		return "1";
	}
}
