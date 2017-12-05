package com.pay.business.payv2.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.pay.business.df.service.DfPayOrderService;
import com.pay.business.df.service.DfPayWayRateService;
import com.pay.business.payv2.service.DFService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.lianlianDF.bean.PaymentRequestBean;
import com.pay.business.util.lianlianDF.constant.PaymentConstant;
import com.pay.business.util.lianlianDF.dfPay.LianLianDf;
import com.pay.business.util.tianxia.TianxiaPay;

/**
 * @Title: DFServiceImpl.java
 * @Package com.pay.business.payv2.service.impl
 * @Description: 代付实现类
 * @author ZHOULIBO
 * @date 2017年10月18日 上午9:29:22
 * @version V1.0
 */
@Service("dfService")
public class DfServiceImpl implements DFService {
	private static final int T0=0;
	private static final int T1=1;
	private static final int T2=2;
	private static final String T10000="10000";
	private static final String T11="1";
	/**
	 * 天付宝代付
	 */
	public static final String DF_PAY_TYPE_TFB = "DF_PAY_TYPE_TFB";
	/**
	 * 连连代付
	 */
	public static final String DF_PAY_TYPE_LL = "DF_PAY_TYPE_LL";
	
	@Autowired
	private DfPayOrderService orderService;
	
	@Autowired
	private DfPayWayRateService wayRateService;
	
