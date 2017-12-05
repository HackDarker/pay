package com.pay.business.util.mail;


public class MailRun{
	
	public static void send(String dictName,String errorMsg,String orderNum
			,String rateId,String appName,String payMoney,String companyName
			,String rateCompanyName,String payWayName){
		
		MailThread mt = new MailThread(errorMsg, dictName, orderNum, rateId, appName
				, payMoney, companyName, rateCompanyName, payWayName);
		mt.start();
	}
	//app审核成功
	public static void appSend(String companyName,String appName,String companyMail){
		appMailThread mt = new appMailThread(companyName,appName,companyMail);
		mt.start();
	}
	//app审核失败
	public static void appSend(String companyName,String appName,String fileReason,String companyMail){
		appMailThread mt = new appMailThread(companyName,appName,fileReason,companyMail);
		mt.start();
	}
	//商户审核成功
	public static void companySend(String companyName,String companyMail){
		appMailThread mt = new appMailThread(companyName,null,companyMail);
		mt.start();
	}
	//商户审核失败
	public static void companySend(String companyName,String fileReason,String companyMail){
		appMailThread mt = new appMailThread(companyName,null,fileReason,companyMail);
		mt.start();
	}
	
	public static void main(String[] args) {
////		appSend("集团公司","超级应用","zhangheng@aijinfu.cn;1750229682@qq.com;895931894@qq.com;luodan@aijinfu.cn");
//		appSend("集团公司","超级应用","你太帅","zhangheng@aijinfu.cn;1750229682@qq.com;895931894@qq.com;luodan@aijinfu.cn");
////		companySend("集团公司","zhangheng@aijinfu.cn;1750229682@qq.com;895931894@qq.com;luodan@aijinfu.cn");
//		companySend("集团公司","你太帅","zhangheng@aijinfu.cn;1750229682@qq.com;895931894@qq.com;luodan@aijinfu.cn");
//		appSend("集团公司","超级应用","zhangheng@aijinfu.cn;1750229682@qq.com");
//		appSend("集团公司","超级应用","你太帅","zhangheng@aijinfu.cn;1750229682@qq.com");
//		companySend("集团公司","zhangheng@aijinfu.cn;1750229682@qq.com");
		companySend("集团公司","你太帅","1750229682@qq.com;zhangheng@aijinfu.cn");
	}
}
