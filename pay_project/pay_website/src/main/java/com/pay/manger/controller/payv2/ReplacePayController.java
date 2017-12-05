package com.pay.manger.controller.payv2;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.core.teamwork.base.util.ObjectUtil;
import com.core.teamwork.base.util.returnback.ReMessage;
import com.pay.business.df.entity.DfPayCompanyRate;
import com.pay.business.df.entity.DfPayOrder;
import com.pay.business.df.entity.DfPayWayRate;
import com.pay.business.df.service.DfPayCompanyRateService;
import com.pay.business.df.service.DfPayOrderService;
import com.pay.business.df.service.DfPayWayRateService;
import com.pay.business.merchant.service.Payv2BussCompanyService;
import com.pay.business.payv2.service.DFService;
import com.pay.business.util.DecimalUtil;
import com.pay.business.util.ParameterEunm;
import com.pay.business.util.PaySignUtil;
import com.pay.business.util.StringHandleUtil;
import com.pay.business.util.lianlianDF.callback.PartnerConfig;
import com.pay.business.util.lianlianDF.callback.PayDataBean;
import com.pay.business.util.lianlianDF.callback.RetBean;
import com.pay.business.util.lianlianDF.callback.YinTongUtil;


/**
* @ClassName: ReplacePayController 
* @Description:代付集控制器
* @author qiuguojie
* @date 2017年08月30日 下午8:21:52
*/
@Controller
@RequestMapping("/dfPay")
public class ReplacePayController {
	//Logger logger = Logger.getLogger(this.getClass());
	/**
	 * logger
	 */
	private static final Log logger = LogFactory.getLog(ReplacePayController.class);
	
	@Autowired
	private Payv2BussCompanyService payv2BussCompanyService;
	
	@Autowired
	private DFService dfService;
	
	@Autowired
	private DfPayOrderService dfPayOrderService;
	@Autowired
	private DfPayWayRateService dfPayWayRateService;
	@Autowired
	private DfPayCompanyRateService dfPayCompanyRateService;
	
