package com.pay.manger.controller.payv2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.core.teamwork.base.util.date.DateUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.merchant.entity.Payv2BussCompany;
import com.pay.business.merchant.entity.Payv2BussCompanyApp;
import com.pay.business.merchant.entity.Payv2Channel;
import com.pay.business.merchant.service.Payv2BussCompanyAppService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.merchant.service.Payv2ChannelService;
import com.pay.business.payway.entity.Payv2PayWay;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.payway.service.Payv2PayWayService;
import com.pay.business.record.entity.PayDataRecord;
import com.pay.business.record.mapper.PayDataRecordMapper;
import com.pay.business.record.service.PayDataRecordService;
import com.pay.business.util.ParameterEunm;
import com.pay.manger.controller.admin.BaseManagerController;

@Controller
@RequestMapping("/Payv2BussiManagerAlldata/*")
public class Payv2BussiManagerAlldataController extends BaseManagerController<PayDataRecord, PayDataRecordMapper> {


	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private PayDataRecordService payDataRecordService;	
	@Autowired
	private Payv2ChannelService payv2ChannelService;//渠道商服务	
	@Autowired
	private Payv2BussCompanyAppService payv2BussCompanyAppService;//商户App服务	
	@Autowired
	private Payv2PayWayRateService payv2PayWayRateService;//支付渠道服务
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	@Autowired
	private Payv2PayWayService payv2PayWayService;//支付渠道服务
	
