package com.pay.manger.controller.payv2;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.pay.business.tx.entity.TxPayOrder;
import com.pay.business.tx.service.TxPayOrderService;
import com.pay.business.util.minshengbank.xm.EpaySignUtil;

@RestController
@RequestMapping("/enchashment")
public class Pay2EnchashmentController {
	private static Logger log = Logger.getLogger(Pay2EnchashmentController.class);
	
	@Autowired
	private TxPayOrderService txPayOrderService;
	
	
	/**
	 * @param memberCode
	 * @param orderNum
	 * @param txnType
	 * @param key
	 * @return
	 * @throws Exception 
	 */
	/*@RequestMapping(value = "/msEnchashment")
	//public Map<String, Object> msEnchashment(String memberCode, String orderNum, String txnType, String callbackUrl, String sign) {
	public Map<String, Object> msEnchashment(@RequestParam Map<String, Object> map){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		boolean paramStrCon = ObjectUtil.checkObject(new String[] { "paramStr" }, map);
		if(!paramStrCon){
			log.debug("未传paramStr字段");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		Map<String, Object> paramMap = StringHandleUtil.toMap(map.get("paramStr").toString());
		if(paramMap.keySet().size()==0){
			log.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_STR_ERROR,null);
			return resultMap;
		}
		//商户号、金额、付款方式、开户账号、卡号、备注、商户订单号、下单时间
		boolean isNotNull = ObjectUtil.checkObject(new String[] {"mchNum", "bussOrderNum", "txnType", "callbackUrl", "sign"}, paramMap);
		if (!isNotNull){
			log.debug("参数不能为空,或者有误");
			resultMap = ReMessage.resultBack(ParameterEunm.PARAM_ERROR_MONEY,null);
			return resultMap;
		}
		String memberCode = paramMap.get("mchNum").toString();
		String orderNum = paramMap.get("bussOrderNum").toString();
		String txnType = paramMap.get("txnType").toString();
		String callbackUrl = paramMap.get("callbackUrl").toString();
		//String sign = paramMap.get("sign").toString();
		
		Payv2BussCompany payv2BussCompany = new Payv2BussCompany();
		payv2BussCompany.setCompanyKey(memberCode);
		payv2BussCompany = companyService.selectSingle(payv2BussCompany);
		if(null==payv2BussCompany){
			log.error("商户不存在！ " + memberCode);
			resultMap = ReMessage.resultBack(ParameterEunm.COMPANY_NOT_EXIST, null);
			return resultMap;
		}
		
		try {
			boolean con = PaySignUtil.checkSign(paramMap, payv2BussCompany.getCompanySecret());
			if(!con){
				log.debug("商户签名错误");
				resultMap = ReMessage.resultBack(ParameterEunm.SIGNATURE_ERROR,null);
				return resultMap;
			}
			Payv2PayWayRate enchashment = rateService.getEnchashment(payv2BussCompany.getId().toString());
			if (null==enchashment) {
				log.error("没有找到提现通道 ");
				resultMap = ReMessage.resultBack(ParameterEunm.RATE_TYPE_ERROR, null);
				return resultMap;
			}
			
			//暂定key5、key6为商户号和密钥
			Map<String, String> drawPay = MinShengXMPay.drawPay(enchashment.getRateKey5(), orderNum, txnType, enchashment.getRateKey6());
			log.info("提现请求上游回复数据:" + drawPay.toString());
			if(!"10001".equals(drawPay.get("code"))){
				RedisDBUtil.redisDao.setString(enchashment.getRateKey5() + orderNum, callbackUrl + "," + enchashment.getRateKey6() + "," + memberCode, 18000);
			}
			resultMap = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, drawPay);
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("服务器异常,请稍后再试");
			resultMap = ReMessage.resultBack(ParameterEunm.ERROR_500_CODE,null);
		}
		return resultMap;
	}*/
	
