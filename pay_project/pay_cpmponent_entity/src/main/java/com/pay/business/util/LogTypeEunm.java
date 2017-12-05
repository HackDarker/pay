package com.pay.business.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum LogTypeEunm {
    T1(1, "增加支付平台"),
    T2(2, "停用支付平台"),
    T3(3, "启用支付平台"),
    T4(4, "删除支付平台"),
    T5(5, "修改支付平台信息"),
    
    T6(6, "增加支付通道"),
    T7(7, "删除支付通道"),
    T8(8, "修改支付通道信息"),
    T9(9, "开启支付通道"),
    T10(10, "关闭支付通道"),
    
    T11(11, "增加渠道商"),
    T12(12, "修改渠道商信息"),
    T13(13, "开启渠道商"),
    T14(14, "关闭渠道商"),
    T15(15, "删除渠道商"),
  
    T16(16, "审核商户"),
    T17(17, "终止商户合作"),
    T18(18, "恢复商户合作"),
    T19(19, "配置商户通道"),
    
    T20(20, "审核app"),
    T21(21, "终止app合作"),
    T22(22, "恢复app合作"),
   
    T23(23, "增加SDK"),
    T24(24, "删除SDK"),
    T25(25, "修改SDK信息"),
    T26(26, "上线SDK"),
    T27(27, "下线SDK"),
    
    T28(28, "修改客户信息"),
    T29(29, "删除客户信息"),
    T30(30, "订单对账"),
    T31(31, "订单平账"),
    T32(32, "订单出账"),
    T33(33, "生成日账单"),
    T34(34, "结算账单"),

    T35(35, "新增代付通道"),
    T36(36, "修改代付通道"),
    T37(37, "开启代付通道"),
    T38(38, "关闭代付通道"),
    T39(39, "删除代付通道"),

    T40(40, "为商户配置代付通道"),
    T41(41, "修改商户代付通道"),
    T42(42, "删除商户代付通道"),

    T43(43, "代付资金池充值"),

    T44(44, "停用商户代付通道"),
    T45(45, "启用商户代付通道"),

    T46(46, "D0通道自动发起提现"),
    T47(47, "D0通道自动提现回调"),

    T48(48, "新增渠道商支持的支付方式"),
    T49(49, "修改渠道商支付方式费率"),
    T50(50, "删除渠道商支付方式"),
    
    T51(51, "新增商户支持的支付方式"),
    T52(52, "修改商户支付方式费率"),
    T53(53, "删除商户支付方式"),

    T54(54, "批量分配通道给商户"),
    T55(55, "批量新增支付通道"),
    
    T56(56, "新增支付方式"),
    T57(57, "修改支付方式"),
    
    T58(58, "删除商户"),
    
    T59(59, "为渠道配置代付费"),
    T60(60, "为商户配置代付费"),
    
    T61(61, "添加接口商户信息"),
    T62(62, "修改接口商户信息"),
    T63(63, "添加接口充值记录"),
    
    T64(64, "修改支付密码"),
    T65(65, "修改登录密码"),
    
    T66(66, "删除商户收款码"),
    T67(67, "添加商户收款码")
    ;

    public static List<Map<String, Object>> getAllType(){
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("id", T1.getTypeNum());
    	map.put("name", T1.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T2.getTypeNum());
    	map.put("name", T2.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T3.getTypeNum());
    	map.put("name", T3.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T4.getTypeNum());
    	map.put("name", T4.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T5.getTypeNum());
    	map.put("name", T5.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T6.getTypeNum());
    	map.put("name", T6.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T7.getTypeNum());
    	map.put("name", T7.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T8.getTypeNum());
    	map.put("name", T8.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T9.getTypeNum());
    	map.put("name", T9.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T10.getTypeNum());
    	map.put("name", T10.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T11.getTypeNum());
    	map.put("name", T11.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T12.getTypeNum());
    	map.put("name", T12.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T13.getTypeNum());
    	map.put("name", T13.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T14.getTypeNum());
    	map.put("name", T14.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T15.getTypeNum());
    	map.put("name", T15.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T16.getTypeNum());
    	map.put("name", T16.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T17.getTypeNum());
    	map.put("name", T17.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T18.getTypeNum());
    	map.put("name", T18.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T19.getTypeNum());
    	map.put("name", T19.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T20.getTypeNum());
    	map.put("name", T20.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T21.getTypeNum());
    	map.put("name", T21.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T22.getTypeNum());
    	map.put("name", T22.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T23.getTypeNum());
    	map.put("name", T23.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T24.getTypeNum());
    	map.put("name", T24.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T25.getTypeNum());
    	map.put("name", T25.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T26.getTypeNum());
    	map.put("name", T26.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T27.getTypeNum());
    	map.put("name", T27.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T28.getTypeNum());
    	map.put("name", T28.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T29.getTypeNum());
    	map.put("name", T29.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T30.getTypeNum());
    	map.put("name", T30.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T31.getTypeNum());
    	map.put("name", T31.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T32.getTypeNum());
    	map.put("name", T32.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T33.getTypeNum());
    	map.put("name", T33.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T34.getTypeNum());
    	map.put("name", T34.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T35.getTypeNum());
    	map.put("name", T35.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T36.getTypeNum());
    	map.put("name", T36.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T37.getTypeNum());
    	map.put("name", T37.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T38.getTypeNum());
    	map.put("name", T38.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T39.getTypeNum());
    	map.put("name", T39.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T40.getTypeNum());
    	map.put("name", T40.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T41.getTypeNum());
    	map.put("name", T41.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T42.getTypeNum());
    	map.put("name", T42.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T43.getTypeNum());
    	map.put("name", T43.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T44.getTypeNum());
    	map.put("name", T44.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T45.getTypeNum());
    	map.put("name", T45.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T46.getTypeNum());
    	map.put("name", T46.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T47.getTypeNum());
    	map.put("name", T47.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T48.getTypeNum());
    	map.put("name", T48.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T49.getTypeNum());
    	map.put("name", T49.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T50.getTypeNum());
    	map.put("name", T50.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T51.getTypeNum());
    	map.put("name", T51.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T52.getTypeNum());
    	map.put("name", T52.getTypeName());
    	list.add(map);
    	map = new HashMap<String, Object>();
    	map.put("id", T53.getTypeNum());
    	map.put("name", T53.getTypeName());
    	list.add(map);

    	map = new HashMap<String, Object>();
    	map.put("id", T54.getTypeNum());
    	map.put("name", T54.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T55.getTypeNum());
    	map.put("name", T55.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T56.getTypeNum());
    	map.put("name", T56.getTypeName());
    	list.add(map);    	
    	map = new HashMap<String, Object>();
    	map.put("id", T57.getTypeNum());
    	map.put("name", T57.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T58.getTypeNum());
    	map.put("name", T58.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T59.getTypeNum());
    	map.put("name", T59.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T60.getTypeNum());
    	map.put("name", T60.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T61.getTypeNum());
    	map.put("name", T61.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T62.getTypeNum());
    	map.put("name", T62.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T63.getTypeNum());
    	map.put("name", T63.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T64.getTypeNum());
    	map.put("name", T64.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T65.getTypeNum());
    	map.put("name", T65.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T66.getTypeNum());
    	map.put("name", T66.getTypeName());
    	list.add(map);
    	
    	map = new HashMap<String, Object>();
    	map.put("id", T67.getTypeNum());
    	map.put("name", T67.getTypeName());
    	list.add(map);
    	return list;
    }
    
    private int typeNum;

    private String typeName;

    public int getTypeNum() {
		return typeNum;
	}

	public void setTypeNum(int typeNum) {
		this.typeNum = typeNum;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	private LogTypeEunm(int typeNum, String typeName) {
    	this.typeNum = typeNum;
    	this.typeName = typeName;
    }
	public static void main(String[] args) {
		for (int i = 1; i < 35; i++) {
			System.out.println("map = new HashMap<String, Object>();");
			System.out.println("map.put(\"id\", T"+i+".getTypeNum());");
			System.out.println("map.put(\"name\", T"+i+".getTypeName());");
			System.out.println("list.add(map);");
		}
	}

}
