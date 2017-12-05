package com.pay.manger.controller.payv2;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.admin.entity.SysUcenterAdmin;
import com.pay.business.admin.service.SysUcenterAdminService;
import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.pay.business.df.service.DfPayOrderService;
import com.pay.business.df.service.DfPayWayRateService;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.mapper.Payv2PayWayRateMapper;
import com.pay.business.sysconfig.entity.SysConfigDictionary;
import com.pay.business.sysconfig.service.SysConfigDictionaryService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * 
 * @ClassName: DfWayRateController
 * @Description: 代付通道
 * @author Terry
 * @date 2017年8月30日 10:43:12
 */
@Controller
@RequestMapping("/dfWayRate")
public class DfWayRateController extends
		BaseManagerController<Payv2PayWayRate, Payv2PayWayRateMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	@Autowired
	private DfPayWayRateService dfPayWayRateService;
	@Autowired
	private SysConfigDictionaryService sysConfigDictionaryService;
	@Autowired
	private SysUcenterAdminService sysUcenterAdminService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private DfPayCompanyRateService dfPayCompanyRateService;
    @Autowired
    private DfPayOrderService dfPayOrderService;
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2ChannelService payv2ChannelService;
	
	

	/**
	 * 代付通道路由列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/rateList")
	public ModelAndView payv2PayWayRateList(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/df/df-wayrate-list");
		map.put("isDelete", 2);// 未删除
		map.put("sortName", "update_time");
		map.put("sortOrder", "DESC");
		PageObject<DfPayWayRate> pageObject = dfPayWayRateService.Pagequery(map);
		mv.addObject("list", pageObject);
		mv.addObject("map", map);// payWayId
		return mv;
	}
	
	/**
	 * 代付订单列表
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/dfOrderList")
	public ModelAndView dfOrderList(@RequestParam Map<String, Object> map) {
		ModelAndView av = new ModelAndView("payv2/df/df-order-list");
		PageObject<DfPayOrder> pageList = dfPayOrderService.getPageObject(map);
		//List<Payv2PayOrder> orderList = pageList.getDataList();
		// 获取渠道商
		Payv2Channel channel = new Payv2Channel();
//		channel.setIsDelete(2);
		List<Payv2Channel> channelList = payv2ChannelService.selectByObject(channel);
		// 获取商户
		Payv2BussCompany company = new Payv2BussCompany();
//		company.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);
		// 获取代付通道
		DfPayWayRate rate = new DfPayWayRate();
//		rate.setIsDelete(2);
//		rate.setStatus(1);
		List<DfPayWayRate> rateList = dfPayWayRateService.selectByObject(rate);
		
		av.addObject("rateList", rateList);
		av.addObject("companyList", companyList);
		av.addObject("channelList", channelList);
		av.addObject("map", map);
		av.addObject("list", pageList);
		return av;
	}
	/**
	 * 代付订单详情
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/getOrderDetail")
	public ModelAndView getOrderDetail(@RequestParam Map<String, Object> map) {
		ModelAndView av = new ModelAndView("payv2/df/df-order-detail");
		DfPayOrder order = dfPayOrderService.selectOrderDetail(map);
		av.addObject("order", order);
		av.addObject("map", map);
		return av;
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
	@RequestMapping("addDfPayWayRateTc")
	public ModelAndView addPayv2PayWayRateTc(
			@RequestParam Map<String, Object> map) {
		ModelAndView andView = new ModelAndView(
				"payv2/df/df-wayrate-add");
		Map<String, Object> param = new HashMap<String, Object>();
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		param.put("dictPvalue", -1);
		param.put("dictName", "DF_PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(param);
		if (sysConfigDictionary != null) {
			param = new HashMap<String, Object>();
			param.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(param);
			andView.addObject("dicList", dicList);
			andView.addObject("map", map);
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
	@RequestMapping("addDfPayWayRate")
	public Map<String, Object> addPayv2PayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("dicId", map.get("dicId"));
			paramMap.put("companyName", map.get("companyName"));
			paramMap.put("isDelete", 2);
			List<DfPayWayRate> list = dfPayWayRateService.query(paramMap);
			if (list == null || list.isEmpty()) {
				map.put("createTime", new Date());
				map.put("updateTime", new Date());
				map.put("createUserBy", getAdmin().getId());
				map.put("updateUserBy", getAdmin().getId());
				map.put("status", 2);// 默认不启动
				DfPayWayRate rate = dfPayWayRateService.add(map);
				DfPayWayRate uRate = new DfPayWayRate();
				String payPlat = "代付通道";
				uRate.setId(rate.getId());
				uRate.setDfPayWayName(payPlat + rate.getId());
				dfPayWayRateService.update(uRate);
				sysLogService.addSysLog(1, LogTypeEunm.T35, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
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
	


	/**
	 * @Title: editPayv2PayWayRateTc
	 * @Description:修改支付通道路由弹窗
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月6日 上午11:21:22
	 * @throws
	 */
	@RequestMapping("editDfWayRateTc")
	public ModelAndView editPayv2PayWayRateTc(
			@RequestParam Map<String, Object> map) {
		ModelAndView andView = new ModelAndView(
				"payv2/df/df-wayrate-edit");
		DfPayWayRate payv2PayWay = dfPayWayRateService.detail(map);
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
		andView.addObject("dfWayRate", payv2PayWay);
		SysConfigDictionary sysConfigDictionary = new SysConfigDictionary();
		map.remove("id");
		map.put("dictPvalue", -1);
		map.put("dictName", "DF_PAY_TYPE");
		sysConfigDictionary = sysConfigDictionaryService.detail(map);
		if (sysConfigDictionary != null) {
			map = new HashMap<String, Object>();
			map.put("dictPvalue", sysConfigDictionary.getId());
			List<SysConfigDictionary> dicList = sysConfigDictionaryService
					.query(map);
			andView.addObject("dicList", dicList);
		}
		return andView;
	}
	
	/**
	 * @Title: updateDfWayRate
	 * @Description: 修改代付通道
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月8日 上午9:45:03
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("updateDfWayRate")
	public Map<String, Object> updatePayv2PayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			if (map.containsKey("id") ) {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("dicId", map.get("dicId"));
				paramMap.put("companyName", map.get("companyName"));
				//paramMap.put("isDelete", 2);
				List<DfPayWayRate> list = dfPayWayRateService
						.query(paramMap);
				// 判断该上游下面是否存在该公司主体
				boolean isExists = false;
				for (DfPayWayRate rate : list) {
					if(rate.getId().longValue() != Long.parseLong(map.get("id").toString())) {
						isExists = true;
					}
				}
				if (!isExists) {
					map.put("updateTime", new Date());
					map.put("updateUserBy", getAdmin().getId());
					dfPayWayRateService.updateDfWayRate(map);
					sysLogService.addSysLog(1, LogTypeEunm.T36, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
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
	@RequestMapping("batchUpdateDfWayRate")
	public Map<String, Object> batchUpdateDfWayRate(
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
						sysLogService.addSysLog(1, LogTypeEunm.T38, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					case "2":
						// 删除
						paramMap.put("optionName", "is_delete");
						paramMap.put("optionValue", "1");
						sysLogService.addSysLog(1, LogTypeEunm.T39, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					case "3":
						// 开启
						paramMap.put("optionName", "status");
						paramMap.put("optionValue", "1");
						sysLogService.addSysLog(1, LogTypeEunm.T37, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
						break;
					}
					dfPayWayRateService.batchUpdate(paramMap);
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
	
	/**
	 * 为商户配置代付通道页面弹窗
	 * 
	 * @param map
	 * @param request
	 * @return
	 */
	@RequestMapping("/configDfWayRateTc")
	public ModelAndView configDfWayRateTc(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/df/df_company_config_way_rate");
		if(map.containsKey("companyId")) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("is_delete", 2);
			tempMap.put("status", 1);
			List<DfPayWayRate> rateList = dfPayWayRateService.query(tempMap);
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(map);
			mv.addObject("configObj", rate);
			mv.addObject("rateList", rateList);
			mv.addObject("map", map);
		}
		return mv;
	}
	
	/**
	 * @Title: addCompanyDfPayWayRate
	 * @Description:为商户添加代付通道
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("addCompanyDfPayWayRate")
	public Map<String, Object> addCompanyDfPayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", map.get("companyId"));
			paramMap.put("dfRateId", map.get("dfRateId"));
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(paramMap);
			if(rate != null) {
				returnMap.put("resultCode", 201);
				return returnMap;
			}
			
			map.put("createTime", new Date());
			map.put("updateTime", new Date());
			map.put("createUserBy", getAdmin().getId());
			map.put("updateUserBy", getAdmin().getId());
			dfPayCompanyRateService.add(map);
			sysLogService.addSysLog(1, LogTypeEunm.T40, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			returnMap.put("resultCode", 200);
		} catch (Exception e) {
			logger.error("为商户添加代付通道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * @Title: updateCompanyDfPayWayRate
	 * @Description:为商户修改代付通道
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("updateCompanyDfPayWayRate")
	public Map<String, Object> updateCompanyDfPayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", map.get("companyId"));
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(paramMap);
			if(rate == null) {
				returnMap.put("resultCode", 202);
				return returnMap;
			}
			map.put("id", rate.getId());
			map.put("updateTime", new Date());
			map.put("updateUserBy", getAdmin().getId());
			dfPayCompanyRateService.update(map);
			sysLogService.addSysLog(1, LogTypeEunm.T41, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			returnMap.put("resultCode", 200);
		} catch (Exception e) {
			logger.error("为商户添加代付通道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * @Title: updateCompanyDfPayWayRateStatus
	 * @Description:修改商户代付通道状态
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("updateCompanyDfPayWayRateStatus")
	public Map<String, Object> updateCompanyDfPayWayRateStatus(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", map.get("companyId"));
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(paramMap);
			if(rate == null) {
				returnMap.put("resultCode", 202);
				return returnMap;
			}
			map.put("id", rate.getId());
			map.put("updateTime", new Date());
			map.put("updateUserBy", getAdmin().getId());
			dfPayCompanyRateService.update(map);
			if(map.get("status").toString().equals("1")) {
				sysLogService.addSysLog(1, LogTypeEunm.T44, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			} else {
				sysLogService.addSysLog(1, LogTypeEunm.T45, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			}
			returnMap.put("resultCode", 200);
		} catch (Exception e) {
			logger.error("为商户添加代付通道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * @Title: deleteCompanyDfPayWayRate
	 * @Description:删除商户代付通道
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("deleteCompanyDfPayWayRate")
	public Map<String, Object> deleteCompanyDfPayWayRate(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", map.get("companyId"));
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(paramMap);
			if(rate == null) {
				returnMap.put("resultCode", 200);
			} else {
				if(rate.getTotalAmount().doubleValue() > 0) {
					returnMap.put("resultCode", 203);
				} else {
					paramMap.clear();
					paramMap.put("companyId", map.get("companyId"));
					dfPayCompanyRateService.delete(paramMap);
					sysLogService.addSysLog(1, LogTypeEunm.T42, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
					returnMap.put("resultCode", 200);
				}
			}
		} catch (Exception e) {
			logger.error("为商户添加代付通道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * @Title: companyDfPayWayRatePayment
	 * @Description:为商户资金池充值
	 * @param map
	 * @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @date 2016年12月7日 下午7:57:11
	 * @throws
	 */
	@ResponseBody
	@RequestMapping("companyDfPayWayRatePayment")
	public Map<String, Object> companyDfPayWayRatePayment(
			@RequestParam Map<String, Object> map,HttpServletRequest request) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("companyId", map.get("companyId"));
			DfPayCompanyRate rate = dfPayCompanyRateService.detail(paramMap);
			if(rate == null) {
				returnMap.put("resultCode", 201);
				return returnMap;
			}
			map.put("dfRateId", rate.getDfRateId());
			map.put("adminId", getAdmin().getId());
			dfPayCompanyRateService.plusTotalAmount(map);
			sysLogService.addSysLog(1, LogTypeEunm.T43, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			
//			Map<String, Object> paramMap = new HashMap<String, Object>();
//			paramMap.put("companyId", map.get("companyId"));
//			dfPayCompanyRateService.delete(paramMap);
//			sysLogService.addSysLog(1, LogTypeEunm.T42, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			returnMap.put("resultCode", 200);
		} catch (Exception e) {
			logger.error("为商户添加代付通道提交失败", e);
			e.printStackTrace();
		}
		return returnMap;
	}
}