	/**
	 * 代付通道:各个代付支付入口
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/singlePay")
	public Map<String,Object> payOrderDetail(@RequestParam Map<String, Object> map) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean paramStrCon = ObjectUtil.checkObject(new String[] { "paramStr" }, map);
		if(!paramStrCon){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		Map<String, Object> paramMap = StringHandleUtil.toMap(map.get("paramStr").toString());
		if(paramMap.keySet().size()==0){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		//商户号、金额、付款方式、开户账号、卡号、备注、商户订单号、下单时间
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"mchNum", "payMoney", "payType", "acctName", "acctNum", "memo", "merchantOrderNum", "orderTime", "sign"}, paramMap);
		if (!isNotNull){
			logger.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			return resultMap;
		}
		
		//金额是否两位小数、是否为0
		if(!DecimalUtil.isBigDecimal(paramMap.get("payMoney").toString())
				||!DecimalUtil.isZero(paramMap.get("payMoney").toString())){
			logger.debug("支付金额错误");
			resultMap = ReMessage.resultBack(ParameterEunm.MOENY_ERROR,null);
			return resultMap;
		}
		
		try {
			//检查appKey是否有效!先查询app审核是否通过，再查询app所属商户状态是否通过
			Map<String, Object> pbc = payv2BussCompanyService.getPbc(paramMap.get("mchNum").toString());
			if(pbc==null){
				logger.debug("商户号无效");
				resultMap = ReMessage.resultBack(ParameterEunm.COMPANY_KEY_INVALID,null);
				return resultMap;
			}
			//验签
			boolean con = PaySignUtil.checkSign(paramMap, pbc.get("comSecret").toString());
			if(!con){
				logger.debug("商户签名错误");
				resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
				return resultMap;
			}
			//
			Double amount = Double.valueOf(pbc.get("totalAmount").toString());
			Double cost = Double.valueOf(pbc.get("wayRate").toString());
			if(amount-cost < Double.valueOf(paramMap.get("payMoney").toString())){
				logger.debug("余额不足");
				resultMap = ReMessage.resultBack(ParameterEunm.MOENY_INSUFFICIENT,null);
				return resultMap;
			}
			//支付请求
			Map<String, Object> dfResult = dfService.dfResult(pbc, paramMap);
			return dfResult;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("服务器异常,请稍后再试");
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
		}
		return resultMap;
	}
	
	/**
	 *	代付单笔订单查询  （提供给商户服务器查询订单）
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/singleQuery")
	@ResponseBody
	public Map<String,Object> singleQuery(@RequestParam Map<String, Object> map) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean paramStrCon = ObjectUtil.checkObject(new String[] { "paramStr" }, map);
		if(!paramStrCon){
			logger.debug("未传paramStr字段");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		Map<String, Object> paramMap = StringHandleUtil.toMap(map.get("paramStr").toString());
		if(paramMap.keySet().size()==0){
			logger.debug("paramStr参数错误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		boolean appKeyCon = ObjectUtil.checkObject(new String[] { "mchNum" }, paramMap);
		if(!appKeyCon){
			logger.debug("未传商户号");
			resultMap = ReMessage.resultBack(ParameterEunm.COMPANY_KEY_INVALID,null);
			return resultMap;
		}
		//检查appKey是否有效!先查询app审核是否通过，再查询app所属商户状态是否通过
		Map<String, Object> pbc = payv2BussCompanyService.getPbc(paramMap.get("mchNum").toString());
		if(pbc==null){
			logger.debug("商户号无效");
			resultMap = ReMessage.resultBack(ParameterEunm.COMPANY_KEY_INVALID,null);
			return resultMap;
		}
		//验签
		boolean con = PaySignUtil.checkSign(paramMap, pbc.get("comSecret").toString());
		if(!con){
			logger.debug("商户签名错误");
			resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
			return resultMap;
		}
		
		try {
			//商家订单和支付集订单同时不存在
			if(!paramMap.containsKey("jfOrderNum")&&!paramMap.containsKey("merchantOrderNum")){
				logger.debug("1023=未传平台订单号或商户订单号");
				resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ORDER_LACK,null);
			}else{
				return dfPayOrderService.singleQuery(paramMap,pbc);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
		}
		return resultMap;
	}
	/**
	* @Title: receiveNotify 
	* @author ZHOULIBO 
	* @Description: 连连代付-付款结果异步回调通知
	* 				逻辑：连连代付不会实时到账，故在银行给用户打款之后会回调此接口告诉是否打款成功！
	* 				在上游没有接收到我们的成功标识时；会分6次调用此接口，每6分钟一次；
	* @param @param req
	* @param @param resp    设定文件 
	* @return void    返回类型 
	* @throws
	*/
	@ResponseBody
	@RequestMapping(value = "/LLPay/receiveNotify")
	public void receiveNotify(HttpServletRequest req, HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
		logger.info("连连代付:连连代付回调开始");
		resp.setCharacterEncoding("UTF-8");
		RetBean retBean = new RetBean();
		String reqStr = YinTongUtil.readReqStr(req);
		logger.info("连连代付:接收付款异步通知数据：【" + reqStr + "】");
		try {
			if (YinTongUtil.isnull(reqStr)) {
				// 可抛异常，后续人工介入分析原因,这里的交易失败不是指订单付款失败，订单付款结果以订单付款状态字段result_pay为准
				retBean.setRet_code("9999");
				retBean.setRet_msg("交易失败");
				resp.getWriter().write(JSON.toJSONString(retBean));
				resp.getWriter().flush();
				logger.error("连连代付:验证回调参数为空-交易失败");
				return;
			}
			// 解析异步通知对象
			PayDataBean payDataBean = JSON.parseObject(reqStr, PayDataBean.class);
			//获取订单状态处理业务逻辑
			Map<String, Object> notifyMap = new HashMap<String, Object>();
			notifyMap.put("dfOrderNum", payDataBean.getNo_order());
			DfPayOrder dfPayOrder = dfPayOrderService.detail(notifyMap);
			if (dfPayOrder != null && dfPayOrder.getDfStatus() == 2) {
				System.out.println("连连代付:解析异步通知对象成功");
				// 获取通道信息
				Map<String, Object> dfPayWayRateMap = new HashMap<String, Object>();
				dfPayWayRateMap.put("id", dfPayOrder.getDfRateId());
				dfPayWayRateMap.put("status", 1);
				dfPayWayRateMap.put("isDelete", 2);
				DfPayWayRate dfPayWayRate = dfPayWayRateService.detail(dfPayWayRateMap);
				if (dfPayWayRate != null) {
					//获取连连代付-公钥
					String publicKeyOnline=dfPayWayRate.getRateKey3();
					if (!YinTongUtil.checkSign(reqStr,publicKeyOnline, PartnerConfig.MD5_KEY)) {
						// 可抛异常，后续人工介入分析原因,这里的交易失败不是指订单付款失败，订单付款结果以订单付款状态字段result_pay为准
						retBean.setRet_code("9999");
						retBean.setRet_msg("交易失败");
						resp.getWriter().write(JSON.toJSONString(retBean));
						resp.getWriter().flush();
						logger.info("连连代付:付款异步通知验签失败-交易失败");
						return;
					}
					Map<String, Object> dfPayCompanyRateMap = new HashMap<String, Object>();
					dfPayCompanyRateMap.put("companyId", dfPayOrder.getCompanyId());
					dfPayCompanyRateMap.put("status", 1);
					DfPayCompanyRate dfPayCompanyRate = dfPayCompanyRateService.detail(dfPayCompanyRateMap);
					if (dfPayCompanyRate == null) {
						System.out.println("连连代付:商户支持的代付通道非法/没有配置->请检查代付通道是否关闭或者删除了（DfPayCompanyRate）");
						return;
					}
					// 代付金额
					double dfPayMoney = dfPayOrder.getDfPayMoney();
					// 资金池
					double rateTotalAmount = dfPayWayRate.getTotalAmount();
					// 商户余额
					double totalAmount = dfPayCompanyRate.getTotalAmount();
					// 成本费
					double dfCostRateMoney = dfPayOrder.getDfCostRateMoney();
					// 手续费
					double dfPayWayMoney = dfPayOrder.getDfPayWayMoney();
					dfPayWayRate.setTotalAmount(rateTotalAmount - dfPayMoney - dfCostRateMoney);
					dfPayCompanyRate.setTotalAmount(totalAmount - dfPayMoney - dfPayWayMoney);
					dfPayWayRateService.update(dfPayWayRate);
					dfPayCompanyRateService.update(dfPayCompanyRate);
					// 修改代付订单状态：成功
					dfPayOrder.setDfStatus(1);
					// 清算时间：这里只返回年月日，不精确；所以用本系统时间
					dfPayOrder.setPayTime(new Date());
					dfPayOrderService.update(dfPayOrder);
					System.out.println("连连代付:付款成功！订单号为：" + dfPayOrder.getDfOrderNum());
					retBean.setRet_code("0000");
					retBean.setRet_msg("交易成功");
					resp.getWriter().flush();
					System.out.println("连连代付:付款异步通知数据接收处理成功->订单号为："+dfPayOrder.getDfOrderNum());
					return;
				}else{
					System.out.println("连连代付:代付通道没有获取到->请检查代付通道是否关闭或删除(DfPayWayRate)");
					return;
				}
			}
			if(dfPayOrder != null && dfPayOrder.getDfStatus() == 1){
				retBean.setRet_code("0000");
				retBean.setRet_msg("交易成功");
				resp.getWriter().write(JSON.toJSONString(retBean));
				resp.getWriter().flush();
				System.out.println("连连代付:付款异步通知数据接收处理成功->此订单已经被处理为交易成功状态");
				return;
			}
			if(dfPayOrder ==null){
				System.out.println("连连代付:订单没有查询到->请检查订单表是否有此订单，订单号为："+payDataBean.getNo_order());
				return;
			}
		} catch (Exception e) {
			// 可抛异常，后续人工介入分析原因,这里的交易失败不是指订单付款失败，订单付款结果以订单付款状态字段result_pay为准
			logger.error("连连代付:异步通知报文解析异常：" + e);
			retBean.setRet_code("9999");
			retBean.setRet_msg("交易失败");
			try {
				resp.getWriter().write(JSON.toJSONString(retBean));
				resp.getWriter().flush();
				return;
			} catch (IOException e1) {
				e1.printStackTrace();
				logger.error("连连代付:异步通知报文解析异常2：" + e1);
			}
		}
	}
	 public static void main(String[] args) {
		try {
			
			System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").parse("2017102559925229"));
			System.out.println(new SimpleDateFormat("yyyyMMddHHmmss").parse("20171025141920"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
