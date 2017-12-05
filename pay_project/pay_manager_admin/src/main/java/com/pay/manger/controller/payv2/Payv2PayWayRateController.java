package com.pay.manger.controller.payv2;

import java.io.File;
import java.io.FileInputStream;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.admin.entity.SysUcenterAdmin;
import com.pay.business.admin.service.SysUcenterAdminService;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.entity.Payv2PayWayRateVO;
import com.pay.business.payway.mapper.Payv2PayWayRateMapper;
import com.pay.business.payway.service.Payv2PayTypeService;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.sysconfig.entity.SysConfigDictionary;
import com.pay.business.sysconfig.service.SysConfigDictionaryService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.CSVUtils;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.ReaderExcelUtils;
import com.pay.business.util.pinganbank.pay.AddConfig;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * 
 * @ClassName: Payv2PayWayRateController
 * @Description: 支付通道路由
 * @author mofan
 * @date 2016年12月28日 10:13:11
 */
@Controller
@RequestMapping("/payv2PayWayRate")
public class Payv2PayWayRateController extends
		BaseManagerController<Payv2PayWayRate, Payv2PayWayRateMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;
	@Autowired
	private SysConfigDictionaryService sysConfigDictionaryService;
	@Autowired
	private SysUcenterAdminService sysUcenterAdminService;
    @Autowired
    private SysLogService sysLogService;
	
    @Autowired
    private ReaderExcelUtils reu;
	
    @Autowired
    private SysConfigDictionaryService dictionaryService;
    @Autowired
    private Payv2PayWayService payv2PayWayService;
    @Autowired
    private Payv2PayTypeService payv2PayTypeService;
    @Autowired
    private Payv2BussCompanyService payv2BussCompanyService;
    @Autowired
    private Payv2BussSupportPayWayService payv2BussSupportPayWayService;

	/**
	 * 支付通道路由列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/payv2PayWayRateList")
	public ModelAndView payv2PayWayRateList(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/paywayrate/payv2paywayrate-list");
		map.put("isDelete", 2);// 未删除
		map.put("sortName", "update_time");
		map.put("sortOrder", "DESC");
		PageObject<Payv2PayWayRate> pageObject = payv2PayWayRateService.Pagequery(map);
		mv.addObject("list", pageObject);

		Map<String, Object> param = new HashMap<String, Object>();
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		param.put("dictPvalue", -1);
		param.put("dictName", "PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(param);
		if (sysConfigDictionary != null) {
			param = new HashMap<String, Object>();
			param.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(param);
			mv.addObject("dicList", dicList);
		}
		
		mv.addObject("map", map);// payWayId
		return mv;
	}
	
	/**
	 * 支付通道路由列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/payv2PayWayRateListAll")
	public ModelAndView payv2PayWayRateListAll(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/paywayrate/payv2paywayrate-all-list");
		map.put("isDelete", 2);// 未删除
		map.put("sortName", "update_time");
		map.put("sortOrder", "DESC");
		PageObject<Payv2PayWayRate> pageObject = payv2PayWayRateService.Pagequery(map);
		mv.addObject("list", pageObject);
		

		Map<String, Object> wayMap = new HashMap<String, Object>();
		wayMap.put("status", 1);
		wayMap.put("isDelete", 2);
		List<Payv2PayWay> wayList = payv2PayWayService.query(wayMap);
		

		wayMap = new HashMap<String, Object>();
		List<Payv2PayType> typeList = payv2PayTypeService.query(wayMap);
		
		Payv2BussCompany company = new Payv2BussCompany();
		company.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);

		mv.addObject("wayList", wayList);
		mv.addObject("typeList", typeList);
		mv.addObject("companyList", companyList);

		Map<String, Object> param = new HashMap<String, Object>();
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		param.put("dictPvalue", -1);
		param.put("dictName", "PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(param);
		if (sysConfigDictionary != null) {
			param = new HashMap<String, Object>();
			param.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(param);
			mv.addObject("dicList", dicList);
		}
		
		mv.addObject("map", map);// payWayId
		return mv;
	}

	/**
	 * @Title: addPayv2PayWayRateTc
	 * @Description:添加支付通道路由弹窗
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月6日 上午11:21:22
	 * @throws
	 */
	@RequestMapping("addPayv2PayWayRateTc")
	public ModelAndView addPayv2PayWayRateTc(
			@RequestParam Map<String, Object> map) {
		ModelAndView andView = new ModelAndView(
				"payv2/paywayrate/payv2paywayrate-add_new");
		Map<String, Object> param = new HashMap<String, Object>();
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		param.put("dictPvalue", -1);
		param.put("dictName", "PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(param);
		if (sysConfigDictionary != null) {
			param = new HashMap<String, Object>();
			param.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(param);
			/*
			 * List<SysConfigDictionary> dicList2 = new
			 * ArrayList<SysConfigDictionary>(); Set<Integer> dictIdSet = new
			 * HashSet<Integer>(); param.remove("dictPvalue");
			 * param.put("wayType", 2); //查询已经使用过的第三方支付 List<Payv2PayWayRate>
			 * payv2PayWayRateList = payv2PayWayRateService.query(param);
			 * for(Payv2PayWayRate payv2PayWayRate : payv2PayWayRateList){
			 * if(payv2PayWayRate.getDicId() != null &&
			 * !dictIdSet.contains(Integer
			 * .valueOf(payv2PayWayRate.getDicId().toString()))){
			 * dictIdSet.add(Integer
			 * .valueOf(payv2PayWayRate.getDicId().toString())); } }
			 * //过滤掉使用过的第三方支付 for(SysConfigDictionary dictionary : dicList){
			 * if(!dictIdSet.contains(dictionary.getId())){
			 * dicList2.add(dictionary); } }
			 */
			andView.addObject("dicList", dicList);
			andView.addObject("map", map);
			
			Map<String, Object> wayMap = new HashMap<String, Object>();
			wayMap.put("status", 1);
			wayMap.put("isDelete", 2);
			wayMap.put("id", map.get("payWayId"));
			Payv2PayWay way = payv2PayWayService.detail(wayMap);
			andView.addObject("way", way);
			
			wayMap = new HashMap<String, Object>();
			wayMap.put("payWayId", map.get("payWayId"));
			List<Payv2PayType> typeList = payv2PayTypeService.query(wayMap);
			andView.addObject("typeList", typeList);
		}

		
		return andView;
	}

	/**
	 * @Title: addPayv2PayWay
	 * @Description:添加支付渠道提交
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("addPayv2PayWayRate")
	public Map<String, Object> addPayv2PayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			map.put("createTime", new Date());
			map.put("updateTime", new Date());
			map.put("createUserBy", getAdmin().getId());
			map.put("updateUserBy", getAdmin().getId());
			map.put("status", 2);// 默认不启动

			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("dicId", map.get("dicId"));
			paramMap.put("companyName", map.get("companyName"));
			paramMap.put("isDelete", 2);
			List<Payv2PayWayRate> list = payv2PayWayRateService.query(paramMap);
			if (list == null || list.isEmpty()) {
				Payv2PayWayRate rate = payv2PayWayRateService.add(map);
				Payv2PayWayRate uRate = new Payv2PayWayRate();
				String payPlat = "";
				if (map.get("payViewType").equals("1"))
					payPlat = "支付宝支付";
				else if (map.get("payViewType").equals("2"))
					payPlat = "微信支付";
				else if (map.get("payViewType").equals("3"))
					payPlat = "QQ支付";
				else
					payPlat = "其他";
				uRate.setId(rate.getId());
				uRate.setPayWayName(payPlat + rate.getId());
				payv2PayWayRateService.update(uRate);
				sysLogService.addSysLog(1, LogTypeEunm.T6, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
				returnMap.put("resultCode", 200);
			} else {
				returnMap.put("resultCode", 201);
			}
		} catch (Exception e) {
			logger.error("添加支付渠道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	@SuppressWarnings({ "static-access", "rawtypes" })
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> importExcel(HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", 201);
		try {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			MultipartFile file = multipartRequest.getFile("file");
			if(file.isEmpty()){
				throw new Exception("文件为空！");
	        }
			//文件名字
			String fileName=file.getOriginalFilename();
			//输入流
			InputStream in=file.getInputStream();
			List<Map> dataListMap = reu.ReaderExcel(in, fileName);
			dataListMap.remove(dataListMap.size()-1);
			//失败录入通道
			List<String> failList = new ArrayList<String>();
			
			List<Map> succList = new ArrayList<Map>();
			//所有字典
			List<SysConfigDictionary> dictionarys = dictionaryService.selectAll();
			Integer nextId = payv2PayWayRateService.getNextId();
			for (Map map : dataListMap) {
				nextId++;
				Payv2PayWayRate payv2PayWayRate = new Payv2PayWayRate();
				try {
					payv2PayWayRate.setId(Long.valueOf(nextId));
					payv2PayWayRate.setCreateTime(new Date());
					payv2PayWayRate.setUpdateTime(new Date());
					payv2PayWayRate.setCreateUserBy(getAdmin().getId());
					payv2PayWayRate.setUpdateUserBy(getAdmin().getId());
					payv2PayWayRate.setStatus(2);// 默认不启动
					payv2PayWayRate.setPayWayId(Long.valueOf(columnTran(map.get("上游通道").toString().trim(), 1)));
					payv2PayWayRate.setPayViewType(Integer.valueOf(columnTran(map.get("上游通道").toString().trim(), 2)));
					
					if(1==payv2PayWayRate.getPayViewType()){
						payv2PayWayRate.setPayWayName("支付宝支付" + nextId);
					}else if (2==payv2PayWayRate.getPayViewType()) {
						payv2PayWayRate.setPayWayName("微信支付" + nextId);
					}else if (3==payv2PayWayRate.getPayViewType()) {
						payv2PayWayRate.setPayWayName("QQ支付" + nextId);
					}else {
						payv2PayWayRate.setPayWayName("其他" + nextId);
					}
					
					payv2PayWayRate.setCompanyName(map.get("主体公司").toString().trim());
					payv2PayWayRate.setCompanyAlias(map.get("主体公司简称").toString().trim());
					//判断是否重复
					Map<String, Object> paramMap = new HashMap<String, Object>();
					
					paramMap.put("companyName", payv2PayWayRate.getCompanyName());
					paramMap.put("isDelete", 2);
					for (SysConfigDictionary dictionary : dictionarys) {
						if(dictionary.getDictValue().equals(map.get("上游通道").toString().trim())){
							paramMap.put("dicId", dictionary.getId());
							payv2PayWayRate.setRateName(dictionary.getDictValue()+"(" + payv2PayWayRate.getCompanyAlias() + ")");
							//设置支付类型和字典关联
							payv2PayWayRate.setDicId(Long.valueOf(dictionary.getId()));
							if(dictionary.getDictName().contains("_APP")){
								if(dictionary.getDictName().contains("_ALI")){
									payv2PayWayRate.setPayType(1);
								}else if(dictionary.getDictName().contains("_WEIXIN")){
									payv2PayWayRate.setPayType(4);
								}
							}else if(dictionary.getDictName().contains("_SCAN")){
								if(dictionary.getDictName().contains("_ALI")){
									payv2PayWayRate.setPayType(2);
								}else if(dictionary.getDictName().contains("_WEIXIN")){
									payv2PayWayRate.setPayType(5);
								}else if(dictionary.getDictName().contains("_QQ")){
									payv2PayWayRate.setPayType(8);
								}
							}else if(dictionary.getDictName().contains("_WAP")){
								if(dictionary.getDictName().contains("_ALI")){
									payv2PayWayRate.setPayType(3);
								}else if(dictionary.getDictName().contains("_WEIXIN")){
									payv2PayWayRate.setPayType(7);
								}
							}else if(dictionary.getDictName().contains("_GZH")){
								if(dictionary.getDictName().contains("_WEIXIN")){
									payv2PayWayRate.setPayType(6);
								}
							}
						}
					}
					
					if(null==payv2PayWayRate.getPayType()){
						throw new Exception(map.get("序号")+"没有字典！");
					}
					
					List<Payv2PayWayRate> list = payv2PayWayRateService.query(paramMap);
					if(list.size()>0){
						throw new Exception(map.get("序号")+"重复！");
					}
					
					
					//银行账户信息
					payv2PayWayRate.setAccountBank(map.get("开户银行").toString().trim());
					payv2PayWayRate.setAccountType(Integer.valueOf(columnTran(map.get("账户类型").toString().trim(), 1)));
					payv2PayWayRate.setAccountName(map.get("开户名称").toString().trim());
					payv2PayWayRate.setAccountCard(map.get("开户账号").toString().trim());
					//费率信息
					payv2PayWayRate.setPayWayRate(Double.valueOf(map.get("通道费率成本").toString().trim()));
					payv2PayWayRate.setOneMaxMoney(Double.valueOf(map.get("单笔限额").toString().trim()));
					
					payv2PayWayRate.setBankRate(Double.valueOf(map.get("银行结算费率").toString().trim()));
					payv2PayWayRate.setBankRateType(Integer.valueOf(columnTran(map.get("清算方式").toString().trim(), 1)));
					
					payv2PayWayRate.setMaxMoney(Double.valueOf(map.get("每日限额").toString().trim()));
					payv2PayWayRate.setWayArrivalType(Integer.valueOf(columnTran(map.get("结算类型").toString().trim(), 1)));
					if(payv2PayWayRate.getWayArrivalType()==2){
						payv2PayWayRate.setWayArrivalValue(0);
					}else {
						payv2PayWayRate.setWayArrivalValue(Integer.valueOf(map.get("到账日期").toString().trim()));
					}
					
					
					
					
					//通道字段
					payv2PayWayRate.setAutoRecord(Integer.valueOf(columnTran(map.get("是否自动对账").toString().trim(), 1)));
					if(null!=map.get("字段1") && !"".equals(map.get("字段1")+"")){
						payv2PayWayRate.setRateKey1(map.get("字段1").toString().trim());
					}
					if(null!=map.get("字段2") && !"".equals(map.get("字段2")+"")){
						payv2PayWayRate.setRateKey2(map.get("字段2")+"".trim());
					}
					if("".equals(payv2PayWayRate.getCompanyName())
							||"".equals(payv2PayWayRate.getCompanyAlias())
							||"".equals(payv2PayWayRate.getAccountBank())
							||"".equals(payv2PayWayRate.getAccountName())
							||"".equals(payv2PayWayRate.getAccountCard())
							||"".equals(payv2PayWayRate.getRateKey1())
							||"".equals(payv2PayWayRate.getRateKey2())){
						throw new Exception("空！");
					}
					
					if(null!=map.get("字段3") && !"".equals(map.get("字段3")+"")){
						payv2PayWayRate.setRateKey3(map.get("字段3")+"".trim());
					}
					if(null!=map.get("字段4") && !"".equals(map.get("字段4")+"")){
						payv2PayWayRate.setRateKey4(map.get("字段4")+"".trim());
					}
					if(null!=map.get("字段5") && !"".equals(map.get("字段5")+"")){
						payv2PayWayRate.setRateKey5(map.get("字段5")+"".trim());
					}
					if(null!=map.get("字段6") && !"".equals(map.get("字段6")+"")){
						payv2PayWayRate.setRateKey6(map.get("字段6")+"".trim());
					}
					if(null!=map.get("公众号app_id") && !"".equals(map.get("公众号app_id")+"")){
						payv2PayWayRate.setGzhAppId(map.get("公众号app_id")+"".trim());
					}
					if(null!=map.get("公众号key") && !"".equals(map.get("公众号key")+"")){
						payv2PayWayRate.setGzhKey(map.get("公众号key")+"".trim());
					}
					if(null!=map.get("公众号预留字段") && !"".equals(map.get("公众号预留字段")+"")){
						payv2PayWayRate.setGzhStr(map.get("公众号预留字段")+"".trim());
					}
					if(null!=map.get("字段备注") && !"".equals(map.get("字段备注")+"")){
						payv2PayWayRate.setKeyRemark(map.get("字段备注")+"".trim());
					}
					
					payv2PayWayRateService.add(payv2PayWayRate);
					succList.add(map);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
					nextId--;
					failList.add(map.get("序号")+"".trim());
					continue;
				}
			}
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("data", succList);
			sysLogService.addSysLog(1, LogTypeEunm.T55, IpAddressUtil.getIpAddress(request), getAdmin().getId(), data);
			result.put("code", 200);
			result.put("failList", failList);
			result.put("msg", failList.toString());
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.getMessage());
			logger.error("excel渠道导入："+e);
		}
		return result;
	}
	
	private String columnTran(String column, Integer type){
		if(type==1){
			if(column.toUpperCase().contains("支付宝APP") || "对公户".equals(column) || "企业".equals(column) || "T+".equals(column) || "是".equals(column) || "直清".equals(column)){
				return "1";
			}else if (column.toUpperCase().contains("支付宝扫码") || "对私户".equals(column) || "私人".equals(column) || "实时到账".equals(column) || "实时到帐".equals(column) || "否".equals(column) || "二清".equals(column)) {
				return "2";
			}else if (column.toUpperCase().contains("支付宝WAP") || "D+".equalsIgnoreCase(column)) {
				return "3";
			}else if (column.toUpperCase().contains("微信APP")) {
				return "4";
			}else if (column.toUpperCase().contains("微信扫码")) {
				return "5";
			}else if (column.toUpperCase().contains("微信公众号")) {
				return "6";
			}else if (column.toUpperCase().contains("微信WAP")) {
				return "7";
			}else if (column.toUpperCase().contains("QQ扫码")) {
				return "8";
			}
		}else {
			if(column.contains("支付宝")){
				return "1";
			}else if (column.contains("微信")) {
				return "2";
			}else if (column.contains("QQ")) {
				return "3";
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println("兴业深圳支付宝app".toUpperCase().contains("支付宝APP"));
	}
	
	/**
	 * @Title: editPayv2PayWayRateTc
	 * @Description:修改支付通道路由弹窗
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月6日 上午11:21:22
	 * @throws
	 */
	@RequestMapping("editPayv2PayWayRateTc")
	public ModelAndView editPayv2PayWayRateTc(
			@RequestParam Map<String, Object> map) {
		ModelAndView andView = new ModelAndView(
				"payv2/paywayrate/payv2paywayrate-edit_new");
		Payv2PayWayRate payv2PayWay = payv2PayWayRateService.detail(map);
		String createUserName = "";
		String updateUserName = "";
		Map<String, Object> adminMap = new HashMap<String, Object>();
		if(payv2PayWay.getCreateUserBy() != null) {
			adminMap.put("id", payv2PayWay.getCreateUserBy());
			SysUcenterAdmin admin = sysUcenterAdminService.detail(adminMap);
			if(admin != null) {
				createUserName = admin.getAdmDisplayName();
			}
		}
		if(payv2PayWay.getUpdateUserBy() != null&&payv2PayWay.getCreateUserBy()!=null) {
			if(payv2PayWay.getUpdateUserBy().longValue() == payv2PayWay.getCreateUserBy().longValue()) {
				updateUserName = createUserName;
			} else {
				adminMap.put("id", payv2PayWay.getUpdateUserBy());
				SysUcenterAdmin admin = sysUcenterAdminService.detail(adminMap);
				if(admin != null) {
					updateUserName = admin.getAdmDisplayName();
				}
			}
		}

		andView.addObject("curPage", map.get("curPage"));
		andView.addObject("createUserName", createUserName);
		andView.addObject("updateUserName", updateUserName);
		andView.addObject("payv2PayWayRate", payv2PayWay);
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		map.remove("id");
		map.put("dictPvalue", -1);
		map.put("dictName", "PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(map);
		if (sysConfigDictionary != null) {
			map = new HashMap<String, Object>();
			map.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(map);
			/*
			 * List<SysConfigDictionary> dicList2 = new
			 * ArrayList<SysConfigDictionary>(); Set<Integer> dictIdSet = new
			 * HashSet<Integer>(); map.remove("dictPvalue"); map.put("wayType",
			 * 2); //查询已经使用过的第三方支付 List<Payv2PayWayRate> payv2PayWayList =
			 * payv2PayWayRateService.query(map); for(Payv2PayWayRate payway :
			 * payv2PayWayList){ if(payway.getDicId() != null &&
			 * !dictIdSet.contains
			 * (Integer.valueOf(payway.getDicId().toString()))){ //当前的支付方式不过滤
			 * if(payway.getDicId() != payv2PayWay.getDicId()){
			 * dictIdSet.add(Integer.valueOf(payway.getDicId().toString())); } }
			 * } //过滤掉使用过的第三方支付 for(SysConfigDictionary dictionary : dicList){
			 * if(!dictIdSet.contains(dictionary.getId())){
			 * dicList2.add(dictionary); } }
			 */
			andView.addObject("dicList", dicList);

			Map<String, Object> wayMap = new HashMap<String, Object>();
			wayMap.put("payWayId", map.get("payWayId"));
			List<Payv2PayType> typeList = payv2PayTypeService.query(wayMap);
			andView.addObject("typeList", typeList);
		}
		return andView;
	}

	/**
	 * @Title: clonePayv2PayWayRateTc
	 * @Description:克隆支付通道路由弹窗
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月6日 上午11:21:22
	 * @throws
	 */
	@RequestMapping("clonePayv2PayWayRateTc")
	public ModelAndView clonePayv2PayWayRateTc(
			@RequestParam Map<String, Object> map) {
		ModelAndView andView = new ModelAndView(
				"payv2/paywayrate/payv2paywayrate-clone");
		Payv2PayWayRate payv2PayWay = payv2PayWayRateService.detail(map);
		andView.addObject("payv2PayWayRate", payv2PayWay);
		andView.addObject("curPage", map.get("curPage"));
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		map.remove("id");
		map.put("dictPvalue", -1);
		map.put("dictName", "PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(map);
		if (sysConfigDictionary != null) {
			map = new HashMap<String, Object>();
			map.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(map);
			/*
			 * List<SysConfigDictionary> dicList2 = new
			 * ArrayList<SysConfigDictionary>(); Set<Integer> dictIdSet = new
			 * HashSet<Integer>(); map.remove("dictPvalue"); map.put("wayType",
			 * 2); //查询已经使用过的第三方支付 List<Payv2PayWayRate> payv2PayWayList =
			 * payv2PayWayRateService.query(map); for(Payv2PayWayRate payway :
			 * payv2PayWayList){ if(payway.getDicId() != null &&
			 * !dictIdSet.contains
			 * (Integer.valueOf(payway.getDicId().toString()))){ //当前的支付方式不过滤
			 * if(payway.getDicId() != payv2PayWay.getDicId()){
			 * dictIdSet.add(Integer.valueOf(payway.getDicId().toString())); } }
			 * } //过滤掉使用过的第三方支付 for(SysConfigDictionary dictionary : dicList){
			 * if(!dictIdSet.contains(dictionary.getId())){
			 * dicList2.add(dictionary); } }
			 */
			andView.addObject("dicList", dicList);

			Map<String, Object> wayMap = new HashMap<String, Object>();
			wayMap.put("payWayId", map.get("payWayId"));
			List<Payv2PayType> typeList = payv2PayTypeService.query(wayMap);
			andView.addObject("typeList", typeList);
		}
		return andView;
	}

	/**
	 * @Title: updatePayv2PayWayRate
	 * @Description: 修改支付渠道 
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月8日 上午9:45:03
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("updatePayv2PayWayRate")
	public Map<String, Object> updatePayv2PayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if (map.containsKey("id") ) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("dicId", map.get("dicId"));
				paramMap.put("companyName", map.get("companyName"));
				//paramMap.put("isDelete", 2);
				List<Payv2PayWayRate> list = payv2PayWayRateService
						.query(paramMap);
				// 判断该上游下面是否存在该公司主体
				boolean isExists = false;
				for (Payv2PayWayRate rate : list) {
					if(rate.getId().longValue() != Long.parseLong(map.get("id").toString())) {
						isExists = true;
					}
				}
				if (!isExists) {
//					System.out.println(map);
					map.put("updateTime", new Date());
					map.put("updateUserBy", getAdmin().getId());
					payv2PayWayRateService.updatePayWayRate(map);

					sysLogService.addSysLog(1, LogTypeEunm.T8, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					returnMap.put("resultCode", 200);
				} else {
					returnMap.put("resultCode", 201);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * @Title: batchUpdatePayv2PayWayRate
	 * @Description: 批量 开启/关闭/删除
	 * @param map 49,50,51,52  1/2/3
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月8日 上午9:45:03
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("batchUpdatePayv2PayWayRate")
	public Map<String, Object> batchUpdatePayv2PayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if (map.containsKey("gids") && map.containsKey("type")) {
				String[] gid = map.get("gids").toString().split(",");
				if(gid.length > 0) {
					String type = map.get("type").toString();
					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("ids", map.get("gids").toString());
					paramMap.put("updateTime", new Date());
					paramMap.put("updateUserBy", getAdmin().getId());
					switch (type) {
					case "1":
						// 关闭 ${optionName}=#{optionValue} where id in ${ids}
						paramMap.put("optionName", "status");
						paramMap.put("optionValue", "2");
						for (String string : gid) {
							RedisDBUtil.redisDao.setString("rate_disabled_"
									+ string, "1");
							RedisDBUtil.redisDao.expire(
									"rate_disabled_" + string,
									600);
						}
						payv2BussSupportPayWayService.batchDelete(map.get("gids").toString());
						sysLogService.addSysLog(1, LogTypeEunm.T10, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					case "2":
						// 删除
						paramMap.put("optionName", "is_delete");
						paramMap.put("optionValue", "1");
						for (String string : gid) {
							RedisDBUtil.redisDao.setString("rate_disabled_"
									+ string, "1");
							RedisDBUtil.redisDao.expire(
									"rate_disabled_" + string,
									600);
						}
						sysLogService.addSysLog(1, LogTypeEunm.T7, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					case "3":
						// 开启
						paramMap.put("optionName", "status");
						paramMap.put("optionValue", "1");
						for (String string : gid) {
							RedisDBUtil.redisDao.delete("rate_disabled_"
									+ string);
						}
						sysLogService.addSysLog(1, LogTypeEunm.T9, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					}
					payv2PayWayRateService.batchUpdate(paramMap);
					returnMap.put("resultCode", 200);
				} else {
					returnMap.put("resultCode", 201);
				}
			} else {
				returnMap.put("resultCode", 201);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	@ResponseBody
	@RequestMapping("/exportExcelRate")
	public Map<String, Object> exportExcelOrder(@RequestParam Map<String, Object> map, HSSFWorkbook workbook, HttpServletResponse response) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<Payv2PayWayRateVO> export = payv2PayWayRateService.getExport(map);
		
		try {
			if (null != export && export.size() > 0) {
				// 导出
				CSVUtils<Object> csv = new CSVUtils<Object>();
				Object[] headers = { "通道名称(下游)", "通道名称(官方)", "支付方式", "上游通道", "主体公司", "录入人", "备注", "开户银行", "账号类型",
										"开户名称", "开户账号", "成本费率‰", "单笔限额", "每日限额", "到期类型", "到账日期", "状态"};
				List<Object> dataset = new ArrayList<Object>();
				for (Payv2PayWayRateVO vo : export) {
					dataset.add(vo);
				}
				OutputStream out = response.getOutputStream();
				String filename = "渠道列表" + new Date().getTime() + ".csv";
				filename = URLEncoder.encode(filename, "UTF-8");// 处理中文文件名
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", "attachment;filename=" + filename);
				List<Object> headList = Arrays.asList(headers);
				File csvFile = csv.commonCSV(headList, dataset, null, filename);
				InputStream in = new FileInputStream(csvFile);
				int b;  
	            while((b=in.read())!= -1)  
	            {  
	                out.write(b);  
	            }  
	            in.close();
				out.close();
			} else {
				returnMap.put("status", -1);// 失败
			}
		} catch (Exception e) {
			logger.error("导出通道列表错误", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	/**
	 * addWXgzhConfig 
	 * 添加平安微信公众号支付配置
	 * @param map
	 * 			id:支付通道ID
	 * 			ctt_code:合同号：此为平安银行下面的微信公众号配置合同号
	 * 			type:类型
	 * @return    设定文件 
	 * Map<String,Object>    返回类型
	 */
	@ResponseBody
	@RequestMapping("addWXgzhConfig")
	public Map<String, Object> addWXgzhConfig(@RequestParam Map<String, Object> map) {
		Map<String, Object> returnMap=new HashMap<String, Object>();
		Long id = Long.valueOf(map.get("id").toString());
		int type = Integer.valueOf(map.get("type").toString());
		Payv2PayWayRate payv2PayWayRate = payv2PayWayRateService.queryByid(id);
		if (payv2PayWayRate != null) {
			String ctt_code = map.get("ctt_code").toString();
			String sub_appid = null;
			String jsapi_path = null;
			if (type == 1) {
				sub_appid = payv2PayWayRate.getGzhAppId();
			}
			if (type == 2) {
				//授权目录
				jsapi_path = payv2PayWayRate.getGzhStr();
			}
			String OPEN_ID = payv2PayWayRate.getRateKey3();
			String OPEN_KEY = payv2PayWayRate.getRateKey4();
			String PRIVATE_KEY = payv2PayWayRate.getRateKey5();
			String PUBLICKEY = payv2PayWayRate.getRateKey6();
			returnMap=AddConfig.addConfig(ctt_code, sub_appid, jsapi_path, OPEN_ID, OPEN_KEY, PRIVATE_KEY, PUBLICKEY);
		}
		return returnMap;
	}
	
	/**
	 * @Title: batchUpdatePayv2PayWayRate
	 * @Description: 批量 开启/关闭/删除
	 * @param map 49,50,51,52  1/2/3
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月8日 上午9:45:03
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("batchGiveToCompany")
	public Map<String, Object> batchGiveToCompany(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if (map.containsKey("gids") && map.containsKey("companyId")) {
				String[] gid = map.get("gids").toString().split(",");
				if(gid.length > 0) {
					String count = payv2BussSupportPayWayService.batchGiveRateToCompany(map);
					int all = map.get("gids").toString().split(",").length;
					String cz = count.split(",")[0];
					String bzc = count.split(",")[1];
					int cg = all - (Integer.parseInt(cz)+Integer.parseInt(bzc));
					map.put("existCount", cz);
					map.put("notSupportCount", bzc);

					sysLogService.addSysLog(1, LogTypeEunm.T54, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null, "共分配"+all+"条通道，成功"+cg+"条"+(cz.equals("0")?"":"<br/>有"+cz+"条已存在所以失败")+(bzc.equals("0")?"":"<br/>有"+bzc+"条商户不支持该支付方式"));
				} else {
					returnMap.put("resultCode", 201);
				}
			} else {
				returnMap.put("resultCode", 201);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnMap;
	}
}
