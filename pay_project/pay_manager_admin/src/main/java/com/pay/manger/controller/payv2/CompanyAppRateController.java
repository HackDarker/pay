package com.pay.manger.controller.payv2;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.mapper.Payv2PayOrderMapper;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * @ClassName: Payv2PayOrderController
 * @Description:订单管理
 * @author zhoulibo
 * @date 2016年12月7日 下午3:10:42
 */
@Controller
@RequestMapping("/companyAppRate/*")
public class CompanyAppRateController extends BaseManagerController<Payv2PayOrder, Payv2PayOrderMapper> {

	private static final Logger logger = Logger.getLogger(CompanyAppRateController.class);
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@ResponseBody
	@RequestMapping("generate")
	public Map<String, Object> searchPayv3PayOrderList(@RequestParam Map<String, Object> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			returnMap = payv2BussCompanyService.generate(map);
			returnMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, returnMap);
		} catch (Exception e) {
			e.printStackTrace();
			returnMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
		}
		return returnMap;
	}
	
}
