package com.pay.manger.controller.payv2;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.date.DateStyle;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.csvreader.CsvWriter;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.order.entity.OrderRefundExportVO;
import com.pay.business.order.entity.Payv2PayOrderRefundVO;
import com.pay.business.order.mapper.Payv2PayOrderRefundMapper;
import com.pay.business.order.service.Payv2PayOrderRefundService;
import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayTypeService;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.util.CSVUtils;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.ExportExcel.TestExportExcel;
import com.pay.manger.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2PayOrderRefund/*")
public class Payv2PayOrderRefundController extends BaseManagerController<Payv2PayOrderRefundVO, Payv2PayOrderRefundMapper> {

	@Autowired
	private Payv2ChannelService payv2ChannelService;//渠道商服务	
	@Autowired
	private Payv2PayWayService payv2PayWayService;//支付渠道服务	
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;//支付渠道服务	
	@Autowired
	private Payv2PayOrderRefundService payv2PayOrderRefundService;//退款订单服务
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;
	@Autowired
    private Payv2PayTypeService payv2PayTypeService;
	
	private static final Logger logger = Logger.getLogger(Payv2PayOrderRefundController.class);
	/**
	 * 交易汇总默认加载页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@RequestMapping("alldata")
	public ModelAndView showDetailDay(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/businessmanage/payv2_bussiness_manage_order_refund");		
		//获取渠道商列表
    	Payv2Channel payv2Channel=new Payv2Channel();
//    	payv2Channel.setIsDelete(2);
    	List<Payv2Channel>	payv2ChannelList=payv2ChannelService.selectByObject(payv2Channel);
    	// 获取商户列表
    	Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
//		payv2BussCompany.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
    	// 获取应用列表    	
    	Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
//    	payv2BussCompanyApp.setIsDelete(2);
    	List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(payv2BussCompanyApp);
    	//获取支付平台列表
    	Payv2PayWay pay = new Payv2PayWay();
//		pay.setIsDelete(2);
		List<Payv2PayWay> payList = payv2PayWayService.selectByObject(pay);
		//获取支付方式列表
		List<Payv2PayType> typeList = payv2PayTypeService.query(map);
		//获取支付通道
//		map.put("status", 1);
//		map.put("isDelete", 2);
		List<Payv2PayWayRate> payWayList =payv2PayWayRateService.query(map);
		mv.addObject("payWayList", payWayList);
		mv.addObject("companyList", companyList);
		mv.addObject("appList", appList);
    	mv.addObject("payv2ChannelList", payv2ChannelList);
    	mv.addObject("payList", payList);
    	mv.addObject("typeList", typeList);
		return mv;
	}	
	/**
	 * 根据搜索条件查询退款订单分页刘表
	 * 
	 * @param map
	 * @return List<Payv2PayOrderRefund>
	 */
	@ResponseBody
	@RequestMapping("queryRefunds")
	public Map<String, Object> queryRefunds(@RequestParam Map<String, Object> map) {
		Map<String,Object> resultMap = new HashMap<>();
		try {
			PageObject<Payv2PayOrderRefundVO> porvList = payv2PayOrderRefundService.queryRefunds(map);
			resultMap.put("payOrderRefundVOList", porvList);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取退款订单失败", e);
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
		ModelAndView mv = new ModelAndView("payv2/businessmanage/payv2_refund_detail");
		Payv2PayOrderRefundVO refund = payv2PayOrderRefundService.refundDetail(map);
    	mv.addObject("refund", refund);
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
		PageObject<Payv2PayOrderRefundVO> pageObject = payv2PayOrderRefundService.queryRefunds(map);
		List<Payv2PayOrderRefundVO> payOrderRefundVOList = pageObject.getDataList();
		if(payOrderRefundVOList.size() > 0){			
			try {
				String[] headers = { "退款订单号", "平台订单号", "商户订单号", "来源商户", "来源应用", "支付平台", "支付方式","支付通道","支付通道编号", "商品名称", "订单金额(元)", "退款金额", "手续费", "订单状态", "交易时间", "退款时间" };
				//文件名、头信息
				String fileName = new String("商户列表.xls".getBytes("GBK"), "ISO8859_1");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.setDateHeader("Expires", 0);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				
				int exportRow = 1;
				HSSFSheet sheet = null;
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFFont f = wb.createFont();
				sheet = wb.createSheet("商户列表");
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = null;
				HSSFCellStyle style = wb.createCellStyle();
				style.setWrapText(true);// 设置自动换行
				for (int i = 0; i < headers.length; i++) {//设置宽度
					sheet.setColumnWidth(i, 4500);
				}
				f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
				sheet.autoSizeColumn(1, true);
				style.setFont(f);
				sheet.autoSizeColumn(1);
				
				for (int i = 0; i < headers.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(headers[i]); 
					cell.setCellStyle(style);
				}
				for (Payv2PayOrderRefundVO refund : payOrderRefundVOList) {
					row = sheet.createRow(exportRow);
					row.createCell(0).setCellValue(refund.getRefundNum());//退款订单号					
					row.createCell(1).setCellValue(refund.getOrderNum());//平台订单号
					row.createCell(2).setCellValue(refund.getMerchantOrderNum());//商户订单号					
					row.createCell(3).setCellValue(refund.getCompanyName());//来源商户							
					row.createCell(4).setCellValue(refund.getAppName());//来源应用
					row.createCell(5).setCellValue(refund.getWayName());//支付平台
					row.createCell(6).setCellValue(refund.getPayTypeName());//支付方式						
					row.createCell(7).setCellValue(refund.getRateName());//支付通道									
					row.createCell(8).setCellValue(refund.getPayWayName());//支付通道编号
					row.createCell(9).setCellValue(refund.getGoodsName());//商品名称
					row.createCell(10).setCellValue(refund.getPayMoney());//订单金额(元)
					row.createCell(11).setCellValue(refund.getRefundMoney());//退款金额
					row.createCell(12).setCellValue(refund.getPayWayMoney());//手续费
					row.createCell(13).setCellValue("退款成功");//订单状态
					row.createCell(14).setCellValue(DateUtil.DateToString(refund.getPayTime(), DateStyle.YYYY_MM_DD_HH_MM_SS));//交易时间
					row.createCell(15).setCellValue(DateUtil.DateToString(refund.getRefundTime(), DateStyle.YYYY_MM_DD_HH_MM_SS));//退款时间
					exportRow++;
				}
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.flush();  
				out.close();
			} catch (Exception e) {
				logger.error("导出退款订单Excel失败", e);
				e.printStackTrace();
				returnMap.put("status", -1);// 失败
			}
		}		
		return returnMap;
	}
}