	/**
	 * @param request
	 * @return
	 */
	/*@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/msEnchashmentBack")
	public Map<String, String> msEnchashmentBack(HttpServletRequest request) {
		Map<String, String> resultMap= new HashMap<String, String>();
		resultMap.put("resCode", "0001");
		try {
			String paramString = new PayWayController().getParamString(request);
			System.out.println("paramString："+paramString);
			log.info("提现上游回调数据:" + paramString);
			Map<String, Object> map = (Map)JSON.parse(paramString);
			
			String memberCode = map.get("memberCode").toString();
			String orderNum = map.get("orderNum").toString();
			//1.商户回调地址，2.商户密钥，3.我方商户号
			String str = RedisDBUtil.redisDao.get(memberCode + orderNum);
			if(str==null||"".equals(str)){
				return resultMap;
			}
			String[] split = str.split(",");
			String signature = EpaySignUtil.signObj(split[1], map);
			if (signature.trim().equals(map.get("signStr").toString().trim())) {
				map.remove("signStr");
				//替换为我方商户号
				map.put("mchNum", split[2]);
				map.put("bussOrderNum", orderNum);
				map.remove("orderNum");
				//通知商户
				String doPost= "";
				map = ReMessage.resultBack(ParameterEunm.SUCCESSFUL_CODE, map);
				if(split[0].startsWith("https")){
					doPost = HttpsUtil.doPostString(split[0], map, "utf-8");
				}else{
					// 通知商户
					doPost = HttpUtil.httpPost(split[0], map);
				}
				if("success".equals(doPost)){
					//清空缓存
					RedisDBUtil.redisDao.delete(memberCode + orderNum);
					resultMap.put("resCode", "0000");
				}else {
					return null;
				}
			}
			
		} catch (Exception e) {
			log.error("提现上游回调:" + e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}*/
	
	
	/**
	 * 提现回调
	 * @throws Throwable 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/msEnchashmentBack")
	public Map<String, String> msEnchashmentBack(HttpServletRequest request) throws Throwable {
		Map<String, String> resultMap= new HashMap<String, String>();
		resultMap.put("resCode", "0001");
		try {
			String paramString = new PayWayController().getParamString(request);
			System.out.println("paramString："+paramString);
			log.info("提现上游回调数据:" + paramString);
			Map<String, Object> map = (Map<String, Object>)JSON.parse(paramString);
			String orderNum = map.get("orderNum").toString();
			Map<String, Object> selectOrderKey = txPayOrderService.selectOrderKey(orderNum);
			if(null == selectOrderKey){
				log.error("无提现记录！");
				return resultMap;
			}
			String id = selectOrderKey.get("id") + "";
			String mchId = selectOrderKey.get("key1") + "";
			String key = selectOrderKey.get("key2") + "";
			
			/*if(null==RedisDBUtil.redisDao.get(orderNum)){
				log.error("提现无缓存");
				return resultMap;
			}
			//1.提现余额记录id，2.密钥，3.商户号
			String[] str = RedisDBUtil.redisDao.get(orderNum).split(",");*/
			
			String signature = EpaySignUtil.signObj(key, map);
			if (signature.trim().equals(map.get("signStr").toString().trim())) {
				//非交易成功
				if(!"1".equals(map.get("respType"))){
					log.error("提现交易失败");
					return resultMap;
				}
				
				
				TxPayOrder txPayOrder = new TxPayOrder();
				txPayOrder.setStatus(1);
				txPayOrder.setId(Long.valueOf(id));
				Double drawAmount = Double.valueOf(String.valueOf(map.get("drawAmount")));
				Double drawFee = Double.valueOf(String.valueOf(map.get("drawFee")));
				Double tradeFee = Double.valueOf(String.valueOf(map.get("tradeFee")));
				//到账金额
				txPayOrder.setArrivalAmount(drawAmount);
				//提现手续费
				txPayOrder.setServiceAmount(drawFee);
				//交易手续费
				txPayOrder.setPayServiceAmount(tradeFee);
				//提现交易额
				txPayOrder.setAmount(drawAmount + drawFee + tradeFee);
				txPayOrder.setTxTime(new SimpleDateFormat("yyyyMMddHHmmss").parse(map.get("drawTime")+""));
				
				
				txPayOrderService.update(txPayOrder);
				
				resultMap.put("resCode", "0000");
			}else {
				System.out.println("自动提现回调验签失败");
				log.error("自动提现回调验签失败(商户号，密钥)：" + mchId + "," + orderNum);
			}
		} catch (Exception e) {
			log.error("提现上游回调:" + e.getMessage());
			e.printStackTrace();
		}
		return resultMap;
	}
	
	@RequestMapping(value = "/test")
	public String test(@RequestParam Map<String, Object> map) {
		System.out.println(map.toString());
		return "success";
	}

}
