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

import com.core.teamwork.base.util.ReadProChange;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.mapper.Payv2BussCompanyCodeMapper;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/bussComapnyCode/*")
public class Payv2BussCompanyCodeController extends BaseManagerController<Payv2BussCompanyCode,Payv2BussCompanyCodeMapper>{	
	private static final Logger logger = Logger.getLogger(Payv2PayOrderController.class);
	
	@Autowired
	private Payv2ChannelService payv2ChannelService;//渠道商服务
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	@Autowired
	private SysLogService sysLogService;

	/**
	 * 收款码列表
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("codeList")
	public ModelAndView codeList(@RequestParam Map<String,Object> map){
		ModelAndView mv = new ModelAndView("payv2/busscompanyCode/bussCompanyCodeList");
		//获取渠道商列表
    	Payv2Channel payv2Channel=new Payv2Channel();
    	List<Payv2Channel>	payv2ChannelList = payv2ChannelService.selectByObject(payv2Channel);
    	// 获取商户列表
    	Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
		// 获取收款码分页列表
		PageObject<Payv2BussCompanyCode> codeList  = payv2BussCompanyCodeService.pageCode(map);
		mv.addObject("payv2ChannelList", payv2ChannelList);
		mv.addObject("companyList", companyList);
		mv.addObject("codeList", codeList);
		mv.addObject("map", map);
		return mv;
	}
	/**
	 * 查看商户收款码
	 * 
	 * @param codeUrl
	 */
	@ResponseBody
	@RequestMapping("getCode")
	public Map<String,Object> getCode(@RequestParam Map<String,Object> map){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			Payv2BussCompany company = payv2BussCompanyService.detail(map);
			resultMap = payv2BussCompanyCodeService.getCode(company);
			if(resultMap.containsKey("codeFileUrl")){
				resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
			}else{
				resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, resultMap,resultMap.get("message").toString());
			}            
        }catch (Exception e) {
			logger.error("生成商户收款码错误");
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null);
		}
		return resultMap;
	}
	/**
	 * 删除商户收款码
	 * 
	 * @param codeUrl
	 */
	@ResponseBody
	@RequestMapping("delCode")
	public Map<String,Object> delCode(@RequestParam Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			payv2BussCompanyCodeService.delete(map);
			sysLogService.addSysLog(1, LogTypeEunm.T66, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
        }catch (Exception e) {
			logger.error("删除商户收款码错误");
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null);
		}
		return resultMap;
	}
	/**
	 * 新增商户收款码
	 * 
	 * @param codeUrl
	 */
	@ResponseBody
	@RequestMapping("addCode")
	public Map<String,Object> addCode(@RequestParam Map<String,Object> map,HttpServletRequest request){
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			Payv2BussCompany company = payv2BussCompanyService.detail(map);
			map.put("companyId", map.get("id"));
			map.remove("id");
			Payv2BussCompanyCode payv2BussCompanyCode = payv2BussCompanyCodeService.detail(map);
			if(payv2BussCompanyCode != null){
				resultMap.put("resultCode", 201);
				return resultMap;
			}			
			map.clear();
			String codeUrl = ReadProChange.getValue("code_url");
			codeUrl = codeUrl + "?companyKey="+company.getCompanyKey();
			map.put("channelId", company.getChannelId());
			map.put("companyId", company.getId());
			map.put("codeUrl", codeUrl);
			map.put("createTime", new Date());
			payv2BussCompanyCodeService.add(map);
			sysLogService.addSysLog(1, LogTypeEunm.T67, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
        }catch (Exception e) {
			logger.error("删除商户收款码错误");
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null);
		}
		return resultMap;
	}
}
