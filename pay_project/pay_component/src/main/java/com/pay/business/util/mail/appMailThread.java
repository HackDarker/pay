package com.pay.business.util.mail;

import com.core.teamwork.base.util.ReadProChange;



public class appMailThread extends Thread{
	private String appName;		//应用名称
	private String companyName;	//商户名称	
	private String fileReason;	//失败理由
	private String companyMail;	//商户邮箱
//	private String mailName = ReadProChange.getValue("mail_name");
//	private String mailForm = ReadProChange.getValue("zhangheng@aijinfu.cn");
	
	//审核通过
	public appMailThread(String companyName,String appName,String companyMail){
		this.appName=appName;
		this.companyMail=companyMail;
		this.companyName=companyName;
	}
	//审核失败
	public appMailThread(String companyName,String appName,String fileReason,String companyMail){
		this.appName=appName;
		this.fileReason=fileReason;
		this.companyMail=companyMail;
		this.companyName=companyName;
	}
	public void run(){

		SslSmtpMailUtil ms = new SslSmtpMailUtil();
        ms.setSubject("全民金服审核结果");
        StringBuffer buffer = new StringBuffer();
	    if(fileReason!=null){
	    	buffer.append("<div class='con' style='width: 100%;height: 100%;'>"
	    			+ "<div class='main' style='width: 600px;height: 400px;margin: 0 auto;margin-top: 20px;'>"
	    			+ "<div class='title' style='width: 100%;height: 44px;line-height: 44px;background-color: #99caff;"
	    			+ "border-radius: 10px 10px 0 0;color: #fff;font-size: 22px;text-indent: 15px;'>审核通知</div>"
	    			+ "<div class='content' style='width: 100%;background: #fff;overflow: hidden;border-right: 1px solid #ccc;"
	    			+ "border-left: 1px solid #ccc;box-sizing: border-box;'><div class='main-con' style='margin-left:50px;"
	    			+ "height: 64px;margin-top: 90px;margin-bottom: 45px;'><img style='display: block;width: 64px;"
	    			+ "height: 64px;float: left;' class='img1' src='http://testfs.aijinfu.cn/zfj/1504746764344/refuse.png' />"
	    			+ "<div class='detail' style='float: left;padding-left: 16px;line-height: 26px;margin-top: 8px;font-size: 16px;'>"
	    			+ "<p style='margin:0;'>尊敬的<span style='font-weight: bold'>"+companyName+"</sapn></p>");
	    	if(appName!=null){
	        	buffer.append("<p style='margin:0;'>很抱歉，你的应用:<span style='font-weight: bold'>"+appName+"</span>，没有通过本次审核</p>");
	        }else{
	        	buffer.append("<p style='margin:0;'>很抱歉，没有通过本次审核</p>");
	        }
	    	buffer.append("</div><div style='clear: both;'></div></div><div class='line' style='width: 580px;height: 1px;background-color:"
	    			+ " #ccc;margin-left: 10px;margin-bottom: 30px;'></div><div class='cause' style='overflow:hidden;font-size: 20px;margin-bottom: "
	    			+ "110px;color: red;'><div class='tt fl' style='margin-left:124px;float:left;'>失败原因：</div><div class='ca fl' "
	    			+ "style='width: 288px;;float:left;'>"+fileReason+"</div>");
	    }else{
	    	buffer.append("<div class='con' style='width: 100%;height: 100%;'><div class='main' style='width: 600px;height: 400px;margin: 0 auto;margin-top: 20px;'>"
	    			+ "<div class='title' style='width: 100%;height: 44px;line-height: 44px;background-color: #99caff;border-radius: 10px 10px 0 0;color: #fff;font-size: 22px;text-indent: 15px;'>审核通知</div>"
	    			+ "<div class='content' style='width: 100%;background: #fff;overflow: hidden;border-right: 1px solid #ccc;border-left: 1px solid #ccc;box-sizing: border-box;'>"
	    			+ "<div class='main-con' style='width: 80%;position: relative;margin:0 auto;margin-top: 115px;margin-bottom: 100px;'>"
	    			+ "<img style='display: block;width: 64px;height: 64px;float: left;margin-right: 20px;' class='img1' src='http://testfs.aijinfu.cn/zfj/1504746758795/pass.png'>"
	    			+ "<div class='detail' style='padding-top: 10px;padding-left: 16px;line-height: 26px;margin-top: 8px;font-size: 16px;'>"
	    			+ "<p style='margin:0;'>尊敬的<span style='font-weight: bold;'>"+companyName+"</sapn></p>");
	    	if(appName!=null){
	        	buffer.append("<p style='margin:0;'>恭喜您，你的应用:<span style='font-weight: bold'>"+appName+"</span>，成功通过应用资料审核！</p>");
	        }else{
	        	buffer.append("<p style='margin:0;'>恭喜你，成功通过商户资料审核成功通过商户资料审核！</p></div>");
	        }
	    }
	    buffer.append("</div></div></div><div class='foot' style='width: 100%;height: 44px;line-height: 44px;background-color: #99caff;border-radius: 0 0 10px 10px;color: #08376a;font-size: 18px;font-weight: bold;'><div style='width: 220px;margin:0 auto'><img style='display: block;float:left;width: 18px;height: 15px;margin:14px 20px 0 0px;' src='http://testfs.aijinfu.cn/zfj/1504747073418/tel.png' class='telimg'><span style='border-bottom: 1px dashed rgb(204, 204, 204); z-index: 1; position: static;'  data='0755-21612184'>0755-21612184</span></div></div></div>");
        //ms.setText(buffer.toString());
		ms.setContent(buffer.toString(), "text/html; charset=utf-8");
        ms.setFrom(ReadProChange.getValue("suport_mail"));
//		ms.setFrom("support@aijinfu.cn");
        //String [] mailFroms = mailForm.split("-");
        String [] mailFroms = companyMail.split(";");
        ms.setRecipients(mailFroms, "TO");
        System.out.println(buffer);
        ms.setSentDate();
        ms.sendMail();
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyMail() {
		return companyMail;
	}
	public void setCompanyMail(String companyMail) {
		this.companyMail = companyMail;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getFileReason() {
		return fileReason;
	}
	public void setFileReason(String fileReason) {
		this.fileReason = fileReason;
	}
}
