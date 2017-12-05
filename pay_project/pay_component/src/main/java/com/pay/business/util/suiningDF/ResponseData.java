package com.pay.business.util.suiningDF;

/**
 * @Title: BankPayData.java
 * @Package com.pay.business.util.suining
 * @Description: XML 响应报文:
 * @author ZHOULIBO
 * @date 2017年10月11日 下午4:56:43
 * @version V1.0
 */
public class ResponseData {
	private ResponseHead Head;
	private Body Body;
	private String Signature;
	public ResponseHead getHead() {
		return Head;
	}
	public void setHead(ResponseHead head) {
		Head = head;
	}
	public Body getBody() {
		return Body;
	}
	public void setBody(Body body) {
		Body = body;
	}
	public String getSignature() {
		return Signature;
	}
	public void setSignature(String signature) {
		Signature = signature;
	}
	
}
