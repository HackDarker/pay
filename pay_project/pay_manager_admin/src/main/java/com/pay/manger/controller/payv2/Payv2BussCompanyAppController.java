package com.pay.manger.controller.payv2;

import java.io.OutputStream;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.mapper.Payv2BussCompanyAppMapper;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.payv2.entity.Payv2AppType;
import com.pay.business.payv2.service.Payv2AppTypeService;
import com.pay.business.payv2.service.Payv2BussAppSupportPayWayService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.mail.MailRun;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * 
 * @ClassName: Payv2BussCompanyAppController
 * @Description: 商户APP表
 * @author mofan
 * @date 2016年12月1日 15:13:11
 */
@Controller
@RequestMapping("/payv2BussCompanyApp")
public class Payv2BussCompanyAppController extends BaseManagerController<Payv2BussCompanyApp, Payv2BussCompanyAppMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2BussAppSupportPayWayService payv2BussAppSupportPayWayService;

	@Autowired
	private Payv2AppTypeService payv2AppTypeService;
    @Autowired
    private SysLogService sysLogService;
	
	/**
	 * 商户APP列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/payv2BussCompanyAppList")
	public ModelAndView payv2BussCompanyAppList(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("payv2/app/pay_app_list");
		map.put("isDelete", "2");
		PageObject<Payv2BussCompanyApp> pageObject = payv2BussCompanyAppService.payv2BussCompanyAppList(map);
		mv.addObject("list", pageObject);
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
//		payv2BussCompany.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
		mv.addObject("companyList", companyList);
		mv.addObject("map", map);
		return mv;
	}

	/**
	 * APP导出
	 */
	@RequestMapping("/exportExcel")
	public String exportExcel(@RequestParam Map<String, Object> map, HttpServletResponse response){
		map.put("isDelete", "2");
		map.put("curPage", 0);
		map.put("pageData", 999999);
		List<Payv2BussCompanyApp> list = payv2BussCompanyAppService.payv2BussCompanyAppList(map).getDataList();
		if(list.size()>0){
			
			try {
				String[] head = {"所属公司", "APP名称", "应用官网", "APPKey", "应用行业", "应用说明", "Android下载地址", "Android应用签名", "Android包名", "Iphone AppStore下载地址", "Iphone Bundle ID", "Iphone 测试版本Bundle ID"};
				//文件名、头信息
				String fileName = new String("APP列表.xls".getBytes("GBK"), "ISO8859_1");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
				response.setDateHeader("Expires", 0);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				
				int exportRow = 1;
				HSSFSheet sheet = null;
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFFont f = wb.createFont();
				sheet = wb.createSheet("APP列表");
				HSSFRow row = sheet.createRow(0);
				HSSFCell cell = null;
				//HSSFDataFormat format= wb.createDataFormat();
				HSSFCellStyle style = wb.createCellStyle();
				//style.setDataFormat(format.getFormat("yyyy年m月d日"));
				style.setWrapText(true);// 设置自动换行
				for (int i = 0; i < head.length; i++) {//设置宽度
					sheet.setColumnWidth(i, 4500);
				}
				f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 粗体显示
				style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
				sheet.autoSizeColumn(1, true);
				style.setFont(f);
				sheet.autoSizeColumn(1);
				
				for (int i = 0; i < head.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(head[i]); 
					cell.setCellStyle(style);
				}
				
				for (Payv2BussCompanyApp app : list) {
					row = sheet.createRow(exportRow);					
					row.createCell(0).setCellValue(app.getCompanyName());//所属公司
					row.createCell(1).setCellValue(app.getAppName());//APP名称
					row.createCell(2).setCellValue(app.getWebUrl());//应用官网					
					row.createCell(3).setCellValue(app.getAppKey());//APPKey
					row.createCell(4).setCellValue(app.getTypeName());//------应用行业					
					row.createCell(5).setCellValue(app.getAppDesc());//应用说明
					row.createCell(6).setCellValue(app.getAndroidAppUrl());//Android下载地址
					row.createCell(7).setCellValue(app.getAndroidAppMd5());//Android应用签名			
					row.createCell(8).setCellValue(app.getAndroidAppPackage());//Android包名
					row.createCell(9).setCellValue(app.getIosIphoneUrl());//Iphone AppStore下载地址
					row.createCell(10).setCellValue(app.getIosIphoneId());//Iphone Bundle ID
					row.createCell(11).setCellValue(app.getIosIpaTestId());//Iphone 测试版本Bundle ID
					exportRow++;
				}
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.flush();  
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 从商户管理进到的商户APP列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/toPayv2BussCompanyAppList")
	public ModelAndView toPayv2BussCompanyAppList(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("payv2/app/pay_from_company_app_list");
		map.put("isDelete", "2");
		PageObject<Payv2BussCompanyApp> pageObject = payv2BussCompanyAppService.payv2BussCompanyAppList(map);
		mv.addObject("list", pageObject);
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		payv2BussCompany.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
		map.put("merchantType", 1);
		mv.addObject("companyList", companyList);
		mv.addObject("map", map);
		return mv;
	}

	/**
	 * 查看商户APP未通过原因
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/toViewFailReason")
	public ModelAndView toViewFailReason(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_app_view");
		Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
		try {
			if (null != map.get("id")) {
				payv2BussCompanyApp = payv2BussCompanyAppService.detail(map);
				mvc.addObject("payv2BussCompanyApp", payv2BussCompanyApp);
			}
		} catch (Exception e) {
			logger.error(" 查看商户APP页面报错", e);
		}
		return mvc;
	}

	/**
	 * 审核
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/toApprove")
	public ModelAndView toApprove(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_app_approve");
		mvc.addObject("map", map);
		return mvc;
	}

	/**
	 * 从商户管理进到的审核
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/toFromCompanyApprove")
	public ModelAndView toFromCompanyApprove(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_from_company_app_approve");
		try {
			if (null != map.get("id")) {
				Payv2BussCompanyApp payv2BussCompanyApp = payv2BussCompanyAppService.detail(map);
				mvc.addObject("payv2BussCompanyApp", payv2BussCompanyApp);

			}
		} catch (Exception e) {
			logger.error(" 跳转商户APP编辑页面报错", e);
		}
		mvc.addObject("map", map);
		return mvc;
	}

	/**
	 * 编辑商户APP
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/toEditPayv2BussCompanyApp")
	public ModelAndView toEditPayv2BussCompanyApp(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_app_edit");
		Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
		try {
			if (null != map.get("id")) {
				payv2BussCompanyApp = payv2BussCompanyAppService.detail(map);
				mvc.addObject("payv2BussCompanyApp", payv2BussCompanyApp);
				Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
				List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
				mvc.addObject("companyList", companyList);
			}
		} catch (Exception e) {
			logger.error(" 跳转商户APP编辑页面报错", e);
		}
		return mvc;
	}

	/**
	 * 查看商户APP下载
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/toDownload")
	public ModelAndView toDownload(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_app_download");
		Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
		try {
			if (null != map.get("id")) {
				payv2BussCompanyApp = payv2BussCompanyAppService.detail(map);
				mvc.addObject("payv2BussCompanyApp", payv2BussCompanyApp);
			}
		} catch (Exception e) {
			logger.error(" 查看商户APP页面报错", e);
		}
		return mvc;
	}

	/**
	 * 编辑商户APP
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updatePayv2BussCompanyApp", method = RequestMethod.POST)
	public Map<String, Object> updatePayv2BussCompanyApp(@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (map.get("id") != null) {
			try {
				map.put("updateTime", new Date());
				payv2BussCompanyAppService.update(map);
				// 向用户发送邮箱
				Payv2BussCompanyApp app = new Payv2BussCompanyApp();
				app.setId(Long.valueOf(map.get("id").toString()));
				app = payv2BussCompanyAppService.selectSingle(app);
				if(app != null && "3".equals(map.get("appStatus"))){
					Payv2BussCompany company = new Payv2BussCompany();
					company.setId(Long.valueOf(app.getCompanyId()));
					company = payv2BussCompanyService.selectSingle(company);
					if(company != null){
						MailRun.appSend(company.getCompanyName(),app.getAppName(),app.getAppPassReason(),company.getContactsMail() == null?"":company.getContactsMail());
					}				
				}
				//添加日志
				if(map.containsKey("appStatus")) {
					if(map.get("appStatus").toString().equals("2")) {
						sysLogService.addSysLog(1, LogTypeEunm.T22, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					} else if(map.get("appStatus").toString().equals("4")) {
						sysLogService.addSysLog(1, LogTypeEunm.T21, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					}
				}
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
			} catch (Exception e) {
				logger.error("修改商户APP提交失败", e);
				e.printStackTrace();
				resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, "修改商户APP提交失败!");
			}
		}
		return resultMap;
	}

	/**
	 * 审核商户通过
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/approveAgreePayv2BussCompanyApp")
	public Map<String, Object> approveAgreePayv2BussCompanyApp(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			map.put("updateTime", new Date());
			payv2BussCompanyAppService.update(map);
			// 向用户发送邮箱
			Payv2BussCompanyApp app = new Payv2BussCompanyApp();
			app.setId(Long.valueOf(map.get("id").toString()));
			app = payv2BussCompanyAppService.selectSingle(app);
			if(app != null){
				Payv2BussCompany company = new Payv2BussCompany();
				company.setId(Long.valueOf(app.getCompanyId()));
				company = payv2BussCompanyService.selectSingle(company);
				if(company != null){
					MailRun.appSend(company.getCompanyName(),app.getAppName(),company.getContactsMail() == null?"":company.getContactsMail());				
				}				
			}
			//添加日志
			sysLogService.addSysLog(1, LogTypeEunm.T20, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
			resultMap.put("companyId", map.get("companyId"));
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, "审核商户异常！");
		}
		return resultMap;
	}

	/**
	 * app详情
	 * @param map
	 * @return
	 */
	@RequestMapping("/detail")
	public ModelAndView detail(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/app/pay_app_detail_new");
		try {
			if (null != map.get("id")) {
				Payv2BussCompanyApp payv2BussCompanyApp = payv2BussCompanyAppService.detail(map);
				Map<String,Object> param =new HashMap<>();
				param.put("id", payv2BussCompanyApp.getCompanyId());
				Payv2BussCompany payv2BussCompany = payv2BussCompanyService.detail(param);
				if(payv2BussCompanyApp.getAppTypeId()!=null){
					param.put("id", payv2BussCompanyApp.getAppTypeId());
					Payv2AppType payv2AppType = payv2AppTypeService.detail(param);
					mvc.addObject("typeName", payv2AppType.getTypeName());
				}
				String appImg = payv2BussCompanyApp.getAppImg();
				//应用截图
				if(appImg!=null&&!"".equals(appImg)){
					String appImgs [] = appImg.split(",");
					mvc.addObject("appImgs", appImgs);
				}
				String appDescFile = payv2BussCompanyApp.getAppDescFile();
				//应用说明文档
				if(appDescFile!=null&&!"".equals(appDescFile)){
					String appDescFiles [] = appDescFile.split(",");
					mvc.addObject("appDescFiles", appDescFiles);
				}
				
				String appCopyright = payv2BussCompanyApp.getAppCopyright();
				//应用说明文档
				if(appCopyright!=null&&!"".equals(appCopyright)){
					String appCopyrights [] = appCopyright.split(",");
					mvc.addObject("appCopyrights", appCopyrights);
				}
				
				mvc.addObject("obj", payv2BussCompanyApp);
				mvc.addObject("companyName", payv2BussCompany.getCompanyName());
			}
		} catch (Exception e) {
			logger.error(" 查看商户APP对应的订单出错", e);
		}
		return mvc;
	}
	
}
