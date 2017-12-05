package com.pay.business.df.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.IdUtils;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.mapper.DfPayOrderMapper;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.pay.business.df.service.DfPayOrderService;
import com.pay.business.df.service.DfPayWayRateService;
import com.pay.business.payv2.service.impl.DfServiceImpl;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.lianlianDF.dfPay.LianLianDf;
import com.pay.business.util.tianxia.TianxiaPay;

/**
 * @author cyl
 * @version 
 */
@Service("dfPayOrderService")
public class DfPayOrderServiceImpl extends BaseServiceImpl<DfPayOrder, DfPayOrderMapper> implements DfPayOrderService {
	private static final String LL10000="10000";
	private static final String LL10002="10002";
	private static final String LL10003="10003";
	private static final String LL10004="10004";
	private static final String LL10005="10005";
	private static final String LL10006="10006";
	// 注入当前dao对象
    @Autowired
    private DfPayOrderMapper dfPayOrderMapper;
    @Autowired
    private DfPayWayRateService dfPayWayRateService;
    @Autowired
    private DfPayCompanyRateService dfPayCompanyRateService;

    public DfPayOrderServiceImpl() {
        setMapperClass(DfPayOrderMapper.class, DfPayOrder.class);
    }
    @Override
	public PageObject<DfPayOrder> getPageObject(Map<String, Object> map) {
		int totalData = dfPayOrderMapper.getCount2(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<DfPayOrder> orderList = dfPayOrderMapper.pageQueryByObject2(helper.getMap());
		PageObject<DfPayOrder> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
	}
    @Override
	public DfPayOrder selectOrderDetail(Map<String, Object> map) {
		return dfPayOrderMapper.selectOrderDetail(map);
	}
    @Override
	public Map<String, Object> dfCreateOrder(Map<String, Object> map, Map<String, Object> param) throws InterruptedException, ParseException{
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("companyId", map.get("companyId"));	
		resultMap.put("dfMerchantOrderNum", param.get("merchantOrderNum").toString());
		
		DfPayOrder dfPayOrder = detail(resultMap);
		if(dfPayOrder!=null){
			resultMap.clear();
			resultMap.put("status", PayFinalUtil.PAY_STATUS_FAIL);
			resultMap.put("msg", "订单号重复！");
			return resultMap;
		}
		
		DfPayOrder order = new DfPayOrder();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		order.setDfOrderNum(sdf.format(new Date()) + IdUtils.createId() + "");
		Object x = map.get("channelId");
		order.setChannelId(Long.valueOf(x.toString()));		
		order.setDfAbstract(param.get("memo").toString());
		order.setDfMerchantOrderNum(param.get("merchantOrderNum").toString());
		order.setCompanyId(Long.valueOf(map.get("companyId")+""));		
		order.setDfType(Integer.valueOf(param.get("payType").toString()));
		order.setDfRateId(Long.valueOf(map.get("rateId")+""));		
		order.setAccountName(param.get("acctName").toString());
		order.setAccountNum(param.get("acctNum").toString());
		order.setDfPayMoney(Double.valueOf(param.get("payMoney").toString()));
		order.setDfPayWayMoney(Double.valueOf(map.get("wayRate")+""));		
		order.setDfCostRateMoney(Double.valueOf(map.get("payRate")+""));	
		order.setReqTime(sdf.parse(param.get("orderTime").toString()));
		order.setPayTime(new Date());
		
		if(null!=param.get("acctType")){
			order.setAccountType(Integer.valueOf(param.get("acctType").toString()));
		}
		if(null!=param.get("bankName")){
			order.setBankName(param.get("bankName").toString());
		}
		if(null!=param.get("bankBranchNum")){
			order.setBankBranchNum(param.get("bankBranchNum").toString());
		}
		if(null!=param.get("bankBranchName")){
			order.setBankBranchName(param.get("bankBranchName").toString());
		}
		if(null!=param.get("mobile")){
			order.setMobile(param.get("mobile").toString());
		}
		dfPayOrderMapper.insertByEntity(order);
		resultMap.put("order", order);
		resultMap.put("status", PayFinalUtil.PAY_STATUS_SUSSESS);
		return resultMap;
	}

	/**
	 * 单笔代付查询（提供给商户）
	 * @param map
	 * @return
	 * @throws Exception 
	 */
    @Override
	public Map<String, Object> singleQuery(Map<String, Object> map, Map<String, Object> pbc) throws Exception {

		DfPayOrder payOrder = new DfPayOrder();
		// 商户传了支付集订单号
		if (map.containsKey("jfOrderNum")) {
			payOrder.setDfOrderNum(map.get("jfOrderNum").toString());
			payOrder = dfPayOrderMapper.selectSingle(payOrder);
		} else {// 商户传了商户订单号
			payOrder.setCompanyId(Long.valueOf(pbc.get("companyId") + ""));
			payOrder.setDfMerchantOrderNum(map.get("merchantOrderNum").toString());
			payOrder = dfPayOrderMapper.selectSingle(payOrder);
		}
		if (payOrder == null) {
			return ReMessage.resultBack(ParameterEunm.TRANSACTION_NOT_EXIST, null);
		}
		if (payOrder.getDfStatus() == 2) {
			String dictName = pbc.get("dictName").toString();
			// 天付宝代付
			if (dictName.equals(DfServiceImpl.DF_PAY_TYPE_TFB)) {
				Map<String, String> mapQuery = TianxiaPay.singleQuery(pbc.get("key1").toString(), payOrder.getDfOrderNum(), pbc.get("key2").toString(), pbc
						.get("key3").toString(), pbc.get("key4").toString());
				if (mapQuery.containsKey("code") && LL10000.equals(mapQuery.get("code"))) {
					if ("1".equals(mapQuery.get("dfStatus"))) {
						payOrder.setDfStatus(1);
						dfPayOrderMapper.updateByEntity(payOrder);
						DfPayWayRate dfPayWayRate = new DfPayWayRate();
						dfPayWayRate.setId(Long.valueOf(pbc.get("rateId") + ""));
						DfPayCompanyRate dfPayCompanyRate = new DfPayCompanyRate();
						dfPayCompanyRate.setId(Long.valueOf(pbc.get("id") + ""));

						Double dfPayMoney = payOrder.getDfPayMoney();
						// 资金池
						Double rateTotalAmount = Double.valueOf(pbc.get("rateTotalAmount") + "");
						// 商户余额
						Double totalAmount = Double.valueOf(pbc.get("totalAmount") + "");
						// 成本费
						Double dfCostRateMoney = payOrder.getDfCostRateMoney();
						// 手续费
						Double dfPayWayMoney = payOrder.getDfPayWayMoney();
						dfPayWayRate.setTotalAmount(rateTotalAmount - dfPayMoney - dfCostRateMoney);
						dfPayCompanyRate.setTotalAmount(totalAmount - dfPayMoney - dfPayWayMoney);
						dfPayWayRateService.update(dfPayWayRate);
						dfPayCompanyRateService.update(dfPayCompanyRate);
						// 处理成功
					} else if ("3".equals(mapQuery.get("dfStatus"))) {
						// 处理失败
						payOrder.setDfStatus(3);
						dfPayOrderMapper.updateByEntity(payOrder);
					} else {
						// 处理中
						payOrder.setDfStatus(2);
					}
				} else {
					// 请求上游错误
					return ReMessage.resultBack(ParameterEunm.QUERY_FAIL, null);
				}
			}
			// 连连代付订单查询
			if (dictName.equals(DfServiceImpl.DF_PAY_TYPE_LL)) {
				// 商户号
				String oidPartner = pbc.get("key1").toString();
				// 公钥
				String publicKeyOnline = pbc.get("key3").toString();
				// 私钥
				String businessPrivateKey = pbc.get("key2").toString();
				Map<String, Object> mapQuery = LianLianDf
						.queryPaymentAndDealBusiness(payOrder.getDfOrderNum(), oidPartner, publicKeyOnline, businessPrivateKey);
				String codeYse = "code";
				if (mapQuery.containsKey(codeYse)) {
					String code = mapQuery.get("code").toString();
					// 付款处理中
					if (LL10000.equals(code)) {
						payOrder.setDfStatus(2);
					}
					// 付款已经成功
					else if (LL10002.equals(code)) {
						// 如果订单查询是已经付款成功了··则需要改变订单状态，修改资金池的资金变化
						payOrder.setDfStatus(1);
						payOrder.setBankTransaction(mapQuery.get("bank_transaction").toString());
						payOrder.setRemark(mapQuery.get("statusState").toString());
						// 清算时间
						payOrder.setPayTime(new SimpleDateFormat("yyyyMMdd").parse(mapQuery.get("rsptime").toString()));
						dfPayOrderMapper.updateByEntity(payOrder);
						DfPayWayRate dfPayWayRate = new DfPayWayRate();
						dfPayWayRate.setId(Long.valueOf(pbc.get("rateId") + ""));
						DfPayCompanyRate dfPayCompanyRate = new DfPayCompanyRate();
						dfPayCompanyRate.setId(Long.valueOf(pbc.get("id") + ""));

						Double dfPayMoney = payOrder.getDfPayMoney();
						// 资金池
						Double rateTotalAmount = Double.valueOf(pbc.get("rateTotalAmount") + "");
						// 商户余额
						Double totalAmount = Double.valueOf(pbc.get("totalAmount") + "");
						// 成本费
						Double dfCostRateMoney = payOrder.getDfCostRateMoney();
						// 手续费
						Double dfPayWayMoney = payOrder.getDfPayWayMoney();
						dfPayWayRate.setTotalAmount(rateTotalAmount - dfPayMoney - dfCostRateMoney);
						dfPayCompanyRate.setTotalAmount(totalAmount - dfPayMoney - dfPayWayMoney);
						dfPayWayRateService.update(dfPayWayRate);
						dfPayCompanyRateService.update(dfPayCompanyRate);
					}
					// 付款失败
					else if (LL10003.equals(code) || LL10005.equals(code) || LL10006.equals(code) || LL10004.equals(code)) {
						payOrder.setDfStatus(3);
						dfPayOrderMapper.updateByEntity(payOrder);
					} else {
						// 请求上游错误
						System.out.println("连连代付-查询订单异常原因为：" + mapQuery.get("msg"));
						return ReMessage.resultBack(ParameterEunm.QUERY_FAIL, null);
					}
				} else {
					// 请求上游错误
					System.out.println("连连代付-查询失败");
					return ReMessage.resultBack(ParameterEunm.QUERY_FAIL, null);
				}
			}
		}

		// 返回商户字段
		Map<String, Object> param = new HashMap<String, Object>();
		// 商户号
		param.put("mchNum", map.get("mchNum"));
		// 平台订单号
		param.put("jfOrderNum", payOrder.getDfOrderNum());
		// 商户代付订单号
		param.put("merchantOrderNum", payOrder.getDfOrderNum());
		// 代付状态1.处理成功2处理中3处理失败4处理异常
		param.put("status", payOrder.getDfStatus());
		// 交易时间
		param.put("payTime", DateUtil.DateToString(payOrder.getPayTime(), "yyyyMMddHHmmss"));
		// 代付金额
		param.put("payMoney", payOrder.getDfPayMoney());
		// 收款人姓名
		param.put("accountName", payOrder.getAccountName());
		// 收款人账号
		param.put("accountNum", payOrder.getAccountNum());

		if (payOrder.getMobile() != null) {
			// 手机号
			param.put("mobile", payOrder.getMobile());
		}
		if (payOrder.getBankName() != null) {
			// 开户行名称
			param.put("bankName", payOrder.getBankName());
		}
		if (payOrder.getBankBranchName() != null) {
			// 支行名称
			param.put("bankBranchName", payOrder.getBankBranchName());
		}
		if (payOrder.getBankBranchNum() != null) {
			// 开户行支行联行号
			param.put("bankBranchNum", payOrder.getBankBranchNum());
		}
		if (payOrder.getDfAbstract() != null) {
			// 摘要
			param.put("memo", payOrder.getDfAbstract());
		}
		if (payOrder.getMerchantBatchNum() != null) {
			// 商户批次号
			param.put("merchantBatchNum", payOrder.getMerchantBatchNum());
		}
		/*
		 * String sign = PaySignUtil.getSign(param,
		 * pbc.get("comSecret").toString()); param.put("sign", sign);
		 */
		return ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, param);
	}
}
