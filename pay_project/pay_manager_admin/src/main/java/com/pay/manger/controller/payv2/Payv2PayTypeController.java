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

import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.payway.entity.Payv2PayType;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.mapper.Payv2PayTypeMapper;
import com.pay.business.payway.service.Payv2PayTypeService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/payType/*")
public class Payv2PayTypeController extends BaseManagerController<Payv2PayType, Payv2PayTypeMapper> {

	@Autowired
	private Payv2PayTypeService payv2PayTypeService;
	@Autowired
	private Payv2PayWayService payv2PayWayService;
	@Autowired
    private SysLogService sysLogService;
	private static final Logger logger = Logger.getLogger(Payv2PayTypeController.class);
	/**
	 * 支付方式管理页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@RequestMapping("alldata")
	public ModelAndView alldata() {
		ModelAndView mv = new ModelAndView("payv2/paytype/payv2paytype-list");
		// 获取所有支付方式列表，默认显示所有数据
		List<Payv2PayType> payTypeList = payv2PayTypeService.payTypeList();
		mv.addObject("payTypeList", payTypeList);
		return mv;
	}
	/**
	 * 新增支付方式
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@RequestMapping("addPaytypePage")
	public ModelAndView addPaytypePage(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/paytype/payv2paytype-add");
		// 获取所有支付平台
		List<Payv2PayWay> payWayList = payv2PayWayService.query(map);
		mv.addObject("payWayList", payWayList);
		return mv;
	}
	/**
	 * 新增支付方式
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@ResponseBody
	@RequestMapping("addPaytype")
	public Map<String,Object> addPaytype(HttpServletRequest request,@RequestParam Map<String, Object> map) {
    	Map<String,Object> resultMap=new HashMap<String, Object>();
		// 新增支付方式
		try {
			// 添加验重：字典匹配值唯一，支付方式名称唯一
			//字典匹配值验重
			Map<String,Object> paramMap=new HashMap<String, Object>();
			Payv2PayType payv2PayType = new Payv2PayType();
			paramMap.put("dictName",map.get("dictName").toString());
			payv2PayType = payv2PayTypeService.detail(paramMap);
			if(payv2PayType != null){
				resultMap.put("resultCode", 201);
				return resultMap;
			}
			paramMap.clear();
			//当前支付平台下支付名称验重
			paramMap.put("payWayId",map.get("payWayId").toString());
			paramMap.put("payTypeName",map.get("payTypeName").toString());
			payv2PayType = payv2PayTypeService.detail(paramMap);
			if(payv2PayType != null){
				resultMap.put("resultCode", 202);
				return resultMap;
			}
			//添加支付方式
			map.put("createTime", new Date());
			payv2PayTypeService.add(map);
			sysLogService.addSysLog(1, LogTypeEunm.T56, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("新增支付方式错误！");
		}
		return resultMap;
	}
	/**
	 * 修改支付方式页面
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@RequestMapping("updatePaytypePage")
	public ModelAndView updatePaytypePage(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/paytype/payv2paytype-edit");		
		// 获取当前支付方式信息
		Payv2PayType payType = payv2PayTypeService.detail(map);
		// 获取所有支付平台
		map.clear();
		List<Payv2PayWay> payWayList = payv2PayWayService.query(map);
		mv.addObject("payWayList", payWayList);
		mv.addObject("payType", payType);
		return mv;
	}
	/**
	 * 修改支付方式
	 * 
	 * @param map
	 * @return ModelAndView
	 */
	@ResponseBody
	@RequestMapping("updatePaytype")
	public Map<String, Object> updatePaytype(HttpServletRequest request,@RequestParam Map<String, Object> map) {
		Map<String,Object> resultMap=new HashMap<String, Object>();
		// 新增支付方式
		try {
			// 添加验重：字典匹配值唯一，支付方式名称唯一
			//字典匹配值验重
			Map<String,Object> paramMap=new HashMap<String, Object>();
			Payv2PayType payv2PayType = new Payv2PayType();
			paramMap.put("dictName",map.get("dictName").toString());
			payv2PayType = payv2PayTypeService.detail(paramMap);
			// 因为是修改，验重的时候的排除掉自身
			if(payv2PayType != null && payv2PayType.getId() != Long.valueOf(map.get("id").toString())){
				resultMap.put("resultCode", 201);
				return resultMap;
			}
			paramMap.clear();
			//当前支付平台下支付名称验重
			paramMap.put("payWayId",map.get("payWayId").toString());
			paramMap.put("payTypeName",map.get("payTypeName").toString());
			payv2PayType = payv2PayTypeService.detail(paramMap);
			if(payv2PayType != null && payv2PayType.getId() != Long.valueOf(map.get("id").toString())){
				resultMap.put("resultCode", 202);
				return resultMap;
			}
			// 更新支付方式
			payv2PayTypeService.update(map);
			sysLogService.addSysLog(1, LogTypeEunm.T57, IpAddressUtil.getIpAddress(request), getAdmin().getId(), map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("修改支付方式错误！");
		}
		return resultMap;
	}
	/**
	 * 获取支付方式详情
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping("detailPaytype")
	public Map<String,Object> detailPaytype(HttpServletRequest request,@RequestParam Map<String, Object> map) {
    	Map<String,Object> resultMap=new HashMap<String, Object>();
		try {
			Payv2PayType payv2PayType = payv2PayTypeService.detail(map);
			resultMap.put("payv2PayType", payv2PayType);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("查询支付方式错误");
		}
		return resultMap;
	}
}
