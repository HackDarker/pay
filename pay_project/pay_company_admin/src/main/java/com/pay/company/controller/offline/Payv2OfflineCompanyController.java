package com.pay.company.controller.offline;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.encrypt.MD5;
import com.core.teamwork.base.util.page.PageObject;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyCode;
import com.pay.business.merchant.service.Payv2BussCompanyCodeService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.mapper.Payv2PayOrderMapper;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.util.ParameterEunm;
import com.pay.company.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/offline/*")
public class Payv2OfflineCompanyController extends BaseManagerController<Payv2PayOrder, Payv2PayOrderMapper> {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private Payv2PayOrderService payv2PayOrderService;
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@Autowired
	private Payv2BussCompanyCodeService payv2BussCompanyCodeService;
	
	
	@ResponseBody
	@RequestMapping("orderList")
	public Map<String, Object> getPayv2PayOrderList(@RequestParam Map<String, Object> map) {		
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		boolean isNotNull = ObjectUtil.checkObject(new String[] { "orderTime" }, map);
		if (!isNotNull) {
			return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "参数不允许为空");
		}
		if(!map.containsKey("token")){
			return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "无设备标识");
		}
		
		//获取当前商户的token信息	
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("token", map.get("token"));
		Payv2BussCompanyCode payv2BussCompanyCode = payv2BussCompanyCodeService.detail(paramMap);		
		// 验证参数token与数据库是否一致
		if(payv2BussCompanyCode == null){
			return resultMap = ReMessage.resultBack(ParameterEunm.NOT_LOGIN, null, "未登录");
		}
		
		//参数封装
		String orderTime = map.get("orderTime").toString();
		map.remove("orderTime");
		map.put("orderType", 2);// 线下
		
		map.put("companyId", payv2BussCompanyCode.getCompanyId());
		map.put("payTimeBegin", orderTime+" 00:00:00");
		map.put("payTimeEnd", orderTime+" 23:59:59");
		try {
			//流水列表
			PageObject<Payv2PayOrder> pageList = payv2PayOrderService.offlineOrderList(map);
			// 流水总金额
			Double totalMoney = payv2PayOrderService.offlineOrderTotalMoney(map);
			resultMap.put("pageList", pageList);
			resultMap.put("totalMoney", String.format("%.2f", totalMoney));
			resultMap.put("totalData", pageList.getTotalData());
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, resultMap);
		}		
		return resultMap;
	}
	/**
	 * @Title: login
	 * @Description: 登录
	 * @param @param map
	 * @param @param request
	 * @param @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @throws
	 */
	@RequestMapping("login")
	@ResponseBody
	public Map<String, Object> login(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		String resultMsgNull = "参数不允许为空";
		String resultMsgError = "服务器异常,请稍后再试";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean isNotNull = ObjectUtil.checkObject(new String[] { "userName", "password" }, map);
		if (!isNotNull) {
			return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, resultMsgNull);
		}
		if(null == map.get("token") || "".equals(map.get("token").toString())){
			return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "无设备标识");
		}
		String userName = map.get("userName").toString();
		String password = map.get("password").toString();
		String token = map.get("token") == null?"":map.get("token").toString();
		// 密码为md5,由前端加密后与数据库比对
		try {
			int count = payv2BussCompanyService.getCountByUserName(userName);
			if (count > 0) {
				password = MD5.GetMD5Code(password);
				Payv2BussCompany bussCompany = payv2BussCompanyService.login(userName, password);
				if (bussCompany == null || bussCompany.getLicenseType() == null) {
					resultMap = ReMessage.resultBack(ParameterEunm.USER_AND_PASSWORD_MISMATCH, null, "帐号和密码不匹配");
				} else if (bussCompany.getIsDelete() == 1 ) {
					resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "对不起,该商户已被删除,请联系工作人员");
				}else {
					// 用户存在
					
					//获取当前商户的token信息	
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("companyId", bussCompany.getId());
					Payv2BussCompanyCode payv2BussCompanyCode = payv2BussCompanyCodeService.detail(paramMap);
					if(payv2BussCompanyCode == null){
						return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "当前商户缺少信息，请联系技术支持！");
					}
					//更新商户的token信息
					Payv2BussCompanyCode t = new Payv2BussCompanyCode();
					t.setId(payv2BussCompanyCode.getId());
					t.setToken(token);
					payv2BussCompanyCodeService.update(t);
					
					resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);// 存在
				}
			} else {
				resultMap = ReMessage.resultBack(ParameterEunm.USER_NO_EXIST, null, "用户不存在");// 用户不存在
			}
		} catch (Exception e) {
			logger.error("支付集商户后台登录异常:" + map.toString(), e);
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null, resultMsgError);
		}
		return resultMap;
	}
	
	/**
	 * @Title: loginOut
	 * @Description: 退出登录
	 * @param @param map
	 * @param @param request
	 * @param @return 设定文件
	 * @return Map<String,Object> 返回类型
	 * @throws
	 */
	@RequestMapping("loginOut")
	@ResponseBody
	public Map<String, Object> loginOut(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		try {
			if(!map.containsKey("token")){
				return resultMap = ReMessage.resultBack(ParameterEunm.FAILED_CODE, null, "无设备标识");
			}
			//获取当前商户的token信息
			Payv2BussCompanyCode payv2BussCompanyCode = payv2BussCompanyCodeService.detail(map);
			// 将数据库token置为空
			Payv2BussCompanyCode t = new Payv2BussCompanyCode();
			t.setId(payv2BussCompanyCode.getId());
			t.setToken("");
			payv2BussCompanyCodeService.update(t);
			
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, null);
		} catch (Exception e) {
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null, "服务器正在裸奔,请稍候重试");
		}
		return resultMap;
	}
	public static void main(String[] args) {
		System.out.println(MD5.GetMD5Code(MD5.GetMD5Code("123123")));
	}
	
}
