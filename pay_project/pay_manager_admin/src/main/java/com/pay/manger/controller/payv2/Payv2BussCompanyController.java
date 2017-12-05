package com.pay.manger.controller.payv2;

import java.io.OutputStream;
import java.util.ArrayList;
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

import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussSupportPayWay;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.entity.Payv2CompanyPayType;
import com.pay.business.merchant.mapper.Payv2BussCompanyMapper;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.merchant.service.Payv2CompanyPayTypeService;
import com.pay.business.payv2.entity.Payv2Bank;
import com.pay.business.payv2.entity.Payv2BussTrade;
import com.pay.business.payv2.entity.Payv2ProvincesCity;
import com.pay.business.payv2.service.Payv2BankService;
import com.pay.business.payv2.service.Payv2BussTradeService;
import com.pay.business.payv2.service.Payv2ProvincesCityService;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.GenerateUtil;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.mail.MailRun;
import com.pay.manger.controller.admin.BaseManagerController;

/**
* @ClassName: Payv2BussCompanyController 
* @Description:商户控制前
* @author mofan
* @date 2016年12月8日 下午18:21:52
*/
@Controller
@RequestMapping("/payv2BussCompany/*")
public class Payv2BussCompanyController extends BaseManagerController<Payv2BussCompany, Payv2BussCompanyMapper>{

	private static final Logger logger = Logger.getLogger(Payv2BussCompanyController.class);
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@Autowired
	private Payv2BussTradeService payv2BussTradeService;
	
	@Autowired
	private Payv2ProvincesCityService payv2ProvincesCityService;
	@Autowired
	private Payv2ChannelService payv2ChannelService;//渠道商
	
	@Autowired
	private Payv2BussSupportPayWayService payv2BussSupportPayWayService;//商户支持的支付通道表
	
	@Autowired
	private Payv2PayWayService payv2PayWayService;
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;
	@Autowired
	private Payv2BankService payv2BankService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private Payv2CompanyPayTypeService payv2CompanyPayTypeService;
    @Autowired
    private Payv2BussCompanyAppService payv2BussCompanyAppService;
    @Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	/**
	 * 商户列表
	 * @param map
	 * @return
	 */
    @RequestMapping("/companyList")
    public ModelAndView companyList(@RequestParam Map<String, Object> map,HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("payv2/company/pay_company_list");
        map.put("isDelete", 2);//未删除
        //分页列表
        PageObject<Payv2BussCompany> pageObject = payv2BussCompanyService.companyList(map);
        //获取配置支付通道个数
        List<Payv2BussCompany> comapnyList=  pageObject.getDataList();
        for (Payv2BussCompany payv2BussCompany : comapnyList) {
        	Payv2BussSupportPayWay payv2BussSupportPayWay=new Payv2BussSupportPayWay();
        	payv2BussSupportPayWay.setParentId(payv2BussCompany.getId());
        	payv2BussSupportPayWay.setIsDelete(2);
        	List<Payv2BussSupportPayWay> SupportPayWayList=	payv2BussSupportPayWayService.selectByObject(payv2BussSupportPayWay);
        	if(SupportPayWayList.size()>0){
        		payv2BussCompany.setSupportPayWayNum(SupportPayWayList.size());
        	}else{
        		payv2BussCompany.setSupportPayWayNum(0);
        	}
		}
        map.put("parentId", 0);
        //省
    	List<Payv2ProvincesCity> provincesList = payv2ProvincesCityService.query(map);
    	if(map.get("companyRangeCity")!=null&&!map.get("companyRangeCity").equals("")){
    		map.put("parentId", map.get("companyRangeProvince"));
    		//市
    		List<Payv2ProvincesCity> cityList = payv2ProvincesCityService.query(map);
    		mv.addObject("cityList", cityList); 
    	}
    	//获取渠道商列表
    	Payv2Channel payv2Channel=new Payv2Channel();
//    	payv2Channel.setIsDelete(2);
    	List<Payv2Channel>	payv2ChannelList=payv2ChannelService.selectByObject(payv2Channel);
    	pageObject.setDataList(comapnyList);
    	mv.addObject("payv2ChannelList", payv2ChannelList); 
        mv.addObject("list", pageObject); 
        mv.addObject("provincesList", provincesList); 
        mv.addObject("map", map); 
        return mv;
    }
    
