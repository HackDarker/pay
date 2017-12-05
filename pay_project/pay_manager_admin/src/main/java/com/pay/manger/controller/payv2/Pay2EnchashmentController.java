package com.pay.manger.controller.payv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.tx.entity.TxPayOrder;
import com.pay.business.tx.entity.TxPayOrderClear;
import com.pay.business.tx.entity.TxPayRateBalance;
import com.pay.business.tx.service.TxPayOrderClearService;
import com.pay.business.tx.service.TxPayOrderService;
import com.pay.business.tx.service.TxPayRateBalanceService;

@Controller
@RequestMapping("/enchashment")
public class Pay2EnchashmentController {
	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private TxPayOrderService txPayOrderService;

	@Autowired
	private TxPayRateBalanceService txPayRateBalanceService;

	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;


	@Autowired
	private TxPayOrderClearService txPayOrderClearService;
	
	/**
	 * 跳转用
	 */
	@RequestMapping(value="/tz", method=RequestMethod.GET)
	public ModelAndView tz(){
		return new ModelAndView("/tz");
	}
	
	/**
	 * 根据商户id提现
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/single", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> singleEnchashment(String id){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", 200);
		try {
			
			txPayOrderService.setEnchashmentByCom(id);
		} catch (Exception e) {
			result.put("code", 201);
			result.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 提现列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/txOrderList")
	public ModelAndView txOrderList(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/tx/tx-order-list");
		map.put("sortName", "tx_time");
		map.put("sortOrder", "DESC");
		PageObject<TxPayOrder> pageObject = txPayOrderService.searchTxOrderList(map);
		mv.addObject("list", pageObject);
		
		// 可提现金额
		List<TxPayRateBalance> tempList = txPayRateBalanceService.getTxRateMoney(map);
		TxPayRateBalance qqRate = new TxPayRateBalance();
		TxPayRateBalance aliRate = new TxPayRateBalance();
		TxPayRateBalance wxRate = new TxPayRateBalance();
		qqRate.setPayWayName("QQ");
		qqRate.setTxBalance(0d);
		aliRate.setPayWayName("支付宝");
		aliRate.setTxBalance(0d);
		wxRate.setPayWayName("微信");
		wxRate.setTxBalance(0d);
		for (TxPayRateBalance txPayRateBalance : tempList) {
			if(txPayRateBalance.getPayWayName().contains("QQ")) {
				qqRate.setTxBalance(qqRate.getTxBalance().doubleValue()+txPayRateBalance.getTxBalance().doubleValue());
			} else if (txPayRateBalance.getPayWayName().contains("支付宝")) {
				aliRate.setTxBalance(aliRate.getTxBalance().doubleValue()+txPayRateBalance.getTxBalance().doubleValue());
			}  else if (txPayRateBalance.getPayWayName().contains("微信")) {
				wxRate.setTxBalance(wxRate.getTxBalance().doubleValue()+txPayRateBalance.getTxBalance().doubleValue());
			} 
		}
//		List<TxPayRateBalance> ktxjeList = new ArrayList<TxPayRateBalance>();
//		ktxjeList.add(qqRate);
//		ktxjeList.add(aliRate);
//		ktxjeList.add(wxRate);
		mv.addObject("ktxje", qqRate.getTxBalance().doubleValue()+aliRate.getTxBalance().doubleValue()+wxRate.getTxBalance().doubleValue());
		Map<String, Object> ktxjeMap = new HashMap<String, Object>();
		ktxjeMap.put("qq", qqRate.getPayWayName());
		ktxjeMap.put("qqRate", qqRate.getTxBalance());
		ktxjeMap.put("wx", wxRate.getPayWayName());
		ktxjeMap.put("wxRate", wxRate.getTxBalance());
		ktxjeMap.put("ali", aliRate.getPayWayName());
		ktxjeMap.put("aliRate", aliRate.getTxBalance());
		
		mv.addObject("ktxjeMap", ktxjeMap);
		
		// 体现中金额
		Double txzje = txPayOrderService.getIngAmount(map);
		mv.addObject("txzje", txzje);
		
		// 今日提现成功、提现手续费、到账金额
		Map<String,Object> todayMap = txPayOrderService.queryToday(map);
		mv.addObject("today", todayMap);
		
//		Payv2BussCompany company = new Payv2BussCompany();
//		company.setIsDelete(2);
		List<TxPayRateBalance> companyList = txPayRateBalanceService.selectCompanyByTx(map);
		mv.addObject("companyList", companyList);
		
		
		mv.addObject("map", map);// payWayId
		return mv;
	}
	
	/**
	 * 提现列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/txBillList")
	public ModelAndView txBillList(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/tx/tx-bill-list");
		Map<String, Object> companyMap = map;
		Map<String, Object> rateMap = map;
		map.put("sortName", "clear_time");
		map.put("sortOrder", "DESC");
		if(map.containsKey("curPageCom")) {
			companyMap.put("curPage", map.get("curPageCom"));
			companyMap.put("pageData", map.get("pageDataCom"));
		}
		PageObject<TxPayOrderClear> companylist = txPayOrderClearService.PagequeryForCompany(companyMap);
		mv.addObject("companyClear", companylist);
		
		if(map.containsKey("curPageRate")) {
			rateMap.put("curPage", map.get("curPageRate"));
			rateMap.put("pageData", map.get("pageDataRate"));
		}
		PageObject<TxPayOrderClear> rateList = txPayOrderClearService.PagequeryForRate(rateMap);
		mv.addObject("rateClear", rateList);
		List<TxPayRateBalance> companyList = txPayRateBalanceService.selectCompanyByTx(map);
		mv.addObject("companyList", companyList);
		
		
		mv.addObject("map", map);// payWayId
		return mv;
	}
}
