package com.pay.business.util.suiningDF;
/**
* @Title: Head.java 
* @Package com.pay.business.util.suining 
* @Description: xml  Head：响应报文头
* @author ZHOULIBO   
* @date 2017年10月11日 下午4:32:22 
* @version V1.0
 */
public class ResponseHead {
	private String version;//版本号			y
	private String instId;//发送方机构代码		y
	private String certId;//证书id			y
	private String retCode;//返回码 成功00		y
	private String retMessage;//失败返回		n
	private String responseSeqNo;//返回流水号	y
	private String tranDate;//交易时间			y
	private String tranTimestamp;//交易时间戳	n
	private String channelCode;//渠道代码		n
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getInstId() {
		return instId;
	}
	public void setInstId(String instId) {
		this.instId = instId;
	}
	public String getCertId() {
		return certId;
	}
	public void setCertId(String certId) {
		this.certId = certId;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getRetMessage() {
		return retMessage;
	}
	public void setRetMessage(String retMessage) {
		this.retMessage = retMessage;
	}
	public String getResponseSeqNo() {
		return responseSeqNo;
	}
	public void setResponseSeqNo(String responseSeqNo) {
		this.responseSeqNo = responseSeqNo;
	}
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getTranTimestamp() {
		return tranTimestamp;
	}
	public void setTranTimestamp(String tranTimestamp) {
		this.tranTimestamp = tranTimestamp;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
}