    @RequestMapping("/exportExcel")
	public String exportExcel(@RequestParam Map<String, Object> map, HttpServletResponse response){
    	map.put("isDelete", "2");
		map.put("curPage", 0);
		map.put("pageData", 999999);
		List<Payv2BussCompany> list = payv2BussCompanyService.companyList(map).getDataList();
		if (list.size()>0) {
			
			try {
				String[] head = {"商户号", "公司名称", "公司行业", "公司规模", "营业执照号", "注册地址", "组织机构代码", "营业类型", "法人名称", "法人身份证号", "联系人姓名", "联系人电话", "联系人邮箱", "开户机构", "账户类型", "收款商户名", "收款账号", "到账类型", "到账日期"};
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
				
				for (Payv2BussCompany com : list) {
					row = sheet.createRow(exportRow);
					row.createCell(0).setCellValue(com.getCompanyKey());//商户号
					
					row.createCell(1).setCellValue(com.getCompanyName());//公司名称
					row.createCell(2).setCellValue(com.getTradeName());//公司行业
					if(com.getCompanyScale()==1){
						row.createCell(3).setCellValue("大型");//公司规模
					}else if(com.getCompanyScale()==2){
						row.createCell(3).setCellValue("中型");//公司规模
					}else if(com.getCompanyScale()==3){
						row.createCell(3).setCellValue("小型");//公司规模
					}else if(com.getCompanyScale()==4){
						row.createCell(3).setCellValue("微型");//公司规模
					}else{
						row.createCell(3).setCellValue("个体户");//公司规模
					}					
					row.createCell(4).setCellValue(com.getLicenseNum());//营业执照号
					row.createCell(5).setCellValue(com.getLicenseAddr());//注册地址
					row.createCell(6).setCellValue(com.getOrganizationCode());//组织机构代码					
					if(com.getLicenseType()==1){
						row.createCell(7).setCellValue("线上（互联网）");//营业类型
					}else if(com.getLicenseType()==2){
						row.createCell(7).setCellValue("线下（实体店铺）");//营业类型
					}else{
						row.createCell(7).setCellValue("线上+线下");//营业类型
					}					
					row.createCell(8).setCellValue(com.getLegalName());//法人名称
					row.createCell(9).setCellValue(com.getLegalIdCard());//法人身份证号					
					row.createCell(10).setCellValue(com.getContactsName());//联系人姓名					
					row.createCell(11).setCellValue(com.getContactsPhone());//联系人电话
					row.createCell(12).setCellValue(com.getContactsMail());//联系人邮箱					
					row.createCell(13).setCellValue(com.getAccountBank());//开户机构
					if(com.getAccountType()==1){
						row.createCell(14).setCellValue("对公账户");//账户类型
					}else{
						row.createCell(14).setCellValue("对私账户");//账户类型
					}					
					row.createCell(15).setCellValue(com.getAccountName());//收款商户名
					row.createCell(16).setCellValue(com.getAccountCard());//收款账号
					if(com.getWayArrivalType()==1){
						row.createCell(17).setCellValue("T+日期（工作日）");//到账类型
					}else if(com.getWayArrivalType()==2){
						row.createCell(17).setCellValue("实时到账");//到账类型
					}else{
						row.createCell(17).setCellValue("T+日期");//到账类型
					}					
					row.createCell(18).setCellValue(com.getWayArrivalValue());//到账日期
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
     * 商户列表
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/merchantList")
    public Map<String,Object> merchantList(@RequestParam Map<String, Object> map){
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		List<Payv2BussCompany> provincesList = payv2BussCompanyService.query(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, provincesList);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
    }
    
    /**
     * 省市联动
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/proCity")
    public Map<String,Object> proCity(@RequestParam Map<String, Object> map){
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		List<Payv2ProvincesCity> provincesList = payv2ProvincesCityService.query(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, provincesList);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
    }
    
    /**
     * 查看商户商铺
     * @param map
     * @return
     */
    @RequestMapping("/toViewPayv2BussCompany")
    public ModelAndView toViewPayv2BussCompany(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/company/pay_company_edit");
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		try {
			if (null != map.get("id")) {
				payv2BussCompany = payv2BussCompanyService.detail(map);
				map.remove("id");
	    		//行业
	    		List<Payv2BussTrade> list = payv2BussTradeService.query(map);
	    		if(payv2BussCompany.getCompanyRangeProvince()!=null){
	    			map.put("parentId", payv2BussCompany.getCompanyRangeProvince());
	    			//市
	    			List<Payv2ProvincesCity> cityList = payv2ProvincesCityService.query(map);
	    			mvc.addObject("cityList", cityList);
	        	}
	        	map.put("parentId", 0);
	        	//省
	        	List<Payv2ProvincesCity> provincesList = payv2ProvincesCityService.query(map);
	        	mvc.addObject("tradeList", list);
	        	mvc.addObject("provincesList", provincesList);
				mvc.addObject("obj", payv2BussCompany);
			}
		} catch (Exception e) {
			logger.error(" 查看商户商铺页面报错", e);
		}
		return mvc;
    }
    
    /**
     * 查看商户商铺
     * @param map
     * @return
     */
    @RequestMapping("/toViewFailReason")
    public ModelAndView toViewFailReason(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/company/pay_company_view");
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		try {
			if (null != map.get("id")) {
				payv2BussCompany = payv2BussCompanyService.detail(map);
				mvc.addObject("payv2BussCompany", payv2BussCompany);
			}
		} catch (Exception e) {
			logger.error(" 查看商户商铺页面报错", e);
		}
		return mvc;
    }
    
    /**
     * 审核
     * @param map
     * @return
     */
    @RequestMapping("/toApprove")
    public ModelAndView toApprove(@RequestParam Map<String, Object> map) {
		ModelAndView mvc = new ModelAndView("payv2/company/pay_company_approve");
		mvc.addObject("map", map);
		return mvc;
    }
    
    /**
     * 查看详情
     * @param map
     * @return
     */
    @RequestMapping("/viewDetail")
    public ModelAndView viewDetail(@RequestParam Map<String, Object> map){
    	ModelAndView mv = new ModelAndView("payv2/company/pay_company_view_handle_new");
    	try {
    		//商户详情
    		Payv2BussCompany obj = payv2BussCompanyService.detail(map);
    		map.remove("id");
    		//行业
    		List<Payv2BussTrade> list = payv2BussTradeService.query(map);
    		if(obj.getCompanyRangeProvince()!=null){
    			map.put("parentId", obj.getCompanyRangeProvince());
    			//市
    			List<Payv2ProvincesCity> cityList = payv2ProvincesCityService.query(map);
    			mv.addObject("cityList", cityList);
        	}
        	map.put("parentId", 0);
        	//省
        	List<Payv2ProvincesCity> provincesList = payv2ProvincesCityService.query(map);
        	
        	//开户机构    银行列表
        	List<Payv2Bank> bankList = payv2BankService.query(map);
        	
        	//商户支持的支付通道和路由
        	/*Payv2BussSupportPayWay pbspw = new Payv2BussSupportPayWay();
        	pbspw.setParentId(obj.getId());
        	pbspw.setIsDelete(2);
        	pbspw.setPayWayStatus(1);
        	List<Payv2BussSupportPayWay> wayList = payv2BussSupportPayWayService.selectByObject(pbspw);
        	
        	//路由列表
        	Map<String,Object> param = new HashMap<>();
        	for (Payv2BussSupportPayWay payv2BussSupportPayWay : wayList) {
            	param.put("payWayId", payv2BussSupportPayWay.getPayWayId());
//				param.put("channelId", obj.getChannelId());
				List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
				
				payv2BussSupportPayWay.setRateList(rateList);
			}
        	Map<String, Object> paramMap = new HashMap<String, Object>();
        	List<Payv2PayWay> payList = payv2PayWayService.query(paramMap);
        	
        	mv.addObject("payList", payList);
        	mv.addObject("wayList", wayList);*/
        	mv.addObject("bankList", bankList);
        	mv.addObject("tradeList", list);
        	mv.addObject("provincesList", provincesList);
    		mv.addObject("obj", obj);
    		mv.addObject("map", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return mv;
    }
    
    @RequestMapping("/companyHandleView")
    public ModelAndView companyHandleView(@RequestParam Long companyId) {
    	//response.setHeader("Access-Control-Allow-Origin", "*" ); 
    	ModelAndView mvc = new ModelAndView("payv2/company/pay_company_view_handle");
    	Map<String,Object> map = new HashMap<>();
    	map.put("id",companyId);
    	Payv2BussCompany detail = payv2BussCompanyService.detail(map);
		mvc.addObject("obj", detail);
		return mvc;
    }
    
    @RequestMapping("/companyHandle")
    @ResponseBody
    public Map<String,Object> companyHandle(@RequestParam Map<String, Object> map,HttpServletRequest request) {
    	/*Long companyId = NumberUtils.createLong(request.getParameter("companyId"));
    	Integer type = NumberUtils.createInteger(request.getParameter("type"));*/
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		if("1".equals(map.get("type").toString())){
        		//通过
    			map.put("id",Long.valueOf(map.get("companyId").toString()));
    			map.put("companyStatus", 2);
        		map.put("updateTime", new Date());
        		payv2BussCompanyService.update(map);
        		// 向用户发送邮箱
				Payv2BussCompany company = new Payv2BussCompany();
				company.setId(Long.valueOf(map.get("companyId").toString()));
				company = payv2BussCompanyService.selectSingle(company);
				if(company != null){
					MailRun.companySend(company.getCompanyName(),company.getContactsMail() == null ? "" : company.getContactsMail());				
				}
				//记录日志
				sysLogService.addSysLog(1, LogTypeEunm.T16, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
        		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null,"审核通过成功");
        	}else if("2".equals(map.get("type").toString())){
        		//拒绝
        		map.put("id", Long.valueOf(map.get("companyId").toString()));
    			map.put("companyStatus", 3);
        		map.put("updateTime", new Date());
        		payv2BussCompanyService.update(map);
        		// 向用户发送邮箱
				Payv2BussCompany company = new Payv2BussCompany();
				company.setId(Long.valueOf(map.get("companyId").toString()));
				company = payv2BussCompanyService.selectSingle(company);
				if(company != null){
					MailRun.companySend(company.getCompanyName(),map.get("companyPassReason").toString(),company.getContactsMail() == null ? "" : company.getContactsMail());				
				}
				//记录日志
				sysLogService.addSysLog(1, LogTypeEunm.T16, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
        		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null,"审核通过成功");
        	}else{
        		resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null,"非法操作");
        	}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null,"服务器异常,请稍候再试");
		}
		return resultMap;
    }
    
    /**
     * 修改商户
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/updatePayv2BussCompany")
    public Map<String,Object> updatePayv2BussCompany(@RequestParam Map<String, Object> map,HttpServletRequest request){
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		map.put("updateTime", new Date());
    		if(map.containsKey("companyStatus")) {
    			if(map.get("companyStatus").toString().equals("2")) {
    				sysLogService.addSysLog(1, LogTypeEunm.T18, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
    			} else if (map.get("companyStatus").toString().equals("4")) {
    				
    				payv2BussCompanyAppService.updateByComId(Long.valueOf(map.get("id").toString()));
    				
    				sysLogService.addSysLog(1, LogTypeEunm.T17, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
				}
    		}
    		if(map.containsKey("isDelete")) {
    			//删除此商户的收款码
    			Map<String,Object> paramMap = new HashMap<String,Object>();
    			paramMap.put("companyId", map.get("id"));
    			payv2BussCompanyCodeService.delete(paramMap);
    			sysLogService.addSysLog(1, LogTypeEunm.T66, IpAddressUtil.getIpAddress(request), getAdmin().getId(), paramMap);
    			sysLogService.addSysLog(1, LogTypeEunm.T58, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
    		}
    		payv2BussCompanyService.update(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
    }
    
    /**
     * 审核商户通过
     * @param map
     * @return
     */
    @ResponseBody
    @RequestMapping("/approveAgreePayv2BussCompany")
    public Map<String,Object> approveAgreePayv2BussCompany(@RequestParam Map<String, Object> map,HttpServletRequest request){
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		map.put("updateTime", new Date());
    		map.put("companyKey", GenerateUtil.getRandomString(64));
    		payv2BussCompanyService.update(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
    }
    
    /**
     * 配置商户支付通道
     * @param map
     * @return
     */
    @RequestMapping("/configPayWayRate")
    public ModelAndView configPayWayRate(@RequestParam Map<String, Object> map){
    	ModelAndView mv = new ModelAndView("payv2/company/pay_company_config_way_rate");
    	try {
    		Payv2BussCompany obj = payv2BussCompanyService.detail(map);
    		Payv2BussSupportPayWay pbspw = new Payv2BussSupportPayWay();
			pbspw.setParentId(obj.getId());
			pbspw.setIsDelete(2);
			pbspw.setPayWayStatus(1);
			List<Payv2BussSupportPayWay> wayList = payv2BussSupportPayWayService.selectByObject(pbspw);
    		// 路由列表
			Map<String, Object> param = new HashMap<>();
			String payWayList = "";
			for (Payv2BussSupportPayWay payv2BussSupportPayWay : wayList) {
				if (payv2BussSupportPayWay.getRateId() != null) {
					payWayList += payv2BussSupportPayWay.getPayWayId() + "-" + payv2BussSupportPayWay.getRateId() + "-"
							+ payv2BussSupportPayWay.getPayWayRate()+"-"+payv2BussSupportPayWay.getSortNum()+"-"+payv2BussSupportPayWay.getMaxMoney()+"-"+payv2BussSupportPayWay.getOneMaxMoney() + ",";
				} else {
					payWayList += payv2BussSupportPayWay.getPayWayId() + "-0-" + payv2BussSupportPayWay.getPayWayRate()+"-"+payv2BussSupportPayWay.getSortNum()+"-"+payv2BussSupportPayWay.getMaxMoney()+"-"+payv2BussSupportPayWay.getOneMaxMoney() + ",";
				}
				param.put("payWayId", payv2BussSupportPayWay.getPayWayId());
				param.put("isDelete", 2);
				param.put("status", 1);
//				param.put("channelId", obj.getChannelId());
				List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
				
				payv2BussSupportPayWay.setRateList(rateList);
			}
			if (payWayList != "") {
				payWayList = payWayList.substring(0, payWayList.length() - 1);
			}
			param.clear();
			List<Payv2PayWay> payList = payv2PayWayService.query(param);
			mv.addObject("payList", payList);
			mv.addObject("wayList", wayList);
			mv.addObject("obj", obj);
			mv.addObject("map", map);
			mv.addObject("payWayList", payWayList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return mv;
    }
    
    /**
     * 配置商户支付通道
     * @param map
     * @return
     */
    @RequestMapping("/configPayWayRateV2")
    public ModelAndView configPayWayRateV2(@RequestParam Map<String, Object> map){
    	ModelAndView mv = new ModelAndView("payv2/company/pay_company_config_way_rate_v2");
    	try {
    		Payv2BussCompany obj = payv2BussCompanyService.detail(map);
    		
    		Payv2BussSupportPayWay pbspw = new Payv2BussSupportPayWay();
			pbspw.setParentId(Long.parseLong(map.get("id").toString()));
			pbspw.setIsDelete(2);
			pbspw.setPayWayStatus(1);
//			pbspw.setPayType(Integer.parseInt(String.valueOf(cType.getPayTypeId())));
			List<Payv2BussSupportPayWay> wayList = payv2BussSupportPayWayService.selectByObject(pbspw);

			Map<String, Object> param = new HashMap<>();
			param.put("isDelete", 2);
			param.put("status", 1);
			List<Payv2PayWayRate> allWayList = payv2PayWayRateService.query(param);
			
//			int maxSortNum = 0;
			
    		List<Payv2CompanyPayType> typeList = payv2CompanyPayTypeService.payTypeList(map.get("id").toString());
    		for (Payv2CompanyPayType cType : typeList) {
    			List<Payv2BussSupportPayWay> tempWay = new ArrayList<Payv2BussSupportPayWay>();

    			List<Payv2PayWayRate> tempAllWay = new ArrayList<Payv2PayWayRate>();
//    			int tempSortNum = 0;
    			for (Payv2BussSupportPayWay way : wayList) {
					if(way.getPayType().intValue() == cType.getPayTypeId().longValue()) {
						tempWay.add(way);
					}
//					tempSortNum = way.getSortNum();
//					if(tempSortNum > maxSortNum)
//						maxSortNum = tempSortNum;
				}

    			for (Payv2PayWayRate rate : allWayList) {
					if(rate.getPayType().intValue() == cType.getPayTypeId().longValue()) {
						tempAllWay.add(rate);
					}
				}
    			cType.setWayList(tempWay);
    			cType.setAllWayList(tempAllWay);
			}
    		
    		
    		
    		
    		
    		
    		
    		
    		
    		/*Payv2BussCompany obj = payv2BussCompanyService.detail(map);
    		Payv2BussSupportPayWay pbspw = new Payv2BussSupportPayWay();
			pbspw.setParentId(obj.getId());
			pbspw.setIsDelete(2);
			pbspw.setPayWayStatus(1);
			List<Payv2BussSupportPayWay> wayList = payv2BussSupportPayWayService.selectByObject(pbspw);
    		// 路由列表
			String payWayList = "";
			for (Payv2BussSupportPayWay payv2BussSupportPayWay : wayList) {
				if (payv2BussSupportPayWay.getRateId() != null) {
					payWayList += payv2BussSupportPayWay.getPayWayId() + "-" + payv2BussSupportPayWay.getRateId() + "-"
							+ payv2BussSupportPayWay.getPayWayRate()+"-"+payv2BussSupportPayWay.getSortNum()+"-"+payv2BussSupportPayWay.getMaxMoney()+"-"+payv2BussSupportPayWay.getOneMaxMoney() + ",";
				} else {
					payWayList += payv2BussSupportPayWay.getPayWayId() + "-0-" + payv2BussSupportPayWay.getPayWayRate()+"-"+payv2BussSupportPayWay.getSortNum()+"-"+payv2BussSupportPayWay.getMaxMoney()+"-"+payv2BussSupportPayWay.getOneMaxMoney() + ",";
				}
				param.put("payWayId", payv2BussSupportPayWay.getPayWayId());
				param.put("isDelete", 2);
				param.put("status", 1);
//				param.put("channelId", obj.getChannelId());
				List<Payv2PayWayRate> rateList = payv2PayWayRateService.query(param);
				
				payv2BussSupportPayWay.setRateList(rateList);
			}
			if (payWayList != "") {
				payWayList = payWayList.substring(0, payWayList.length() - 1);
			}
			param.clear();
			List<Payv2PayWay> payList = payv2PayWayService.query(param);
			mv.addObject("payList", payList);
			mv.addObject("wayList", wayList);*/
			mv.addObject("obj", obj);
			mv.addObject("map", map);
//			mv.addObject("maxSortNum", maxSortNum);
			mv.addObject("typeList", typeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return mv;
    }
    
    @ResponseBody
    @RequestMapping("/configCompanyWayRate")
    public Map<String,Object> configCompanyWayRate(@RequestParam Map<String, Object> map,HttpServletRequest request){
    	Map<String,Object> resultMap = new HashMap<>();
    	try {
    		// payWayList 格式：1-2-2,3-0-4,5-6-1
			if (map.get("payWayList") != null) {
				payv2BussSupportPayWayService.configCompanyWayRate(map);
				sysLogService.addSysLog(1, LogTypeEunm.T19, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
				
				/*Map<String,Object> param = new HashMap<>();
				param.put("parentId", map.get("id"));
				// 先删除所有支付通道，再添加
				payv2BussSupportPayWayService.delete(param);
				String payWayList = map.get("payWayList").toString();
				// 解析
				String a[] = payWayList.split(",");
				param.clear();
				//添加支付通道
				boolean isOk =addPayWay(a, Long.parseLong(map.get("id").toString()));
				if (isOk) {
					// 修改商户信息数据提交
					payv2BussCompanyService.update(map);
					sysLogService.addSysLog(1, LogTypeEunm.T19, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
				} else {
					// 排序填写有问题
					resultMap.put("resultCode", 201);
					return resultMap;
				}*/
			} else {
				// 通道费不能为空
				resultMap.put("resultCode", 401);
				return resultMap;
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
    	return resultMap;
    }
    
    /**
	* @Title: addPayWay 
	* @Description: 添加通道
	* @param @param a
	* @param @param companyId    设定文件 
	* @return void    返回类型 
	* @throws
	 */
	public boolean addPayWay(String a[], Long companyId) {
		try {
			Payv2BussSupportPayWay payv2BussSupportPayWay = null;
			for (String string : a) {
				String b[] = string.split("-");
				// 支付平台ID
				Long wayId = Long.valueOf(b[0]);
				// 支付类型ID
				int typeId = Integer.valueOf(b[1]);
				// 通道ID
				Long rateId = Long.valueOf(b[2]);
				// 通道费
				Double payWayRate = Double.valueOf(b[3]);
				// 排序
				int sortNum = Integer.valueOf(b[4]);
				// 限额
				double maxMoney = Double.valueOf(b[5]);
				// 单笔限额（最小值）
				double oneMinMoney = Double.valueOf(b[6]);
				//每笔限额（最大值）
				double oneMaxMoney = Double.valueOf(b[7]);
				

				payv2BussSupportPayWay = new Payv2BussSupportPayWay();
				payv2BussSupportPayWay.setIsDelete(2);
				payv2BussSupportPayWay.setParentId(companyId);
				payv2BussSupportPayWay.setPayWayId(wayId);
				payv2BussSupportPayWay.setPayType(typeId);
				payv2BussSupportPayWay.setRateId(rateId);
				payv2BussSupportPayWay.setSortNum(sortNum);
				payv2BussSupportPayWay.setPayWayRate(payWayRate);
				payv2BussSupportPayWay.setPayWayStatus(1);
				payv2BussSupportPayWay.setCreateTime(new Date());
				payv2BussSupportPayWay.setMaxMoney(maxMoney);
				payv2BussSupportPayWay.setOneMinMoney(oneMinMoney);
				payv2BussSupportPayWay.setOneMaxMoney(oneMaxMoney);
				payv2BussSupportPayWayService.add(payv2BussSupportPayWay);
			}
		} catch (Exception e) {
			logger.info("支付通道添加失败");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
