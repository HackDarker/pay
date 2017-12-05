package com.pay.manger.controller.payv2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.admin.entity.SysUcenterAdmin;
import com.pay.business.admin.service.SysUcenterAdminService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.mapper.Payv2PayOrderMapper;
import com.pay.business.sysconfig.entity.SysLog;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.LogTypeEunm;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * @ClassName: Payv2PayOrderController
 * @Description:订单管理
 * @author zhoulibo
 * @date 2016年12月7日 下午3:10:42
 */
@Controller
@RequestMapping("/payv2SysLog/*")
public class Payv2SysLogController extends BaseManagerController<Payv2PayOrder, Payv2PayOrderMapper> {

	private static final Logger logger = Logger.getLogger(Payv2SysLogController.class);
	@Autowired
	private SysLogService sysLogService;

	@Autowired
	private SysUcenterAdminService sysUcenterAdminService;
	
	/**
	 * @Title: getPayv2PayOrderList
	 * @Description:获取订单管理列表
	 * @param map
	 * @return 设定文件
	 * @return ModelAndView 返回类型
	 * @date 2016年12月7日 下午3:12:21
	 * @throws
	 */
	@RequestMapping("getSysLogList")
	public ModelAndView getPayv2PayOrderList(@RequestParam Map<String, Object> map) {
		ModelAndView av = new ModelAndView("payv2/syslog/payv2SysLog-list");
		if(!map.containsKey("sysType")){
			map.put("sysType", 1);
		}		
		PageObject<SysLog> pageObject = sysLogService.getSysLogPageList(map);
		Map<String, Object> aMap = new HashMap<String, Object>();
		List<SysUcenterAdmin> admins = sysUcenterAdminService.query(aMap);
		
		av.addObject("map", map);
		av.addObject("adminList", admins);
		av.addObject("executeTypeList", LogTypeEunm.getAllType());
		av.addObject("list", pageObject);
		return av;
	}
}