	/**
	 * 交易汇总默认加载页面
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("alldata")
	public ModelAndView showDetailDay(@RequestParam Map<String, Object> map) {
		ModelAndView mv = new ModelAndView("payv2/businessmanage/payv2_bussiness_manage_alldata");	
		//获取渠道商列表
    	Payv2Channel payv2Channel=new Payv2Channel();
//    	payv2Channel.setIsDelete(2);
    	List<Payv2Channel>	payv2ChannelList=payv2ChannelService.selectByObject(payv2Channel);
    	// 获取商户列表
    	Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
//		payv2BussCompany.setIsDelete(2);
		List<Payv2BussCompany> companyList = payv2BussCompanyService.selectByObject(payv2BussCompany);
    	// 获取应用列表    	
    	Payv2BussCompanyApp payv2BussCompanyApp = new Payv2BussCompanyApp();
//    	payv2BussCompanyApp.setIsDelete(2);
    	List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(payv2BussCompanyApp);
    	//获取支付平台列表
    	Payv2PayWay pay = new Payv2PayWay();
//		pay.setIsDelete(2);
		List<Payv2PayWay> payWayList = payv2PayWayService.selectByObject(pay);
    	//获取支付通道列表
//		map.put("status", 1);
//		map.put("isDelete", 2);
    	List<Payv2PayWayRate> payWayRateList =payv2PayWayRateService.query(map);
    	
    	mv.addObject("payv2ChannelList", payv2ChannelList);
    	mv.addObject("companyList", companyList);
    	mv.addObject("appList",appList);
    	mv.addObject("payWayList",payWayList);
    	mv.addObject("payWayRateList", payWayRateList);
		return mv;
	}
	
	/**
	 * 根据商户id查询对应的应用
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
    @RequestMapping("getApps")
    public Map<String,Object> getApps(@RequestParam Long companyId){
		Map<String,Object> resultMap = new HashMap<>();
    	try {
    		Payv2BussCompanyApp pbca = new Payv2BussCompanyApp();
    		pbca.setCompanyId(companyId);
//    		pbca.setIsDelete(2);
    		List<Payv2BussCompanyApp> appList = payv2BussCompanyAppService.selectByObject(pbca);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, appList);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取商户App失败", e);
		}
    	return resultMap;
    }
	
	/**
	 * 根据channelID获取其支持的支付渠道
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
    @RequestMapping("getPayWaysByChannel")
    public Map<String,Object> getPayWaysByChannel(@RequestParam Map<String, Object> map){
		Map<String,Object> resultMap = new HashMap<>();
    	try {
    		List<Payv2PayWayRate> payWayList =payv2PayWayRateService.queryByChannelWayId(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, payWayList);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取渠道商支付渠道失败", e);
		}
    	return resultMap;
    }
	
	
	/**
	 * 根据companyID获取其支持的支付渠道
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
    @RequestMapping("getPayWays")
    public Map<String,Object> getPayWays(@RequestParam Map<String, Object> map){
		Map<String,Object> resultMap = new HashMap<>();
    	try {
//    		map.put("isDelete", 2);
    		List<Payv2PayWayRate> payWayList =payv2PayWayRateService.getPayWaysByCom(map);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, payWayList);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取商户支付渠道失败", e);
		}
    	return resultMap;
    }
	
	/**
	 * 关键数据获取
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
    @RequestMapping("curxData")
    public Map<String,Object> curxData(@RequestParam Map<String, Object> map){
		Map<String,Object> resultMap = new HashMap<>();
		Integer dateType = NumberUtils.createInteger(map.get("dateType").toString());
    	try {
    		PayDataRecord curxData = new PayDataRecord();
    		// 判断dateType，如果为1，2，查实时
    		if(dateType ==1 || dateType ==2 ){
    			curxData = payDataRecordService.curxDataNow(map);
    		// 如果为3，4，实时、统计数据并查
    		}else if(dateType ==3||dateType ==4){
    			curxData = payDataRecordService.curxDataTOG(map);
    		// 判断自定义时间的查询范围1：只查实时数据，2：实时、统计数据并查，3：只查询统计数据
    		}else if(dateType == 5){
    			int type = comparDate(map.get("startTime").toString(),map.get("endTime").toString());
    			if(type == 1){
    				curxData = payDataRecordService.curxDataNow(map);
    			}else if(type == 2){    				
    				curxData = payDataRecordService.curxDataTOG(map);
    			}else if(type == 3){
					curxData = payDataRecordService.curxData(map);
				}
    		}
    		resultMap.put("curxData", curxData);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, curxData);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("关键数据获取失败", e);
		}
    	return resultMap;
    }	 
	/**
	 * 判断自定义时间查询的方式
	 * 
	 * @param startTime
	 * @param endTime
	 * @return 1：只查实时数据，2：实时数据与统计数据 并查，3：只查询统计数据
	 */
	public static int comparDate(String startT,String endT) {		
		try {			
			long date1 = new Date().getTime();
			long date2 = DateUtil.addDay(new Date(), -1).getTime();
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			long now = DateUtil.unixToDate(date1,df).getTime();
			long yesterday = DateUtil.unixToDate(date2,df).getTime();
			
			long startTime = df.parse(startT).getTime();
			long endTime = df.parse(endT).getTime();
			if(startTime >= yesterday && endTime <= now){
				//其实时间大于等于昨天，查实时
				return 1;
			}else if(startTime < yesterday && endTime >= yesterday){
				//结束时间大于等于昨天，起始时间小于昨天，实时统计并查
				return 2;
			}else if(endTime < yesterday){
				//结束时间小于昨天，查统计
				return 3;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	/**
	 * 交易时刻趋势图
	 * 
	 * @param map
	 * @return
	 */
	@ResponseBody
    @RequestMapping("timeTrend")
    public Map<String,Object> timeTrend(@RequestParam Map<String, Object> map){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Integer queryType = NumberUtils.createInteger(map.get("dateType").toString());
			List<PayDataRecord> resList = null;
			//今日、昨日查实时
			if(queryType == 1 || queryType == 2){
				map.put("isNow", 1);
				resList = payDataRecordService.groupByHour(map);
				resultMap.put("resultType", 1);
			//前7天、30天实时、统计数据并查
			}else if(queryType == 3 || queryType == 4){
				map.put("dateType", 5);
				resList = payDataRecordService.groupByDayTOG(map);
				resultMap.put("resultType", 2);
			//自定义时间--判断起始时间与结束时间是否在同一天 true：以小时为单位查询，false:以天为单位查询
			}else if(queryType == 5){
				String startTime = map.get("startTime").toString();
				String endTime = map.get("endTime").toString();
				if(startTime.substring(0, 10).equals(endTime.substring(0, 10))){
					// 判断自定义时间的查询范围1：只查实时数据，3：只查询统计数据（以小时为单位查询只有这两种情况）
					int type = comparDate(startTime,endTime);
					if(type == 1){
						map.put("isNow", 1);
						resList = payDataRecordService.groupByHour(map);
					}else if(type == 3){
						map.put("isNow", 0);
						resList = payDataRecordService.groupByHour(map);
					}
					resultMap.put("resultType", 1);
				}else{
					// 判断自定义时间的查询范围1：只查实时数据，2：实时、统计数据并查，3：只查询统计数据
					int type = comparDate(startTime,endTime);
					if(type == 1){
						map.put("isNow", 1);
						resList = payDataRecordService.groupByDay(map);
					}else if(type == 2){
						resList = payDataRecordService.groupByDayTOG(map);
					}else if(type == 3){
						map.put("isNow", 0);
						resList = payDataRecordService.groupByDay(map);
					}
					resultMap.put("resultType", 2);
				}
			}
    		resultMap.put("resultData", resList);
    		resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE, null);
			logger.error("获取交易时刻趋势图数据失败", e);
		}
		return resultMap;
	}
	
}
