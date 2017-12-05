package com.pay.business.payv2.service;

import java.util.Map;

import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;

/**
 * 支付service
 * @author Administrator
 *
 */
public interface PaymentService {
	/**
	 * 支付
	 * @param map
	 * @param pbca
	 * @param payType 1app支付 2web支付 3扫码支付 5公众号支付
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> payment(Map<String,Object> map,Payv2BussCompanyApp pbca, String dictNameLike) throws Exception;
	
	/**
	 * 支付
	 * @param map
	 * @param pbca
	 * @param dictNameLike
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> payment(Map<String,Object> map,Payv2BussCompany pbc, String dictNameLike) throws Exception;
}
