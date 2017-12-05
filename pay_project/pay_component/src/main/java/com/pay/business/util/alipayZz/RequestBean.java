package com.pay.business.util.alipayZz;

public class RequestBean {

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
	// / 明文跳转
	// / </summary>
	public String p4_returnurl;

	// / <summary>
	// / 服务器异步通知地址
	// / </summary>
	public String p5_notifyurl;
	// / <summary>
	// / 订单时间 格式:yyyymmddhhmmss
	// / </summary>
	public String p6_ordertime;
	// / <summary>
	// / 签名
	// / </summary>
	public String p7_sign;
	// / <summary>
	// / 签名方式 (非必填)
	// / </summary>
	public String p8_signtype;
	// / <summary>
	// / 支付方式:1:网银 2:快捷 3:微信 4:支付宝 5:游戏点卡 6:竣付通账户 7:预付费卡 8:人民币 9:授信额度
	// / </summary>
	public String p9_paymethod;
	// / <summary>
	// / 支付通道编码(银行编码或卡类编码)
	// / </summary>
	public String p10_paychannelnum;
	// / <summary>
	// / 产品名称
	// / </summary>
	public String p14_customname;
	public String p17_customip;
	public String p25_terminal;
	public String p26_iswappay;
	public String p18_product;
	public String p19_productcat;
	public String p20_productnum;

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

	public String getP4_returnurl() {
		return p4_returnurl;
	}

	public void setP4_returnurl(String p4_returnurl) {
		this.p4_returnurl = p4_returnurl;
	}

	public String getP5_notifyurl() {
		return p5_notifyurl;
	}

	public void setP5_notifyurl(String p5_notifyurl) {
		this.p5_notifyurl = p5_notifyurl;
	}

	public String getP6_ordertime() {
		return p6_ordertime;
	}

	public void setP6_ordertime(String p6_ordertime) {
		this.p6_ordertime = p6_ordertime;
	}

	public String getP7_sign() {
		return p7_sign;
	}

	public void setP7_sign(String p7_sign) {
		this.p7_sign = p7_sign;
	}

	public String getP8_signtype() {
		return p8_signtype;
	}

	public void setP8_signtype(String p8_signtype) {
		this.p8_signtype = p8_signtype;
	}

	public String getP9_paymethod() {
		return p9_paymethod;
	}

	public void setP9_paymethod(String p9_paymethod) {
		this.p9_paymethod = p9_paymethod;
	}

	public String getP10_paychannelnum() {
		return p10_paychannelnum;
	}

	public void setP10_paychannelnum(String p10_paychannelnum) {
		this.p10_paychannelnum = p10_paychannelnum;
	}

	public String getP14_customname() {
		return p14_customname;
	}
	public void setP14_customname(String p14_customname){
		this.p14_customname = p14_customname;
	}

	public String getP17_customip() {
		return p17_customip;
	}

	public void setP17_customip(String p17_customip) {
		this.p17_customip = p17_customip;
	}

	public String getP18_product() {
		return p18_product;
	}

	public void setP18_product(String p18_product) {
		this.p18_product = p18_product;
	}
	
	public String getP19_productcat(){
		return p19_productcat;
	}
	
	public void setP19_productcat(String p19_productcat){
		this.p19_productcat = p19_productcat;
	}
	
	public String getP20_productnum(){
		return p20_productnum;
	}
	public void setP20_productnum(String p20_productnum){
		this.p20_productnum = p20_productnum;
	}

	public String getP25_terminal() {
		return p25_terminal;
	}

	public void setP25_terminal(String p25_terminal) {
		this.p25_terminal = p25_terminal;
	}

	public String getP26_iswappay() {
		return p26_iswappay;
	}

	public void setP26_iswappay(String p26_iswappay) {
		this.p26_iswappay = p26_iswappay;
	}

	// 转换为post数据
	public String toString() {
		String str = "p1_usercode=" + p1_usercode + "&p2_order=" + p2_order
				+ "&p3_money=" + p3_money + "&p4_returnurl=" + p4_returnurl
				+ "&p5_notifyurl=" + p5_notifyurl + "&p6_ordertime="
				+ p6_ordertime + "&p7_sign=" + p7_sign + "&p8_signtype="
				+ p8_signtype + "&p9_paymethod=" + p9_paymethod
				+ "&p10_paychannelnum=" + p10_paychannelnum;
		return str;
	}
}
