package com.pay.business.util.suiningDF;
/**
* @Title: Head.java 
* @Package com.pay.business.util.suining 
* @Description: xml  Head：请求报文头
* @author ZHOULIBO   
* @date 2017年10月11日 下午4:32:22 
* @version V1.0
 */
public class RequestHeader {
	private String version;//版本号
	private String instId;//发送方机构代码
	private String certId;//证书id
	private String requestBusiDate;//发送方交易日期 YYYYMMDD
	private String requestTimestamp;//发送方交易时间戳
	private String requestSeqNo;//发送方交易流水号
	private String channelNo;//渠道代码
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
	public String getRequestBusiDate() {
		return requestBusiDate;
	}
	public void setRequestBusiDate(String requestBusiDate) {
		this.requestBusiDate = requestBusiDate;
	}
	public String getRequestTimestamp() {
		return requestTimestamp;
	}
	public void setRequestTimestamp(String requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}
	public String getRequestSeqNo() {
		return requestSeqNo;
	}
	public void setRequestSeqNo(String requestSeqNo) {
		this.requestSeqNo = requestSeqNo;
	}
	public String getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
}
