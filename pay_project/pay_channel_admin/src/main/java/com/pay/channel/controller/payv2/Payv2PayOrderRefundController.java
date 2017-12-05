package com.pay.channel.controller.payv2;


import java.io.OutputStream;
import java.net.URLEncoder;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.order.entity.OrderRefundExportVO;
import com.pay.business.order.entity.Payv2PayOrderRefundVO;
import com.pay.business.order.mapper.Payv2PayOrderRefundMapper;
import com.pay.business.order.service.Payv2PayOrderRefundService;
import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.service.Payv2PayTypeService;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.util.ParameterEunm;
import com.pay.channel.ExportExcel.TestExportExcel;
import com.pay.channel.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2PayOrderRefund/*")
public class Payv2PayOrderRefundController extends BaseManagerController<Payv2PayOrderRefundVO, Payv2PayOrderRefundMapper> {

	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;	
	@Autowired
	private Payv2PayWayService payv2PayWayService;//支付渠道服务	
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;//支付渠道服务
	@Autowired
	private Payv2PayOrderRefundService payv2PayOrderRefundService;//退款订单服务
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;
	@Autowired
	private Payv2BussSupportPayWayService payv2BussSupportPayWayService;
	@Autowired
    private Payv2PayTypeService payv2PayTypeService;
	
	private static final Logger logger = Logger.getLogger(Payv2PayOrderRefundController.class);
	
	/**
	 * 退款订单默认加载页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */	
	@RequestMapping("alldata")
	public ModelAndView showDetailDay(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/businessManager/payv2OrderRefundList");		
		// 根据渠道商ID 获取渠道下面的商户
		Long channelId = getAdmin().getId();
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		payv2BussCompany.setChannelId(channelId);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);		
		
    	//获取支付平台列表
    	Payv2PayWay pay = new Payv2PayWay();
		List<Payv2PayWay> payList = payv2PayWayService.selectByObject(pay);
		//获取支付方式列表
		List<Payv2PayType> typeList = payv2PayTypeService.query(map);
		//获取支付通道
		map.put("channelId", channelId);
		List<Payv2BussSupportPayWay> payWayRateList = payv2BussSupportPayWayService.queryByCompany(map);
		mv.addObject("companyList", companyList);
		mv.addObject("payWayRateList", payWayRateList);
		mv.addObject("typeList", typeList);
    	mv.addObject("payList", payList);
		return mv;
	}
	
	/**
	 * 查询商户的app
	 * 
	 * @param map
	 * @return List<Payv2PayOrderRefund>
	 */
	@ResponseBody
	@RequestMapping("queryApps")
	public Map<String, Object> queryApps(@RequestParam Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
			payv2BussCompanyApp.setCompanyId(Long.valueOf(map.get("companyId").toString()));
			List<Payv2BussCompanyApp> appsList = payv2BussCompanyAppService.selectByObject(payv2BussCompanyApp);
			resultMap.put("appsList", appsList);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
	}
	
	/**
	 * 查询支付通道列表
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("queryRatesByCompany")
	public Map<String, Object> queryRatesByCompany(@RequestParam Map<String, Object> map) throws Exception {		
		Map<String,Object> resultMap = new HashMap<>();
		try {
			Long channelId = getAdmin().getId();
			map.put("channelId", channelId);
			List<Payv2BussSupportPayWay> list = payv2BussSupportPayWayService.queryByCompany(map);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, list);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}		
		return resultMap;
	}
	
	/**
	 * 根据搜索条件查询退款订单分页列表
	 * 
	 * @param map
	 * @return List<Payv2PayOrderRefund>
	 */
	@ResponseBody
	@RequestMapping("queryRefunds")
	public Map<String, Object> queryRefunds(@RequestParam Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			Long channelId = getAdmin().getId();
			map.put("channelId", channelId);
			PageObject<Payv2PayOrderRefundVO> payOrderRefundVOList = payv2PayOrderRefundService.queryRefunds(map);
			for(Payv2PayOrderRefundVO payv2PayOrderRefundVO : payOrderRefundVOList.getDataList()){
				payv2PayOrderRefundVO.setRateName("");
			}
			resultMap.put("payOrderRefundVOList", payOrderRefundVOList);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
	}
	
	/**
	 * 退款订单详情页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@RequestMapping("refundDetail")
	public ModelAndView refundDetail(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/businessManager/payv2OrderRefundDetail");
    	mv.addObject("dataMap", map);
		return mv;
	}
	
	
	
	/**
	 * @param map form页面搜索条件
	 * @param workbook
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping("refundsExport")
	public Map<String, Object> refundsExport(@RequestParam Map<String, Object> map, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		// 根据搜索条件搜索出退款账单
		map.put("curPage", 1);
		map.put("pageData", 999999999);
		Long channelId = getAdmin().getId();
		map.put("channelId", channelId);
		PageObject<Payv2PayOrderRefundVO> pageObject = payv2PayOrderRefundService.queryRefunds(map);
		List<Payv2PayOrderRefundVO> payOrderRefundVOList = pageObject.getDataList();
		// 将退款导出实体类的集合出给导出工具类
		// 将搜索出的退款账单中的账单对象循环取出，去对象中需要导出的字段放到退款导出实体类中
		if(payOrderRefundVOList.size() > 0){			
			try {
				// 导出
				TestExportExcel<OrderRefundExportVO> ex = new TestExportExcel<OrderRefundExportVO>();
				String[] headers = { "退款订单号", "平台订单号", "商户订单号", "来源商户", "来源应用", "支付平台", "支付方式","支付通道", "商品名称", "订单金额(元)", "退款金额", "手续费", "订单状态", "交易时间", "退款时间" };

				List<OrderRefundExportVO> dataset = new ArrayList<OrderRefundExportVO>();
				for (Payv2PayOrderRefundVO payv2PayOrderRefundVO : payOrderRefundVOList) {
					OrderRefundExportVO bte = new OrderRefundExportVO();
					//退款订单号
					bte.setRefundNum(payv2PayOrderRefundVO.getRefundNum());
					//平台订单号
					bte.setOrderNum(payv2PayOrderRefundVO.getOrderNum());
					//商户订单号
					bte.setMerchantOrderNum(payv2PayOrderRefundVO.getMerchantOrderNum());
					//来源商户
					bte.setCompanyName(payv2PayOrderRefundVO.getCompanyName());
					//来源应用
					bte.setAppName(payv2PayOrderRefundVO.getAppName());
					//支付平台
					bte.setPayWayName(payv2PayOrderRefundVO.getWayName());
					//支付方式
					bte.setRateTypeName(payv2PayOrderRefundVO.getPayTypeName());
					//支付通道
					bte.setRateName(payv2PayOrderRefundVO.getPayWayName());
					//商品名称
					bte.setGoodsName(payv2PayOrderRefundVO.getGoodsName());
					//订单金额(元)
					bte.setPayMoney(payv2PayOrderRefundVO.getPayMoney());
					//退款金额
					bte.setRefundMoney(payv2PayOrderRefundVO.getRefundMoney());
					//手续费
					bte.setPayWayMoney(payv2PayOrderRefundVO.getPayWayMoney());
					//交易时间
					bte.setPayTime(payv2PayOrderRefundVO.getPayTime());
					//退款时间
					bte.setRefundTime(payv2PayOrderRefundVO.getRefundTime());
					dataset.add(bte);
				}				
				OutputStream out = response.getOutputStream();
				String filename = "退款订单" + new Date().getTime() + ".xls";// 设置下载时客户端Excel的名称
				filename = URLEncoder.encode(filename, "UTF-8");// 处理中文文件名
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
				ex.exportExcel(headers, dataset, out);
				out.close();				
			} catch (Exception e) {
				logger.error("导出客户信息Excel失败", e);
				e.printStackTrace();
				returnMap.put("status", -1);// 失败
			}
		}		
		return returnMap;
	}
}
