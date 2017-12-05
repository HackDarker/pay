package com.pay.business.df.entity;

import java.io.Serializable;
import java.util.Date;

/**
TABLE:.df_pay_way_rate          
--------------------------------------------------------
id                   Long(19)           NOTNULL             //
df_pay_way_name      String(50)                             //代付通道名称（由支付平台和主键生成，下游看）
df_rate_name         String(50)                             //代付通道名称（自动生成，官方看）
dic_id               Long(19)                               //接入代付的类型，关联字典表sys_config_dictionary表
remark               String(65535)                          //备注
company_name         String(50)                             //所属机构名称
company_alias        String(50)                             //商户简称
total_amount         Double(16,2)                0.00       //资金池总额
pay_way_rate         Double(9,2)                 0.00       //代付成本每笔/元
rate_key1            String(100)                            //第三方通道字段1,如支付宝app_id
rate_key2            String(4000)                           //第三方通道字段2,如支付宝商户私钥
rate_key3            String(800)                            //第三方通道字段3,如支付宝公钥
rate_key4            String(100)                            //通道字段4
rate_key5            String(65535)                          //通道字段5
rate_key6            String(65535)                          //通道字段6
key_remark           String(65535)                          //通道备注
is_delete            Integer(10)                 2          //是否删除1.是2.否
status               Integer(10)                 1          //状态1.开启2.关闭
create_user_by       Long(19)                               //创建者
update_user_by       Long(19)                               //修改人
create_time          Date(19)                               //创建时间
update_time          Date(19)                               //修改时间
*/
public class DfPayWayRate implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private	Long id;
	private	String dfPayWayName;
	private	String dfRateName;
	private	Long dicId;
	private	String remark;
	private	String companyName;
	private	String companyAlias;
	private	Double totalAmount;
	private	Double payWayRate;
	private	String rateKey1;
	private	String rateKey2;
	private	String rateKey3;
	private	String rateKey4;
	private	String rateKey5;
	private	String rateKey6;
	private	String keyRemark;
	private	Integer isDelete;
	private	Integer status;
	private	Long createUserBy;
	private	Long updateUserBy;
	private	Date createTime;
	private	Date updateTime;

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
	* df_pay_way_name  String(50)  //代付通道名称（由支付平台和主键生成，下游看）    
	*/
	public String getDfPayWayName(){
		return dfPayWayName;
	}
	
	/**
	* df_pay_way_name  String(50)  //代付通道名称（由支付平台和主键生成，下游看）    
	*/
	public void setDfPayWayName(String dfPayWayName){
		this.dfPayWayName = dfPayWayName;
	}
	
	/**
	* df_rate_name  String(50)  //代付通道名称（自动生成，官方看）    
	*/
	public String getDfRateName(){
		return dfRateName;
	}
	
	/**
	* df_rate_name  String(50)  //代付通道名称（自动生成，官方看）    
	*/
	public void setDfRateName(String dfRateName){
		this.dfRateName = dfRateName;
	}
	
	/**
	* dic_id  Long(19)  //接入代付的类型，关联字典表sys_config_dictionary表    
	*/
	public Long getDicId(){
		return dicId;
	}
	
	/**
	* dic_id  Long(19)  //接入代付的类型，关联字典表sys_config_dictionary表    
	*/
	public void setDicId(Long dicId){
		this.dicId = dicId;
	}
	
	/**
	* remark  String(65535)  //备注    
	*/
	public String getRemark(){
		return remark;
	}
	
	/**
	* remark  String(65535)  //备注    
	*/
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	/**
	* company_name  String(50)  //所属机构名称    
	*/
	public String getCompanyName(){
		return companyName;
	}
	
	/**
	* company_name  String(50)  //所属机构名称    
	*/
	public void setCompanyName(String companyName){
		this.companyName = companyName;
	}
	
	/**
	* company_alias  String(50)  //商户简称    
	*/
	public String getCompanyAlias(){
		return companyAlias;
	}
	
	/**
	* company_alias  String(50)  //商户简称    
	*/
	public void setCompanyAlias(String companyAlias){
		this.companyAlias = companyAlias;
	}
	
	/**
	* total_amount  Double(16,2)  0.00  //资金池总额    
	*/
	public Double getTotalAmount(){
		return totalAmount;
	}
	
	/**
	* total_amount  Double(16,2)  0.00  //资金池总额    
	*/
	public void setTotalAmount(Double totalAmount){
		this.totalAmount = totalAmount;
	}
	
	/**
	* pay_way_rate  Double(9,2)  0.00  //代付成本每笔/元    
	*/
	public Double getPayWayRate(){
		return payWayRate;
	}
	
	/**
	* pay_way_rate  Double(9,2)  0.00  //代付成本每笔/元    
	*/
	public void setPayWayRate(Double payWayRate){
		this.payWayRate = payWayRate;
	}
	
	/**
	* rate_key1  String(100)  //第三方通道字段1,如支付宝app_id    
	*/
	public String getRateKey1(){
		return rateKey1;
	}
	
	/**
	* rate_key1  String(100)  //第三方通道字段1,如支付宝app_id    
	*/
	public void setRateKey1(String rateKey1){
		this.rateKey1 = rateKey1;
	}
	
	/**
	* rate_key2  String(4000)  //第三方通道字段2,如支付宝商户私钥    
	*/
	public String getRateKey2(){
		return rateKey2;
	}
	
	/**
	* rate_key2  String(4000)  //第三方通道字段2,如支付宝商户私钥    
	*/
	public void setRateKey2(String rateKey2){
		this.rateKey2 = rateKey2;
	}
	
	/**
	* rate_key3  String(800)  //第三方通道字段3,如支付宝公钥    
	*/
	public String getRateKey3(){
		return rateKey3;
	}
	
	/**
	* rate_key3  String(800)  //第三方通道字段3,如支付宝公钥    
	*/
	public void setRateKey3(String rateKey3){
		this.rateKey3 = rateKey3;
	}
	
	/**
	* rate_key4  String(100)  //通道字段4    
	*/
	public String getRateKey4(){
		return rateKey4;
	}
	
	/**
	* rate_key4  String(100)  //通道字段4    
	*/
	public void setRateKey4(String rateKey4){
		this.rateKey4 = rateKey4;
	}
	
	/**
	* rate_key5  String(65535)  //通道字段5    
	*/
	public String getRateKey5(){
		return rateKey5;
	}
	
	/**
	* rate_key5  String(65535)  //通道字段5    
	*/
	public void setRateKey5(String rateKey5){
		this.rateKey5 = rateKey5;
	}
	
	/**
	* rate_key6  String(65535)  //通道字段6    
	*/
	public String getRateKey6(){
		return rateKey6;
	}
	
	/**
	* rate_key6  String(65535)  //通道字段6    
	*/
	public void setRateKey6(String rateKey6){
		this.rateKey6 = rateKey6;
	}
	
	/**
	* key_remark  String(65535)  //通道备注    
	*/
	public String getKeyRemark(){
		return keyRemark;
	}
	
	/**
	* key_remark  String(65535)  //通道备注    
	*/
	public void setKeyRemark(String keyRemark){
		this.keyRemark = keyRemark;
	}
	
	/**
	* is_delete  Integer(10)  2  //是否删除1.是2.否    
	*/
	public Integer getIsDelete(){
		return isDelete;
	}
	
	/**
	* is_delete  Integer(10)  2  //是否删除1.是2.否    
	*/
	public void setIsDelete(Integer isDelete){
		this.isDelete = isDelete;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启2.关闭    
	*/
	public Integer getStatus(){
		return status;
	}
	
	/**
	* status  Integer(10)  1  //状态1.开启2.关闭    
	*/
	public void setStatus(Integer status){
		this.status = status;
	}
	
	/**
	* create_user_by  Long(19)  //创建者    
	*/
	public Long getCreateUserBy(){
		return createUserBy;
	}
	
	/**
	* create_user_by  Long(19)  //创建者    
	*/
	public void setCreateUserBy(Long createUserBy){
		this.createUserBy = createUserBy;
	}
	
	/**
	* update_user_by  Long(19)  //修改人    
	*/
	public Long getUpdateUserBy(){
		return updateUserBy;
	}
	
	/**
	* update_user_by  Long(19)  //修改人    
	*/
	public void setUpdateUserBy(Long updateUserBy){
		this.updateUserBy = updateUserBy;
	}
	
	/**
	* create_time  Date(19)  //创建时间    
	*/
	public Date getCreateTime(){
		return createTime;
	}
	
	/**
	* create_time  Date(19)  //创建时间    
	*/
	public void setCreateTime(Date createTime){
		this.createTime = createTime;
	}
	
	/**
	* update_time  Date(19)  //修改时间    
	*/
	public Date getUpdateTime(){
		return updateTime;
	}
	
	/**
	* update_time  Date(19)  //修改时间    
	*/
	public void setUpdateTime(Date updateTime){
		this.updateTime = updateTime;
	}
	
}