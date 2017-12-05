package com.pay.business.merchant.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.payv2_channel            
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
channel_key          String(64)                             //渠道商Key
channel_name         String(50)                             //渠道商名称
company_name         String(50)                             //公司名称
channel_addr         String(100)                            //地址
license_num          String(100)                            //营业执照号码
license_pic          String(255)                            //营业执照
organization_code    String(64)                             //组织机构代码
organization_code_url String(255)                            //组织机构代码照
legal_name           String(20)                             //公司法人名称
legal_id_card        String(18)                             //法人身份证号码
posit_card           String(255)                            //身份证正面图地址
negat_card           String(255)                            //身份证反面图地址
channel_contact_name String(50)                             //联系人姓名
channel_contact_phone String(12)                             //联系电话
channel_status       Integer(10)                 1          //当前状态,1合作中,2终止合作
channel_login_name   String(20)                             //后台登录帐号
channel_login_pwd    String(64)                             //登录密码
bank_name            String(100)                            //开户行
bank_type            Integer(10)                 1          //账号类型1.企业账号2.个人账号
bank_card            String(30)                             //开户账号
bank_user            String(50)                             //开户人
channel_rebate       Double(5,2)                 0.00       //返点佣金比例，，单位百分号,0表示底价
pay_way_rate         Double(5,2)                 0.00       //渠道代付费（每笔/元）
is_add_company       Integer(10)                 2          //是否允许添加商户1.是2.否
is_add_platform      Integer(10)                 2          //是否允许代理平台1.是2.否
is_delete            Integer(10)                 2          //是否删除,1是,2否
create_time          Date(19)                               //
update_time          Date(19)                               //
*/
public class Payv2Channel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	String channelKey;
	private	String channelName;
	private	String companyName;
	private	String channelAddr;
	private	String licenseNum;
	private	String licensePic;
	private	String organizationCode;
	private	String organizationCodeUrl;
	private	String legalName;
	private	String legalIdCard;
	private	String positCard;
	private	String negatCard;
	private	String channelContactName;
	private	String channelContactPhone;
	private	Integer channelStatus;
	private	String channelLoginName;
	private	String channelLoginPwd;
	private	String bankName;
	private	Integer bankType;
	private	String bankCard;
	private	String bankUser;
	private	Double channelRebate;
	private	Double payWayRate;
	private	Integer isAddCompany;
	private	Integer isAddPlatform;
	private	Integer isDelete;
	private	Date createTime;
	private	Date updateTime;
	
	private String platformName;//代理平台名称
	private Double platformComm;//平台佣金
	private Long platformId;//代理平台Id

	/**
	* id  Long(19)  NOTNULL  //    
	*/
	public Long getId(){
		return id;
	}
	
	/**
	* id  Long(19)  NOTNULL  //    
	*/
	public void setId(Long id){
		this.id = id;
	}
	
	/**
	* channel_key  String(64)  //渠道商Key    
	*/
	public String getChannelKey(){
		return channelKey;
	}
	
	/**
	* channel_key  String(64)  //渠道商Key    
	*/
	public void setChannelKey(String channelKey){
		this.channelKey = channelKey;
	}
	
	/**
	* channel_name  String(50)  //渠道商名称    
	*/
	public String getChannelName(){
		return channelName;
	}
	
	/**
	* channel_name  String(50)  //渠道商名称    
	*/
	public void setChannelName(String channelName){
		this.channelName = channelName;
	}
	
	/**
	* channel_addr  String(100)  //地址    
	*/
	public String getChannelAddr(){
		return channelAddr;
	}
	
	/**
	* channel_addr  String(100)  //地址    
	*/
	public void setChannelAddr(String channelAddr){
		this.channelAddr = channelAddr;
	}
	
	/**
	* channel_contact_name  String(50)  //联系人姓名    
	*/
	public String getChannelContactName(){
		return channelContactName;
	}
	
	/**
	* channel_contact_name  String(50)  //联系人姓名    
	*/
	public void setChannelContactName(String channelContactName){
		this.channelContactName = channelContactName;
	}
	
	/**
	* channel_contact_phone  String(12)  //联系电话    
	*/
	public String getChannelContactPhone(){
		return channelContactPhone;
	}
	
	/**
	* channel_contact_phone  String(12)  //联系电话    
	*/
	public void setChannelContactPhone(String channelContactPhone){
		this.channelContactPhone = channelContactPhone;
	}
	
	/**
	* channel_status  Integer(10)  1  //当前状态,1合作中,2终止合作    
	*/
	public Integer getChannelStatus(){
		return channelStatus;
	}
	
	/**
	* channel_status  Integer(10)  1  //当前状态,1合作中,2终止合作    
	*/
	public void setChannelStatus(Integer channelStatus){
		this.channelStatus = channelStatus;
	}
	
	/**
	* channel_login_name  String(20)  //后台登录帐号    
	*/
	public String getChannelLoginName(){
		return channelLoginName;
	}
	
	/**
	* channel_login_name  String(20)  //后台登录帐号    
	*/
	public void setChannelLoginName(String channelLoginName){
		this.channelLoginName = channelLoginName;
	}
	
	/**
	* channel_login_pwd  String(64)  //登录密码    
	*/
	public String getChannelLoginPwd(){
		return channelLoginPwd;
	}
	
	/**
	* channel_login_pwd  String(64)  //登录密码    
	*/
	public void setChannelLoginPwd(String channelLoginPwd){
		this.channelLoginPwd = channelLoginPwd;
	}
	
	/**
	* channel_rebate  Double(5,2)  0.00  //返点佣金比例，，单位千分号    
	*/
	public Double getChannelRebate(){
		return channelRebate;
	}
	
	/**
	* channel_rebate  Double(5,2)  0.00  //返点佣金比例，，单位千分号    
	*/
	public void setChannelRebate(Double channelRebate){
		this.channelRebate = channelRebate;
	}
	
	/**
	* is_add_company  Integer(10)  //是否允许添加商户1.是2.否    
	*/
	public Integer getIsAddCompany(){
		return isAddCompany;
	}
	
	/**
	* is_add_company  Integer(10)  //是否允许添加商户1.是2.否    
	*/
	public void setIsAddCompany(Integer isAddCompany){
		this.isAddCompany = isAddCompany;
	}
	
	/**
	* is_add_platform  Integer(10)  //是否允许代理平台1.是2.否    
	*/
	public Integer getIsAddPlatform(){
		return isAddPlatform;
	}
	
	/**
	* is_add_platform  Integer(10)  //是否允许代理平台1.是2.否    
	*/
	public void setIsAddPlatform(Integer isAddPlatform){
		this.isAddPlatform = isAddPlatform;
	}
	
	/**
	* is_delete  Integer(10)  2  //是否删除,1是,2否    
	*/
	public Integer getIsDelete(){
		return isDelete;
	}
	
	/**
	* is_delete  Integer(10)  2  //是否删除,1是,2否    
	*/
	public void setIsDelete(Integer isDelete){
		this.isDelete = isDelete;
	}
	
	/**
	* create_time  Date(19)  //    
	*/
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
	* create_time  Date(19)  //    
	*/
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	
	/**
	* update_time  Date(19)  //    
	*/
	public Date getUpdateTime(){
		return updateTime;
	}
	
	/**
	* update_time  Date(19)  //    
	*/
	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public Double getPlatformComm() {
		return platformComm;
	}

	public void setPlatformComm(Double platformComm) {
		this.platformComm = platformComm;
	}

	public Long getPlatformId() {
		return platformId;
	}

	public void setPlatformId(Long platformId) {
		this.platformId = platformId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Integer getBankType() {
		return bankType;
	}

	public void setBankType(Integer bankType) {
		this.bankType = bankType;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBankUser() {
		return bankUser;
	}

	public void setBankUser(String bankUser) {
		this.bankUser = bankUser;
	}

	public Double getPayWayRate() {
		return payWayRate;
	}

	public void setPayWayRate(Double payWayRate) {
		this.payWayRate = payWayRate;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getLicenseNum() {
		return licenseNum;
	}

	public void setLicenseNum(String licenseNum) {
		this.licenseNum = licenseNum;
	}

	public String getLicensePic() {
		return licensePic;
	}

	public void setLicensePic(String licensePic) {
		this.licensePic = licensePic;
	}

	public String getOrganizationCode() {
		return organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getOrganizationCodeUrl() {
		return organizationCodeUrl;
	}

	public void setOrganizationCodeUrl(String organizationCodeUrl) {
		this.organizationCodeUrl = organizationCodeUrl;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getLegalIdCard() {
		return legalIdCard;
	}

	public void setLegalIdCard(String legalIdCard) {
		this.legalIdCard = legalIdCard;
	}

	public String getPositCard() {
		return positCard;
	}

	public void setPositCard(String positCard) {
		this.positCard = positCard;
	}

	public String getNegatCard() {
		return negatCard;
	}

	public void setNegatCard(String negatCard) {
		this.negatCard = negatCard;
	}

	
}