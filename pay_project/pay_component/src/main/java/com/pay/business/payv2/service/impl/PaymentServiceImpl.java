package com.pay.business.payv2.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.payv2.service.PaymentService;
import com.pay.business.util.PayFinalUtil;
import com.pay.business.util.PayRateDictValue;

@Service("paymentService")
public class PaymentServiceImpl implements PaymentService{

	@Autowired
	private Payv2BussSupportPayWayService payv2BussSupportPayWayService;// 支付通道
	@Autowired
	private Payv2PayOrderService payv2PayOrderService;
	@Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	
	/**
	 * 支付
	 */
	public Map<String,String> payment(Map<String,Object> map,Payv2BussCompanyApp pbca, String dictNameLike) throws Exception{
		//支付平台  1支付宝 2微信
		Integer payViewType = Integer.valueOf(map.get("payPlatform").toString());
		//支付类型  1app支付 2web支付 3扫码支付 5公众号支付
		//Integer payType = 2;
		// 创建支付集订单
		Map<String, String> orderMap = payv2PayOrderService.dyCreateOrder(map, pbca, payViewType, dictNameLike);
		
		if (orderMap.get("status").equals(PayFinalUtil.PAY_STATUS_SUSSESS)) {
			
			String ip = map.get("ip").toString();
			//app类型 1安卓2ios
			String appType = map.get("appType").toString();
			if(dictNameLike!=null&&"APP".equals(dictNameLike)){
				return PayRateDictValue.appRatePay(orderMap.get("dictName").toString(), orderMap, ip, appType,dictNameLike);
			}else{
				return PayRateDictValue.ratePay(orderMap.get("dictName").toString(), orderMap, ip, appType,dictNameLike);
			}
		}
		
		return orderMap;
	}
	
	/**
	 * 支付
	 */
	public Map<String,String> payment(Map<String,Object> map,Payv2BussCompany pbc, String dictNameLike) throws Exception{
		//支付平台  1支付宝 2微信
		Integer payViewType = Integer.valueOf(map.get("payPlatform").toString());
		//支付类型  1app支付 2web支付 3扫码支付 5公众号支付
		//Integer payType = 2;
		// 创建支付集订单
		Map<String, String> orderMap = payv2BussCompanyCodeService.createOrder(map, pbc, payViewType, dictNameLike);
		
		if (orderMap.get("status").equals(PayFinalUtil.PAY_STATUS_SUSSESS)) {
			
			String ip = map.get("address").toString();
			return PayRateDictValue.ratePay(orderMap.get("dictName").toString(), orderMap, ip, null,dictNameLike);
		}
		
		return orderMap;
	}
}
