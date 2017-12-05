package com.pay.business.payway.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.cache.annotations.Cacheable;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.redis.RedisDBUtil;
import com.pay.business.merchant.service.Payv2BussSupportPayWayService;
import com.pay.business.payv2.entity.Payv2BankAppKey;
import com.pay.business.payv2.service.Payv2BankAppKeyService;
import com.pay.business.payway.entity.Payv2PayWayRate;
import com.pay.business.payway.entity.Payv2PayWayRateVO;
import com.pay.business.payway.mapper.Payv2PayWayRateMapper;
import com.pay.business.payway.service.Payv2PayWayRateService;
import com.pay.business.sysconfig.entity.SysConfigDictionary;
import com.pay.business.sysconfig.service.SysConfigDictionaryService;
import com.pay.business.sysconfig.service.SysLogService;
import com.pay.business.util.LogTypeEunm;
import com.pay.business.util.PayRateDictValue;
import com.pay.business.util.minshengbank.xm.MinShengXMPay;

/**
 * @author cyl
 * @version 
 */
@Service("payv2PayWayRateService")
public class Payv2PayWayRateServiceImpl extends BaseServiceImpl<Payv2PayWayRate, Payv2PayWayRateMapper> implements Payv2PayWayRateService {
	// 注入当前dao对象
    @Autowired
    private Payv2PayWayRateMapper payv2PayWayRateMapper;
    @Autowired
    private Payv2BussSupportPayWayService payv2BussSupportPayWayService;
    @Autowired
    private SysConfigDictionaryService sysConfigDictionaryService;
    @Autowired
    private Payv2BankAppKeyService payv2BankAppKeyService;
    @Autowired
    private SysLogService sysLogService;
    
    public Payv2PayWayRateServiceImpl() {
        setMapperClass(Payv2PayWayRateMapper.class, Payv2PayWayRate.class);
    }

    /**
	 * 根据策略获取支付通道路由
	 * @param rateType
	 * @param payWayId
	 * @return
	 */
    @Cacheable(
			region = "payv2PayWayRateService",
			namespace = "getPayWayRate",
			fieldsKey = {"#rateType","#payWayId","#companyId","#appId"},
			expire = 300
			)
	public Payv2PayWayRate getPayWayRate(Integer rateType, Long payWayId,Long companyId,Long appId) {
		
    	Map<String,Object> map = new HashMap<>();
		map.put("companyId", companyId);
		map.put("payWayId", payWayId);
		map.put("payType", rateType);
		map.put("payWayStatus", 1);
		map.put("isDelete", 2);
		map.put("status", 1);
		List<Payv2PayWayRate> rateList = payv2PayWayRateMapper.queryByCompany(map);
    	
		return getRate(rateList,appId);
		
	}
    
	/**
	 * 根据策略获取支付通道路由
	 * @param rateList
	 * @param appId
	 * @return
	 */
	public Payv2PayWayRate getRate(List<Payv2PayWayRate> allRateList,Long appId){
		/*Map<String,Object> param = new  HashMap<>();
		List<Payv2PayWayRate> rateList = new ArrayList<>();
		for (Payv2PayWayRate payv2PayWayRate : allRateList) {
			param.clear();
			param.put("id", payv2PayWayRate.getDicId());
			SysConfigDictionary sysConfigDictionary = sysConfigDictionaryService.detail(param);
			//查询通道是否绑定银行数据(key和密钥)
			if(checkRate(sysConfigDictionary, appId, payv2PayWayRate.getId())){
				rateList.add(payv2PayWayRate);
			}
		}*/
		
		return getRate(allRateList);
	}
	
