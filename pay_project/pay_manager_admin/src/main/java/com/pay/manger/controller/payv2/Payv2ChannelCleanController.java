package com.pay.manger.controller.payv2;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.order.entity.OrderClearVO;
import com.pay.business.order.entity.Payv2PayOrder;
import com.pay.business.order.entity.Payv2PayOrderClear;
import com.pay.business.order.mapper.Payv2PayOrderClearMapper;
import com.pay.business.order.service.Payv2PayOrderClearService;
import com.pay.business.order.service.Payv2PayOrderService;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.IpAddressUtil;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.ReaderExcelUtils;
import com.pay.manger.controller.admin.BaseManagerController;

/**
 * <p>Title:渠道对账</p>
 * <p>Description: </p>
 * @author yy
 * 2017年8月3日
 */
@Controller
@RequestMapping("/channelClean")
public class Payv2ChannelCleanController extends BaseManagerController<Payv2PayOrderClear, Payv2PayOrderClearMapper> {
	Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private Payv2PayOrderClearService clearService;
	
	@Autowired
	private Payv2PayWayRateService wayService;

    @Autowired
    private SysLogService sysLogService;
	
    @Autowired
    private ReaderExcelUtils reu;
    
    @Autowired
    private Payv2PayOrderService orderService;
    
	/**
	 * @return	跳转用
	 */
	@RequestMapping("/channelClean")
	public ModelAndView showDetailDay() {
		ModelAndView mv = new ModelAndView("payv2/companyMoneyClear/channelClean");	
		return mv;
	}
	
	/**
	 * @return	跳转用
	 */
	@RequestMapping("/differDetail")
	public ModelAndView differDetail(String date, String rateid) {
		ModelAndView mv = new ModelAndView("payv2/companyMoneyClear/differDetail");	
		mv.addObject("date", date);
		mv.addObject("rateid", rateid);
		return mv;
	}
	
	/**
	 * @return	获取待对账账单时间
	 */
	@RequestMapping(value = "/getBills", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBills() {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<String> bills = clearService.getBills();
			result.put("code", 200);
			result.put("data", bills);
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("查询待对账账单", e);
		}
		return result;
	}
	
