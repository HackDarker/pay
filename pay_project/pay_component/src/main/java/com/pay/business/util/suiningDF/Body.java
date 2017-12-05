package com.pay.business.util.suiningDF;
/**
 * 
* @Title: Body.java 
* @Package com.pay.business.util.suining 
* @Description: xml Body
* @author ZHOULIBO   
* @date 2017年10月11日 下午4:32:02 
* @version V1.0
 */
public class Body {
	private String merchcode;//商户代码 Y
	private String msgtype;//交易标志 Y
	private String txcode;//交易码 Y
	private String hvbrno;//本行清算行号 Y
	private String sw_tx_code;//渠道 Y
	private String txtpcd;//业务类型 Y
	private String txamt;//转账金额 Y
	private String dbtrnm;//付款账户名称 Y
	private String dbtrid;//付款账户 Y
	private String dbtrissr;//付款行名称 Y
	private String dbtrtype;//付款账户类型 Y
	private String dbtrbrnchid;//付款行所属网银系统行号 Y
	private String cash_opn_br_no;//收款行所属网银系统行号 Y
	private String cdtrnm;//收款户名 Y
	private String cdtrid;//收款账号 Y
	private String ctgypurpcd;//业务种类 Y
	private String remark;//备注	N
	private String usage;//用途 Y
	private String tel;//柜员 Y
	private String passwd;//密码 N
	public String getMerchcode() {
		return merchcode;
	}
	public void setMerchcode(String merchcode) {
		this.merchcode = merchcode;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getTxcode() {
		return txcode;
	}
	public void setTxcode(String txcode) {
		this.txcode = txcode;
	}
	public String getHvbrno() {
		return hvbrno;
	}
	public void setHvbrno(String hvbrno) {
		this.hvbrno = hvbrno;
	}
	public String getSw_tx_code() {
		return sw_tx_code;
	}
	public void setSw_tx_code(String sw_tx_code) {
		this.sw_tx_code = sw_tx_code;
	}
	public String getTxtpcd() {
		return txtpcd;
	}
	public void setTxtpcd(String txtpcd) {
		this.txtpcd = txtpcd;
	}
	public String getTxamt() {
		return txamt;
	}
	public void setTxamt(String txamt) {
		this.txamt = txamt;
	}
	public String getDbtrnm() {
		return dbtrnm;
	}
	public void setDbtrnm(String dbtrnm) {
		this.dbtrnm = dbtrnm;
	}
	public String getDbtrid() {
		return dbtrid;
	}
	public void setDbtrid(String dbtrid) {
		this.dbtrid = dbtrid;
	}
	public String getDbtrissr() {
		return dbtrissr;
	}
	public void setDbtrissr(String dbtrissr) {
		this.dbtrissr = dbtrissr;
	}
	public String getDbtrtype() {
		return dbtrtype;
	}
	public void setDbtrtype(String dbtrtype) {
		this.dbtrtype = dbtrtype;
	}
	public String getDbtrbrnchid() {
		return dbtrbrnchid;
	}
	public void setDbtrbrnchid(String dbtrbrnchid) {
		this.dbtrbrnchid = dbtrbrnchid;
	}
	public String getCash_opn_br_no() {
		return cash_opn_br_no;
	}
	public void setCash_opn_br_no(String cash_opn_br_no) {
		this.cash_opn_br_no = cash_opn_br_no;
	}
	public String getCdtrnm() {
		return cdtrnm;
	}
	public void setCdtrnm(String cdtrnm) {
		this.cdtrnm = cdtrnm;
	}
	public String getCdtrid() {
		return cdtrid;
	}
	public void setCdtrid(String cdtrid) {
		this.cdtrid = cdtrid;
	}
	public String getCtgypurpcd() {
		return ctgypurpcd;
	}
	public void setCtgypurpcd(String ctgypurpcd) {
		this.ctgypurpcd = ctgypurpcd;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