	/**
	 * 查询通道是否绑定银行数据(key和密钥)
	 * @param sysConfigDictionary
	 * @param appId
	 * @param rateId
	 * @return
	 */
	public boolean checkRate(SysConfigDictionary sysConfigDictionary,Long appId,Long rateId){
		if(PayRateDictValue.PAY_TYPE_ALIPAY_YY.equals(sysConfigDictionary.getDictName())){
			Map<String,Object> param = new  HashMap<>();
			param.put("appId", appId);
			param.put("rate", rateId);
			Payv2BankAppKey payv2BankAppKey = payv2BankAppKeyService.detail(param);
			if(payv2BankAppKey!=null&&payv2BankAppKey.getBankKey()!=null&&payv2BankAppKey.getBankKey1()!=null){
				return true;
			}
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 根据策略获取支付通道路由
	 * @param rateList
	 * @param appId
	 * @return
	 */
	public Payv2PayWayRate getRate(List<Payv2PayWayRate> rateList){
		//取费率最低的，如果还有多个，再取主键默认排序的第一个
		double rate = 100.0;
		List<Payv2PayWayRate> list = new ArrayList<>();
		for (Payv2PayWayRate payv2PayWayRate : rateList) {
			if(payv2PayWayRate.getPayWayRate().doubleValue()<=rate){
				rate = payv2PayWayRate.getPayWayRate().doubleValue();
			}
		}
		for (Payv2PayWayRate payv2PayWayRate : rateList) {
			if(payv2PayWayRate.getPayWayRate().doubleValue()==rate){
				list.add(payv2PayWayRate);
			}
		}
		Payv2PayWayRate result = null;
		if(list.size()==1){
			result = list.get(0);
		}else{
			long id = 100000000; //假设主键最大值
			for (Payv2PayWayRate payv2PayWayRate : list) {
				if(payv2PayWayRate.getId().longValue()<id){
					result = payv2PayWayRate;
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据策略获取支付通道路由(用于客户端无界面的策略)
	 * @param rateType
	 * @param companyId
	 * @param payViewType   1支付宝，2微信  。。。。叠加字段
	 * @return
	 */
	@Cacheable(
			region = "payv2PayWayRateService",
			namespace = "getPayWayRate1",
			fieldsKey = {"#rateType","#companyId","#payViewType","#appId"},
			expire = 300
			)
	public Payv2PayWayRate getPayWayRate(Integer rateType,Long companyId,Integer payViewType,Long appId) {
		
		Map<String,Object> map = new HashMap<>();
		map.put("companyId", companyId);
		map.put("payViewType", payViewType);
		map.put("payType", rateType);
		map.put("payWayStatus", 1);
		map.put("isDelete", 2);
		map.put("status", 1);
		List<Payv2PayWayRate> rateList = payv2PayWayRateMapper.queryByCompany(map);
		
		
		return getRate(rateList,appId);
		
	}

	/**
	 * 根据id查询支付通道
	 * @param id
	 * @return
	 */
	@Cacheable(
			region = "payv2PayWayRateService",
			namespace = "queryByid",
			fieldsKey = {"#id"},
			expire = 300
			)
	public Payv2PayWayRate queryByid(Long id) {
		return payv2PayWayRateMapper.queryByid(id);
	}
	
	/**
	 * 根据渠道商id和支付方式id查询通道
	 * @param map
	 * @return
	 */
	public List<Payv2PayWayRate> queryByChannelWayId(Map<String,Object> map){
		return payv2PayWayRateMapper.queryByChannelWayId(map);
	}
	/**
	 * 获取通道
	 */
	public List<Payv2PayWayRate> queryByDicRate(){
		//获取通道
		List<Payv2PayWayRate> rateList = payv2PayWayRateMapper.queryByDicRate();
		return rateList;
	}

	
	public List<Payv2PayWayRate> getPayWaysByCom(Map<String,Object> map) {
		return payv2PayWayRateMapper.getPayWaysByCom(map);
	}

	public void batchUpdate(Map<String, Object> paramMap) {
		payv2PayWayRateMapper.batchUpdate(paramMap);
	}

	public List<Payv2PayWayRateVO> getExport(Map<String, Object> paramMap) {
		return payv2PayWayRateMapper.getExport(paramMap);
	}

	public void updatePayWayRate(Map<String, Object> map) {
		payv2PayWayRateMapper.updatePayWayRate(map);
	}

	
	public Payv2PayWayRate getEnchashment(String id) {
		return payv2PayWayRateMapper.getEnchashment(id);
	}
	
	/**
	 * 自动提现(定时8点30 、12点 、 下午4点 、下午9点50)
	 */
	public void setAutoEnchashment(){
		List<Map<String, String>> listMap = payv2PayWayRateMapper.getAutoEnchashmentRate();
		
		for (int i = 0; i < listMap.size(); i++) {
			Map<String, String> map = listMap.get(i);
			//根据商户号写缓存
			RedisDBUtil.redisDao.setString(map.get("key1"), map.get("key2"), 18000);
			
			long orderNum = System.currentTimeMillis();
			orderNum = orderNum + (long)i;
			System.out.println("定时单号："+orderNum);
			
			//判断通道
			String txnType = "";
			if(map.get("name").contains("_ALI")){
				txnType = "2";
			}else if (map.get("name").contains("_QQ")) {
				txnType = "3";
			}else if (map.get("name").contains("_WEIXIN")) {
				txnType = "1";
			}
			
			Map<String, String> drawPay = MinShengXMPay.drawPay(map.get("key1"), "AUTO" + orderNum, txnType, map.get("key2"));
			sysLogService.addSysLogByString(1, LogTypeEunm.T46, "127.0.0.1", (long)60, drawPay);
		}
	}
	
	public static void main(String[] args) {
		String xx = "PAY_TYPE_MSBANk_ALI_SCAN";
		System.out.println(xx.contains("_ALI"));
		
	}

	public List<Payv2PayWayRate> getD0Rates(Map<String, Object> map) {
		return payv2PayWayRateMapper.getD0Rates(map);
	}

	
	public Integer getNextId() {
		
		return payv2PayWayRateMapper.getNextId();
	}
}