	@Autowired
	private DfPayCompanyRateService dfCompanyService;
	/**
	 * 代付
	 */
	@Override
	public Map<String, Object> dfResult(Map<String, Object> map, Map<String, Object> param) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//resultMap.put("code", "10000");
			Map<String, Object> dfCreateOrder = orderService.dfCreateOrder(map, param);
			if(!PayFinalUtil.PAY_STATUS_SUSSESS.equals(dfCreateOrder.get("status").toString())){
				return ReMessage.resultBack(ParameterEunm.PAYORDER_REPEAT, null);
			}
			String dictName=map.get("dictName").toString();
			if(DF_PAY_TYPE_TFB.equals(dictName)){
				DfPayOrder order = (DfPayOrder)dfCreateOrder.get("order");
				resultMap.put("mchNum", param.get("mchNum"));
				resultMap.put("jfOrderNum", order.getDfOrderNum());
				//resultMap.put("payTime", order.getPayTime());
				
				String spid = map.get("key1")+"";
				String publicKey = map.get("key2")+"";
				String privateKey = map.get("key3")+"";
				String md5Key = map.get("key4")+"";
				String spSerialno = order.getDfOrderNum();
				String tranAmt = order.getDfPayMoney().toString();
				String payType = order.getDfType().toString();
				String acctName = order.getAccountName();
				String acctId = order.getAccountNum();
				String acctType = null;
				if(null!=order.getAccountType()){
					acctType = order.getAccountType().toString();
				}
				
				String businessType = "20101";
				String memo = order.getDfAbstract();
				Map<String, String> singlePay = TianxiaPay.singlePay(spid, spSerialno, DecimalUtil.yuanToCents(tranAmt), payType, acctName, acctId, 
						acctType, order.getMobile(), order.getBankName(), order.getBankBranchNum(), order.getBankBranchName(), businessType, memo, publicKey, privateKey, md5Key);
				if(T10000.equals(singlePay.get("code"))){
					order.setBankTransaction(singlePay.get("bank_transaction"));
					order.setRemark(singlePay.get("statusState"));
					order.setPayTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(singlePay.get("rsptime")));
					//更新订单
					String status = singlePay.get("statusState").split(",")[0];
					order.setDfStatus(Integer.valueOf(status));
					orderService.update(order);
					resultMap.put("msg", singlePay.get("statusState").split(",")[1]);
					resultMap.put("status", status);
					if(T11.equals(status)){
						DfPayWayRate dfPayWayRate = new DfPayWayRate();
						dfPayWayRate.setId(Long.valueOf(map.get("rateId")+""));
						DfPayCompanyRate dfPayCompanyRate = new DfPayCompanyRate();
						dfPayCompanyRate.setId(Long.valueOf(map.get("id")+""));
						
						Double dfPayMoney = order.getDfPayMoney();
						//资金池
						Double rateTotalAmount = Double.valueOf(map.get("rateTotalAmount")+"");
						//商户余额
						Double totalAmount = Double.valueOf(map.get("totalAmount")+"");
						//成本费
						Double dfCostRateMoney = order.getDfCostRateMoney();
						//手续费
						Double dfPayWayMoney = order.getDfPayWayMoney();
						dfPayWayRate.setTotalAmount(rateTotalAmount-dfPayMoney-dfCostRateMoney);
						dfPayCompanyRate.setTotalAmount(totalAmount-dfPayMoney-dfPayWayMoney);
						wayRateService.update(dfPayWayRate);
						dfCompanyService.update(dfPayCompanyRate);
					}
					resultMap.put("payTime", order.getPayTime());
				}else {
					resultMap.put("msg", singlePay.get("msg"));
					return ReMessage.resultBack(ParameterEunm.PAYORDER_ERROR, null);
				}
			}
			
			/**
			 * 连连代付
			 */
			if(DF_PAY_TYPE_LL.equals(dictName)){
				DfPayOrder order = (DfPayOrder)dfCreateOrder.get("order");
				//商户号
				String spid = map.get("key1")+"";
				//连连代付商户私钥
				String privateKey = map.get("key2")+"";
				//连连代付公钥
				String publicKey = map.get("key3")+"";
				if(spid!=null&&publicKey!=null&&privateKey!=null){
					PaymentRequestBean paymentRequestBean=new PaymentRequestBean();
					//固定参数
					// 商户号
					paymentRequestBean.setOid_partner(spid);
					// 平台来源：可以不填
					// paymentRequestBean.setPlatform("test.com");
					// 版本号
					paymentRequestBean.setApi_version(PaymentConstant.API_VERSION);
					// 签名方式
					paymentRequestBean.setSign_type(PaymentConstant.SIGN_TYPE);
					//请求参数
					//商户付款流水号:
					paymentRequestBean.setNo_order(order.getDfOrderNum());
					//商户付款时间
					paymentRequestBean.setDt_order(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					//付款金额
					paymentRequestBean.setMoney_order(order.getDfPayMoney().toString());
					//银行账号
					paymentRequestBean.setCard_no(order.getAccountNum());
					//收款人名字
					paymentRequestBean.setAcct_name(order.getAccountName());
					// paymentRequestBean.setBank_name("中国平安银行");
					//付款用途
					paymentRequestBean.setInfo_order("代付"+System.currentTimeMillis());
					//0 对私  1对公
					int acctType = order.getAccountType();
					//对私
					if(acctType==T0||acctType==T1){
						paymentRequestBean.setFlag_card("0");
					}
					//对公
					if(acctType==T2){
						paymentRequestBean.setFlag_card("1");
					}
					// 收款备注 用于给用户显示
					paymentRequestBean.setMemo(order.getDfAbstract());
					// 填写商户自己的接收付款结果回调异步通知
					paymentRequestBean.setNotify_url(PaymentConstant.NOTIFY_URL);
					//调用连连代付
					Map<String,Object>	singlePay=LianLianDf.lianLianPay(paymentRequestBean,spid,publicKey,privateKey);
					String code="code";
					String t10000="10000";
					String t10002="10002";
					if(singlePay.containsKey(code)){
						if(t10000.equals(singlePay.get(code))){
							order.setBankTransaction(singlePay.get("bank_transaction").toString());
							order.setRemark(singlePay.get("statusState").toString());
							//这里没有返回交易的时间；所以默认用我们的付款时间
//							order.setPayTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(paymentRequestBean.getDt_order()));
							//更新订单
							//注意：连连代付：付款默认是交易成功，都是付款中状态，银行处理成功后，通过异步URL通知服务器，这个时候去改变这笔代付成功状态：所以这里默认为2
							order.setDfStatus(2);
							//修改代付订单状态
							orderService.update(order);
							//返回参数
							resultMap.put("mchNum", param.get("mchNum"));
							resultMap.put("jfOrderNum", order.getDfOrderNum());
							resultMap.put("msg","交易成功");
							resultMap.put("status", 2);
							resultMap.put("payTime", order.getPayTime());
						}
						//已经清算：成功
						else if(t10002.equals(singlePay.get(code))){
							DfPayWayRate dfPayWayRate = new DfPayWayRate();
							dfPayWayRate.setId(Long.valueOf(map.get("rateId")+""));
							DfPayCompanyRate dfPayCompanyRate = new DfPayCompanyRate();
							dfPayCompanyRate.setId(Long.valueOf(map.get("id")+""));
							
							Double dfPayMoney = order.getDfPayMoney();
							//资金池
							Double rateTotalAmount = Double.valueOf(map.get("rateTotalAmount")+"");
							//商户余额
							Double totalAmount = Double.valueOf(map.get("totalAmount")+"");
							//成本费
							Double dfCostRateMoney = order.getDfCostRateMoney();
							//手续费
							Double dfPayWayMoney = order.getDfPayWayMoney();
							dfPayWayRate.setTotalAmount(rateTotalAmount-dfPayMoney-dfCostRateMoney);
							dfPayCompanyRate.setTotalAmount(totalAmount-dfPayMoney-dfPayWayMoney);
							wayRateService.update(dfPayWayRate);
							dfCompanyService.update(dfPayCompanyRate);
							
							//返回参数
							resultMap.put("mchNum", param.get("mchNum"));
							resultMap.put("jfOrderNum", order.getDfOrderNum());
							resultMap.put("msg","交易成功");
							resultMap.put("status",1);
							resultMap.put("payTime",new SimpleDateFormat("yyyyMMdd").parse(singlePay.get("rsptime").toString()));
						}else {
							//失败
							order.setDfStatus(3);
							//修改代付订单状态
							orderService.update(order);
							resultMap.put("msg", singlePay.get("msg"));
							System.out.println("下单失败："+singlePay.get("msg"));
							return ReMessage.resultBack(ParameterEunm.PAYORDER_ERROR, null);
						}
					}else{
						System.out.println("连连代付-付款申请失败");
						return ReMessage.resultBack(ParameterEunm.PAYORDER_ERROR, null);
					}
				}else{
					System.out.println("连连代付:配置不完整：请检查 key1,key2,key3是否填写完整");
					return ReMessage.resultBack(ParameterEunm.PAYORDER_ERROR, null);
				}
				
			}
		return ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
	}

}
