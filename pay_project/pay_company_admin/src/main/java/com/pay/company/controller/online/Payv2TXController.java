package com.pay.company.controller.online;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.date.DateStyle;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.tx.entity.TxPayOrder;
import com.pay.business.tx.entity.TxPayOrderClear;
import com.pay.business.tx.entity.TxPayRateBalance;
import com.pay.business.tx.mapper.TxPayOrderMapper;
import com.pay.business.tx.service.TxPayOrderClearService;
import com.pay.business.tx.service.TxPayOrderService;
import com.pay.business.tx.service.TxPayRateBalanceService;
import com.pay.business.util.ParameterEunm;
import com.pay.company.ExportExcel.CsvWriter;
import com.pay.company.controller.admin.BaseManagerController;
import com.pay.company.controller.offline.Payv2BussCompanyDataController;
/**
 * 
 * @ClassName: Payv2TXController
 * @Description: 商户提现
 * @author zhangheng
 * @date 2017年9月11日 19:43:12
 */
@Controller
@RequestMapping("/TX/*")
public class Payv2TXController extends BaseManagerController<TxPayOrder, TxPayOrderMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private TxPayOrderService txPOS;
	@Autowired
	private TxPayRateBalanceService txPRBS;
	@Autowired
	private Payv2PayWayRateService pwrs;
	@Autowired
	private TxPayOrderClearService txPOCS;
	/**
	 * 商户提现关键数据获取
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("curData")
	public Map<String, Object> curData(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			Long companyId = getAdmin().getId();
			map.put("companyId", companyId);
			// 查看当前商户是否有D0通道
			List<Payv2PayWayRate> DOList = pwrs.getD0Rates(map);
			if(DOList == null || DOList.size() == 0){
				resultMap.put("resultCode",201);
				logger.error("当前商户没有D0通道");
				return resultMap;
			}
			// 根据商户ID获取可提现交易金额，获取今日交易金额，今日转T1交易金额
			TxPayRateBalance txPayRateBalance = txPRBS.getCurDate(map);
			resultMap.put("txPayRateBalance",txPayRateBalance);
			// 获取今日提现手续费、到账金额
			map.put("txTime", DateUtil.DateToString(new Date(), DateStyle.YYYY_MM_DD));
			TxPayOrder txPayOrder = txPOS.getNowAmount(map);
			resultMap.put("txNowAmount",txPayOrder);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取商户提现关键数据失败", e);
		}
		return resultMap;
	}
	/**
	 * 提现金额详情（商户在每个渠道下的提现金额）
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("txRateMoney")
	public Map<String, Object> txRateMoney(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			map.put("companyId", getAdmin().getId());
			resultMap.put("txRateMoney",txPRBS.getTxRateMoney(map));
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取渠道提现金额错误", e);
		}
		return resultMap;
	}
	/**
	 * 根据商户id提现全部金额
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/single", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> singleEnchashment(){
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取当前商户
		result.put("companyId", getAdmin().getId());
		// 判断当前时间是否在0~8点
		int hour = new Date().getHours();
		if(hour >= 22 && hour < 8){
			result.put("code", 201);
			result.put("msg", "每日22:00以后，8:00以前不能提现");
			logger.error("每日22:00以后，8:00以前不能提现");
			return result;
		}
		// 获取可提现余额，判断可提现金额是否小于10元
		Double money = txPRBS.getTxBalance(result);
		if(money == null){
			money = 0d;
		}
		if(money<10d){
			result.put("code", 201);
			result.put("msg", "可提现余额小于10元，不能提现");
			logger.error("提现余额小于10元，不能提现");
			return result;
		}
		result.clear();
		result.put("code", 200);
		try {
			txPOS.setEnchashmentByCom(getAdmin().getId().toString());
		} catch (Exception e) {
			result.put("code", 201);
			result.put("msg", e.getMessage());
			e.printStackTrace();
			logger.error("商户提现错误", e);
		}
		return result;
	}
	/**
	 * 获取商户提现订单分页列表
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("txOrderList")
	public Map<String, Object> txOrderList(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			map.put("companyId", getAdmin().getId());
			// 设置代付订单的查询时间
			if (map.get("dateType") != null && map.get("dateType") != "") {
				Map<String, Object> paramMap = Payv2BussCompanyDataController.commonTimeParam(map);
				map.put("txTimeBegin", paramMap.get("startTime"));
				map.put("txTimeEnd", paramMap.get("endTime"));
			}
			resultMap.put("txOrders", txPOS.getPageObject(map));
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取提现订单数据错误", e);
		}
		return resultMap;
	}	
	/**
	 * 提现订单导出
	 * 
	 * @param map
	 * @param workbook
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ordersExport")
	public Map<String, Object> ordersExport(@RequestParam Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		OutputStream out = null;
		String displayColNames = null;
		String matchColNames = null;
		String fileName = null;
		String content = "";
		try {
			// 搜索
			map.put("curPage", 1);
			map.put("pageData", 999999999);
			map.put("companyId", getAdmin().getId());
			// 设置代付订单的查询时间
			if (map.get("dateType") != null && map.get("dateType") != "") {
				Map<String, Object> paramMap = Payv2BussCompanyDataController.commonTimeParam(map);
				map.put("txTimeBegin", paramMap.get("startTime"));
				map.put("txTimeEnd", paramMap.get("endTime"));
			}
			PageObject<TxPayOrder> pageList = txPOS.getPageObject(map);
			// 获取LIST集合
			List<Map<String, Object>> dataList = fillData(pageList.getDataList());
			// 完成数据csv文件的封装
			displayColNames = "提现时间,提现订单号,支付通道,开户行全称,银行账号,开户名称,订单状态,交易金额,提现手续费,交易手续费,提现到账金额";
			matchColNames = "txTime,orderNum,payWayName,accountBank,accountCard,accountName,status,amount,serviceAmount,payServiceAmount,arrivalAmount";
			fileName = "DO提现订单";
			content = fillTheme(getAdmin(), map);
			content += CsvWriter.formatCsvData(dataList, displayColNames, matchColNames);
			content += "#------------------交易明细列表结束------------------#\r\n";
			CsvWriter.exportCsv(fileName, content, response);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			logger.error("导出DO提现订单.csv错误", e);
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnMap;	
	}
	/**
	 * 标题封装
	 * 
	 * @param company
	 * @param map
	 * @return
	 */
	public static String fillTheme(Payv2BussCompany company, Map<String, Object> map) {
		StringBuffer buf = new StringBuffer();		
		buf.append("D0提现订单明细").append(",").append("\r\n");		
		buf.append("商户号：").append(",").append(company.getUserName() + "\t").append(",").append("\r\n");
		buf.append("商户名称：").append(",").append(company.getCompanyName()).append(",").append("\r\n");
		buf.append("起始日期：").append(",").append(map.get("txTimeBegin") == null ? "" : map.get("txTimeBegin").toString()).append(",").append("终止日期：")
				.append(",").append(map.get("txTimeEnd") == null ? "" : map.get("txTimeEnd").toString()).append(",").append("\r\n");
		buf.append("#------------------交易明细列表开始------------------#").append(",").append("\r\n");
		return buf.toString();
	}
	/**
	 * 提现订单导出数据封装
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> fillData(List<TxPayOrder> list) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (TxPayOrder order : list) {
			Map<String, Object> orderMap = new HashMap<String, Object>();
			orderMap.put("txTime", order.getTxTime() == null ? "" : DateUtil.DateToString(order.getTxTime(), "yyyy-MM-dd HH:mm:ss") + "\t");
			orderMap.put("orderNum", order.getOrderNum() == null ? "" : order.getOrderNum() + "\t");
			orderMap.put("payWayName", order.getPayWayWame() == null ? "" : order.getPayWayWame() + "\t");
			orderMap.put("accountBank", order.getAccountBank() == null ? "" : order.getAccountBank()+ "\t");
			orderMap.put("accountCard", order.getAccountCard() == null ? "" : order.getAccountCard()+ "\t");
			orderMap.put("accountName", order.getAccountName() == null ? "" : order.getAccountName()+ "\t");
			if(order.getStatus() == 1){
				orderMap.put("status","提现成功"+ "\t");
			}else if(order.getStatus() == 2){
				orderMap.put("status","提现中"+ "\t");
			}else if(order.getStatus() == 3){
				orderMap.put("status","提现失败"+ "\t");
			}else{
				orderMap.put("dfStatus",""+ "\t");
			}
			orderMap.put("amount", order.getAmount() == null ? "" : order.getAmount()+ "\t");
			orderMap.put("serviceAmount", order.getServiceAmount() == null ? "" : order.getServiceAmount()+ "\t");
			orderMap.put("payServiceAmount", order.getPayServiceAmount() == null ? "" : order.getPayServiceAmount()+ "\t");
			orderMap.put("arrivalAmount", order.getArrivalAmount() == null ? "" : order.getArrivalAmount()+ "\t");
			dataList.add(orderMap);
		}
		return dataList;
	}
	/**
	 * 获取商户DO提现账单分页列表
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("txOrderClearList")
	public Map<String, Object> txOrderClearList(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			map.put("companyId", getAdmin().getId());
			resultMap.put("txClearOrderList", txPOCS.getPageObject(map));
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取DO提现账单数据错误", e);
		}
		return resultMap;
	}
	/**
	 * 商户DO提现账单导出
	 * 
	 * @param map
	 * @param workbook
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("ordersClearExport")
	public Map<String, Object> ordersClearExport(@RequestParam Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		OutputStream out = null;
		String displayColNames = null;
		String matchColNames = null;
		String fileName = null;
		String content = "";
		try {
			// 搜索
			map.put("companyId", getAdmin().getId());
			List<TxPayOrderClear> clearList = txPOCS.getRatesList(map);
			// 获取LIST集合
			List<Map<String, Object>> dataList = fillData2(clearList);
			// 完成数据csv文件的封装
			displayColNames = "支付通道,账单时间,交易金额,转T1交易额,已提现交易额,提现手续费,提现次数,D0交易手续费,到账金额";
			matchColNames = "payWayName,clearTime,balance,t1Balance,txBalance,txServiceAmount,txCount,payServiceAmount,arrivalAmount";
			fileName = "DO提现账单";
			content = fillTheme2(getAdmin(), map);
			content += CsvWriter.formatCsvData(dataList, displayColNames, matchColNames);
			CsvWriter.exportCsv(fileName, content, response);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			logger.error("导出DO提现账单.csv错误", e);
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnMap;	
	}
	/**
	 * 标题封装
	 * 
	 * @param company
	 * @param map
	 * @return
	 */
	public static String fillTheme2(Payv2BussCompany company, Map<String, Object> map) {
		StringBuffer buf = new StringBuffer();			
		
		buf.append("起始日期：").append(",").append(map.get("txTimeBegin") == null ? "" : map.get("clearTimeBegin").toString()).append(",").append("终止日期：")
				.append(",").append(map.get("txTimeEnd") == null ? "" : map.get("clearTimeEnd").toString()).append(",").append("\r\n");
		buf.append("商户号：").append(",").append(company.getUserName() + "\t").append(",");
		buf.append("商户名称：").append(",").append(company.getCompanyName()).append(",").append("\r\n");
		buf.append("商户D0账单汇总").append(",").append("\r\n");
		return buf.toString();
	}
	/**
	 * D0提现账单导出数据封装
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> fillData2(List<TxPayOrderClear> list) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (TxPayOrderClear order : list) {
			Map<String, Object> orderMap = new HashMap<String, Object>();
			orderMap.put("payWayName", order.getPayWayName() == null ? "" : order.getPayWayName() + "\t");
			orderMap.put("clearTime", order.getClearTime() == null ? "" : DateUtil.DateToString(order.getClearTime(), "yyyy-MM-dd HH:mm:ss") + "\t");
			orderMap.put("balance", order.getBalance() == null ? "" : order.getBalance() + "\t");
			orderMap.put("t1Balance", order.getT1Balance() == null ? "" : order.getT1Balance() + "\t");
			orderMap.put("txBalance", order.getTxBalance() == null ? "" : order.getTxBalance()+ "\t");
			orderMap.put("txServiceAmount", order.getTxServiceAmount() == null ? "" : order.getTxServiceAmount()+ "\t");
			orderMap.put("txCount", order.getTxCount() == null ? "" : order.getTxCount()+ "\t");
			orderMap.put("payServiceAmount", order.getPayServiceAmount() == null ? "" : order.getPayServiceAmount()+ "\t");
			orderMap.put("arrivalAmount", order.getArrivalAmount() == null ? "" : order.getArrivalAmount()+ "\t");
			dataList.add(orderMap);
		}
		return dataList;
	}
}