	/**
	 * @return	根据时间获取对账列表
	 */
	@RequestMapping(value = "/getBillList", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBillList(String date) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Assert.notNull(date, "日期不能为空！");
			List<Map<String, Object>> billList = clearService.getBillList(date);
			result.put("code", 200);
			result.put("data", billList);
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("查询待对账账单", e);
		}
		return result;
	}
	
	/**
	 * @return	获取差错列表
	 */
	@RequestMapping(value = "/getDifferOrder", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getDifferOrder(@RequestParam Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PageObject<OrderClearVO> differOrder = clearService.getDifferOrder(map);
			result.put("code", 200);
			result.put("data", differOrder);
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("获取差错订单", e);
		}
		return result;
	}
	
	/**
	 * 平帐
	 */
	@RequestMapping(value = "/updateClear", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> updateClear(String ids,HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (StringUtils.isEmpty(ids)) {
				throw new NullPointerException();
			}
			clearService.updateClear(ids.trim());
			result.put("ids", ids);
			sysLogService.addSysLog(1, LogTypeEunm.T31, IpAddressUtil.getIpAddress(request), getAdmin().getId(), result);
			result.clear();
			result.put("code", 200);
		} catch (NullPointerException n) {
			result.put("code", 101);
			result.put("msg", "请选择！");
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("平帐", e);
		}
		return result;
	}
	
	/**
	 * 对账
	 */
	@RequestMapping(value = "/toBills", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> toBills(String date, String rateid,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> oderAndRefundByTime = clearService.getOderAndRefundByTime(date, Long.valueOf(rateid));
			Payv2PayWayRate rate = wayService.queryByid(Long.valueOf(rateid));
			String payName = "";
			if (rate.getPayViewType() == 1) {
				payName = "支付宝";
			} else if (rate.getPayViewType() == 2) {
				payName = "微信";
			}
			result.put("date", date);
			result.put("rateid", rateid);
			sysLogService.addSysLog(1, LogTypeEunm.T30, IpAddressUtil.getIpAddress(request), getAdmin().getId(), result);
			result.clear();
			clearService.job(date, date.replace("-", ""), oderAndRefundByTime.get(1), oderAndRefundByTime.get(0), payName, rate);
			result.put("code", 200);
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("对账", e);
		}
		return result;
	}
	
	/**
	 * 出账
	 */
	@RequestMapping(value = "/outOrder", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> outOrder(String date, String rateid,HttpServletRequest request) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Assert.notNull(date, "日期不能为空！");
			Assert.notNull(rateid, "渠道不能为空！");
			clearService.updateOutOrder(date, rateid);
			result.put("date", date);
			result.put("rateid", rateid);
			sysLogService.addSysLog(1, LogTypeEunm.T32, IpAddressUtil.getIpAddress(request), getAdmin().getId(), result);
			result.clear();
			result.put("code", 200);
		} catch (Exception e) {
			result.put("code", 101);
			result.put("msg", e.getMessage());
			logger.error("出账", e);
		}
		return result;
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
			//失败录入单号
			List<String> failList = new ArrayList<String>();
			for (Map map : dataListMap) {
				//以后加if做不同类型excel导入处理
				guofuToBills(map, failList);
			}
			result.put("code", 200);
			result.put("msg", failList.toString());
			System.out.println("失败处理订单号：" + failList.toString());
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.getMessage());
			logger.error("excel对账单导入："+e);
		}
		return result;
	}
	
	/**
	 * 国付对账
	 */
	public void guofuToBills(Map<String,Object> map, List<String> failList) throws Exception {
		Payv2PayOrder order = new Payv2PayOrder();
		order.setOrderNum(map.get("商户流水号")+"");
		order = orderService.selectSingle(order);
		if(null == order){
			failList.add(map.get("商户流水号")+"");
		}else {
			Payv2PayOrderClear clear = new Payv2PayOrderClear();
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(order.getMerchantOrderNum())) {
				clear.setMerchantOrderNum(order.getMerchantOrderNum());// 商家订单号
			}
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(order.getOrderNum())) {
				clear.setOrderNum(order.getOrderNum());// 支付集订单号
			}
			if (order.getCompanyId() != null) {
				clear.setCompanyId(order.getCompanyId());// 商户id,关联payv2_buss_company表
			}

			if (order.getAppId() != null) {
				clear.setAppId(order.getAppId());// 应用id，关联payv2_buss_company_app或payv2_buss_company_shop表
			}

			if (order.getPayWayId() != null) {
				clear.setPayWayId(order.getPayWayId());// 支付渠道，关联payv2_pay_way表
			}

			if (order.getOrderType() > 0) {
				clear.setMerchantType(order.getOrderType());// 商户类型1.线上2.线下
			}
			
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(order.getOrderName())) {
				clear.setOrderName(order.getOrderName());// 订单名称
			}
			clear.setType(1);// 类型1.交易2.退款
			clear.setCreateTime(new Date());
			double paymoney = Double.valueOf(map.get("交易金额")+"");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date finishedTimeDate = df.parse(map.get("交易日期")+"");
			if (order.getPayMoney() != null && order.getPayDiscountMoney().doubleValue() == paymoney) {
				clear.setStatus(1);// 订单正常
				if (order.getPayStatus().intValue() != 1&&order.getPayStatus().intValue() != 5) {
					clear.setStatus(3);// 记录可能异常订单
					// 更新订单状态
					Payv2PayOrder der = new Payv2PayOrder();
					der.setId(order.getId());
					der.setPayStatus(1);// 成功
					der.setPayTime(finishedTimeDate);
					order.setPayDiscountMoney(paymoney);
					order.setPayTime(finishedTimeDate);
					orderService.update(der);
				}
				order.setAlipayOrderType("交易");// 对账单类型
				order.setPayType(map.get("支付方式")+"");// 支付类型

				if (order.getPayTime() == null) {
					order.setPayTime(finishedTimeDate);
				}
				clear.setOrderMoney(order.getPayMoney());
			} else {
				clear.setStatus(2);// 订单异常
				clear.setOrderMoney(order.getPayMoney());
			}
			if (clear != null) {
				clear.setUpstreamTime(finishedTimeDate);
				clear.setUpstreamAmount(paymoney);
				clear.setTransactionId(map.get("商户订单号")+"");
				clear.setClearTime(finishedTimeDate);
				clear.setUpstreamStatus(1);
				clear.setPayStatus(1);
				clearService.updateByOrderNum(clear);
			}
		}
	}
}
