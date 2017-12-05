package com.pay.business.util.alipayZz;

public class ResponseBean {
	public String getP1_usercode() {
		return p1_usercode;
	}

	public void setP1_usercode(String p1_usercode) {
		this.p1_usercode = p1_usercode;
	}

	public String getP2_order() {
		return p2_order;
	}

	public void setP2_order(String p2_order) {
		this.p2_order = p2_order;
	}

	public String getP3_money() {
		return p3_money;
	}

	public void setP3_money(String p3_money) {
		this.p3_money = p3_money;
	}

	public String getP4_status() {
		return p4_status;
	}

	public void setP4_status(String p4_status) {
		this.p4_status = p4_status;
	}

	public String getP5_jtpayorder() {
		return p5_jtpayorder;
	}

	public void setP5_jtpayorder(String p5_jtpayorder) {
		this.p5_jtpayorder = p5_jtpayorder;
	}

	public String getP6_paymethod() {
		return p6_paymethod;
	}

	public void setP6_paymethod(String p6_paymethod) {
		this.p6_paymethod = p6_paymethod;
	}

	public String getP7_paychannelnum() {
		return p7_paychannelnum;
	}

	public void setP7_paychannelnum(String p7_paychannelnum) {
		this.p7_paychannelnum = p7_paychannelnum;
	}

	public String getP8_charset() {
		return p8_charset;
	}

	public void setP8_charset(String p8_charset) {
		this.p8_charset = p8_charset;
	}

	public String getP9_signtype() {
		return p9_signtype;
	}

	public void setP9_signtype(String p9_signtype) {
		this.p9_signtype = p9_signtype;
	}

	public String getP10_sign() {
		return p10_sign;
	}

	public void setP10_sign(String p10_sign) {
		this.p10_sign = p10_sign;
	}

	public String getP11_remark() {
		return p11_remark;
	}

	public void setP11_remark(String p11_remark) {
		this.p11_remark = p11_remark;
	}

	// / <summary>
	// / 商户号
	// / </summary>
	public String p1_usercode;
	// / <summary>
	// / 订单号
	// / </summary>
	public String p2_order;
	// / <summary>
	// / 订单金额
	// / </summary>
	public String p3_money;
	// / <summary>
	// / 支付结果
	// / </summary>
	public String p4_status;

	// / <summary>
	// / 竣付通订单号
	// / </summary>
	public String p5_jtpayorder;
	// / <summary>
	// / 商户支付方式
	// / </summary>
	public String p6_paymethod;
	// / <summary>
	// / 支付通道编码(银行,卡类编码)
	// / </summary>
	public String p7_paychannelnum;
	// / <summary>
	// / 编码方式
	// / </summary>
	public String p8_charset;
	// / <summary>
	// / 签名验证方式
	// / </summary>
	public String p9_signtype;
	// / <summary>
	// / 签名
	// / </summary>
	public String p10_sign;
	// / <summary>
	// / 备注
	// / </summary>
	public String p11_remark;
}
