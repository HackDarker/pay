package com.pay.manger.controller.payv2;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.date.DateStyle;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.jk.entity.JkCompanyInfo;
import com.pay.business.jk.entity.JkCompanyRecharge;
import com.pay.business.jk.mapper.JkCompanyInfoMapper;
import com.pay.business.jk.service.JkCompanyInfoService;
import com.pay.business.jk.service.JkCompanyRechargeService;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2JK/*")
public class Payv2JKController extends BaseManagerController<JkCompanyInfo, JkCompanyInfoMapper> {
	private static final Logger logger = Logger.getLogger(Payv2JKController.class);
	
	@Autowired
	private JkCompanyInfoService jkCompanyInfoService;
	
	@Autowired
	private JkCompanyRechargeService jkCompanyRechargeService;
	
	@Autowired
	private Payv2ChannelService payv2ChannelService;
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@Autowired
    private SysLogService sysLogService;
	/**
	 * 接口余额充值页面
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("editJKPage")
	public ModelAndView editJKPage(@RequestParam Map<String, Object> map){
		ModelAndView mv = new ModelAndView("payv2/jk/jk_company_info_edit");
		//获取当前商户的的借口信息
		JkCompanyInfo jkCompanyInfo = jkCompanyInfoService.detail(map);
		mv.addObject("jkCompanyInfo", jkCompanyInfo == null ? new JkCompanyInfo() : jkCompanyInfo);
		mv.addObject("companyId", map.get("companyId"));
		return mv;
	}
	/**
	 * 接口余额充值
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("editJKMoney")
	public Map<String, Object> editJKMoney(@RequestParam Map<String, Object> map,HttpServletRequest request){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			// 累加商户接口金额
			Long companyId = Long.valueOf(map.get("companyId").toString());
			Double serviceMoney = Double.valueOf(map.get("serviceMoney").toString());
			Double addBalance = 0.00;
			if(map.get("paymentMoney") != null && map.get("paymentMoney") != ""){
				addBalance = Double.valueOf(map.get("paymentMoney").toString());
			}
			map.remove("serviceMoney");
			map.remove("paymentMoney");
			JkCompanyInfo jkCompanyInfo = jkCompanyInfoService.detail(map);
			Date now = new Date();
			Map<String,Object> paramMap = new HashMap<String,Object>();
			if(jkCompanyInfo == null){
				jkCompanyInfo = new JkCompanyInfo();
				jkCompanyInfo.setCompanyId(companyId);
				jkCompanyInfo.setBalance(addBalance);
				jkCompanyInfo.setCallCount(0);
				jkCompanyInfo.setServiceMoney(serviceMoney);
				jkCompanyInfo.setStatus(1);
				jkCompanyInfo.setCreateTime(now);
				jkCompanyInfoService.add(jkCompanyInfo);
				//添加日志
				paramMap.put("companyId", companyId);
				paramMap.put("addBalance", addBalance);
				paramMap.put("callCount", 0);
				paramMap.put("serviceMoney", serviceMoney);
				paramMap.put("status", 1);
				paramMap.put("createTime", DateUtil.DateToString(now, DateStyle.YYYY_MM_DD_HH_MM));
				sysLogService.addSysLog(1, LogTypeEunm.T61, IpAddressUtil.getIpAddress(request), getAdmin().getId(), paramMap);
			}else{
				jkCompanyInfo.setBalance(DecimalUtil.add(jkCompanyInfo.getBalance(), addBalance, 4)  );
				jkCompanyInfo.setServiceMoney(serviceMoney);
				jkCompanyInfoService.update(jkCompanyInfo);
				//添加日志
				paramMap.clear();
				paramMap.put("balance", DecimalUtil.add(jkCompanyInfo.getBalance(), addBalance, 4)  );
				paramMap.put("serviceMoney", serviceMoney);
				sysLogService.addSysLog(1, LogTypeEunm.T62, IpAddressUtil.getIpAddress(request), getAdmin().getId(), paramMap);
			}
			// 添加充值记录
			if(addBalance>0){
				JkCompanyRecharge jkCompanyRecharge = new JkCompanyRecharge();
				jkCompanyRecharge.setInfoId(jkCompanyInfo.getId());
				jkCompanyRecharge.setCompanyId(companyId);
				jkCompanyRecharge.setAddBalance(addBalance);
				jkCompanyRecharge.setCreateTime(now);
				jkCompanyRechargeService.add(jkCompanyRecharge);
				//添加日志
				paramMap.clear();
				paramMap.put("infoId", jkCompanyInfo.getId());
				paramMap.put("companyId", companyId);
				paramMap.put("addBalance", addBalance);
				paramMap.put("createTime", DateUtil.DateToString(now, DateStyle.YYYY_MM_DD_HH_MM));
				sysLogService.addSysLog(1, LogTypeEunm.T63, IpAddressUtil.getIpAddress(request), getAdmin().getId(), paramMap);
			}
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			logger.error("接口商户金额充值错误", e);
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, "充值失败！");
		}
		return resultMap;
	}
	/**
	 * 商户接口信息页面
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("JKInfoPage")
	public ModelAndView JKInfoPage(@RequestParam Map<String, Object> map){
		ModelAndView mv = new ModelAndView("payv2/jk/jk_company_info_list");
		//渠道商列表
		Map<String, Object> param = new HashMap<>();
		List<Payv2Channel> channelList = payv2ChannelService.query(param);
		//商户列表
		Payv2BussCompany company = new Payv2BussCompany();
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);		
		mv.addObject("channelList", channelList);
		mv.addObject("companyList", companyList);		
		return mv;
	}	
	/**
	 * 商户接口信息分页数据
	 * 
	 * @param map
	 * @return
	 */	
	@RequestMapping("JKInfoList")
	public ModelAndView JKInfoList(@RequestParam Map<String, Object> map){
		ModelAndView mv = new ModelAndView("payv2/jk/jk_company_info_list");
		//渠道商列表
		Map<String, Object> param = new HashMap<>();
		List<Payv2Channel> channelList = payv2ChannelService.query(param);
		//商户列表
		Payv2BussCompany company = new Payv2BussCompany();
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(company);
		// 根据搜索条件获取分页数据
		PageObject<JkCompanyInfo> JKInfoPage = jkCompanyInfoService.getPageObject(map);
		mv.addObject("channelList", channelList);
		mv.addObject("companyList", companyList);
		mv.addObject("list", JKInfoPage);
		mv.addObject("map", map);
		return mv;
	}
}
