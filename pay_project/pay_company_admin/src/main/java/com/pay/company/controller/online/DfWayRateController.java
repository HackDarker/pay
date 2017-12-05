package com.pay.company.controller.online;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.mapper.DfPayOrderMapper;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.pay.business.df.service.DfPayOrderService;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.util.ParameterEunm;
import com.pay.company.ExportExcel.CsvWriter;
import com.pay.company.controller.admin.BaseManagerController;
import com.pay.company.controller.offline.Payv2BussCompanyDataController;
/**
 * 
 * @ClassName: dfWayRate
 * @Description: 代付通道
 * @author zhangheng
 * @date 2017年8月31日 10:43:12
 */
@Controller
@RequestMapping("/dfWayRate/*")
public class DfWayRateController extends BaseManagerController<DfPayOrder, DfPayOrderMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private DfPayCompanyRateService dfPayCompanyRateService;
	@Autowired
	private DfPayOrderService dfPayOrderService;
	/**
	 * 查询商户的代付资金池
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("dfInfo")
	public Map<String, Object> dfInfo(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			map.put("companyId", getAdmin().getId());
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(map);
			if(rate!=null){
				resultMap.put("totalAmount", rate.getTotalAmount());
			}else{
				resultMap.put("totalAmount", 0);
			}
			// 获取商户的代付通道信息
			resultMap.put("comRates", dfPayCompanyRateService.getRates(map));
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取商户代付余额错误", e);
		}
		return resultMap;
	}
	/**
	 * 代付订单列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("dfOrderList")
	public Map<String, Object> dfOrderList(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			// 获取当前商户
			map.put("companyId", getAdmin().getId());
			// 设置代付订单的查询时间
			if (map.get("dateType") != null && map.get("dateType") != "") {
				Map<String, Object> paramMap = Payv2BussCompanyDataController.commonTimeParam(map);
				map.put("createTimeBegin", paramMap.get("startTime"));
				map.put("createTimeEnd", paramMap.get("endTime"));
			}
			PageObject<DfPayOrder> pageList = dfPayOrderService.getPageObject(map);
			resultMap.put("list", pageList);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取代付订单数据错误", e);
		}
		return resultMap;
	}
	/**
	 * 代付订单详情
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getOrderDetail")
	public Map<String, Object> getOrderDetail(@RequestParam Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			DfPayOrder order = dfPayOrderService.selectOrderDetail(map);
			resultMap.put("orderDetail", order);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取代付订单详情数据错误", e);
		}
		return resultMap;
	}
	/**
	 * @param map form页面搜索条件
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
			Payv2BussCompany company = getAdmin();
			map.put("companyId", company.getId());
			// 设置代付订单的查询时间
			if (map.get("dateType") != null && map.get("dateType") != "") {
				Map<String, Object> paramMap = Payv2BussCompanyDataController.commonTimeParam(map);
				map.put("startTime", paramMap.get("startTime"));
				map.put("endTime", paramMap.get("endTime"));
			}
			PageObject<DfPayOrder> pageList = dfPayOrderService.getPageObject(map);
			// 获取LIST集合
			List<Map<String, Object>> dataList = fillData(pageList.getDataList());
			// 完成数据csv文件的封装
			displayColNames = "交易时间,平台代付单号,商户代付单号,来源商户,代付通道,代付金额,商户代付费,代付类型,账号类型,开户行全称,银行账号,开户名称,开户行支行联行号,摘要";
			matchColNames = "payTime,dfOrderNum,dfMerchantOrderNum,companyName,dfPayWayName,dfPayMoney,costRateMoney,dfStatus,accountType,bankName,accountNum,accountName,bankBranchNum";
			fileName = "商户代付订单";
			content += CsvWriter.formatCsvData(dataList, displayColNames, matchColNames);
			CsvWriter.exportCsv(fileName, content, response);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			logger.error("导出商户代付订单.csv错误", e);
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
	 * 数据封装
	 * 
	 * @param list
	 * @return
	 */
	private List<Map<String, Object>> fillData(List<DfPayOrder> list) {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		for (DfPayOrder order : list) {
			Map<String, Object> orderMap = new HashMap<String, Object>();
			orderMap.put("payTime", order.getPayTime() == null ? "" : DateUtil.DateToString(order.getPayTime(), "yyyy-MM-dd HH:mm:ss") + "\t");
			orderMap.put("dfOrderNum", order.getDfOrderNum() == null ? "" : order.getDfOrderNum() + "\t");
			orderMap.put("dfMerchantOrderNum", order.getDfMerchantOrderNum() == null ? "" : order.getDfMerchantOrderNum() + "\t");
			orderMap.put("companyName", order.getCompanyName() == null ? "" : order.getCompanyName()+ "\t");
			orderMap.put("dfPayWayName", order.getDfPayWayName() == null ? "" : order.getDfPayWayName()+ "\t");
			orderMap.put("dfPayMoney", order.getDfPayMoney() == null ? "" : order.getDfPayMoney()+ "\t");
			orderMap.put("costRateMoney", order.getDfPayWayMoney() == null ? "" : order.getDfPayWayMoney()+ "\t");
			if(order.getDfStatus() == 1){
				orderMap.put("dfStatus","代付成功"+ "\t");
			}else if(order.getDfStatus() == 2){
				orderMap.put("dfStatus","代付中"+ "\t");
			}else if(order.getDfStatus() == 3){
				orderMap.put("dfStatus","代付失败"+ "\t");
			}else if(order.getDfStatus() == 4){
				orderMap.put("dfStatus","处理异常"+ "\t");
			}else{
				orderMap.put("dfStatus",""+ "\t");
			}			
			if(order.getAccountType() == 1){
				orderMap.put("accountType","借记卡");
			}else if(order.getDfStatus() == 2){
				orderMap.put("accountType","贷记卡");
			}else if(order.getDfStatus() == 3){
				orderMap.put("accountType","公户");
			}else{
				orderMap.put("accountType","");
			}
			orderMap.put("bankName", order.getBankName() == null ? "" : order.getBankName()+ "\t");
			orderMap.put("accountNum", order.getAccountNum() == null ? "" : order.getAccountNum()+ "\t");
			orderMap.put("accountName", order.getAccountName() == null ? "" : order.getAccountName()+ "\t");
			orderMap.put("bankBranchNum", order.getBankBranchNum() == null ? "" : order.getBankBranchNum()+ "\t");
			dataList.add(orderMap);
		}
		return dataList;
	}
}
