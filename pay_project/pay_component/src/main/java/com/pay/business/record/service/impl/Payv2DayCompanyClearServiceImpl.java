package com.pay.business.record.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.merchant.mapper.Payv2BussCompanyMapper;
import com.pay.business.merchant.mapper.Payv2ChannelMapper;
import com.pay.business.record.entity.Payv2DayCompanyClear;
import com.pay.business.record.mapper.Payv2DayCompanyClearMapper;
import com.pay.business.record.service.Payv2DayCompanyClearService;
import com.pay.business.util.DecimalUtil;

/**
 * @author cyl
 * @version 
 */
@Service("payv2DayCompanyClearService")
public class Payv2DayCompanyClearServiceImpl extends BaseServiceImpl<Payv2DayCompanyClear, Payv2DayCompanyClearMapper> implements Payv2DayCompanyClearService {
	// 注入当前dao对象
    @Autowired
    private Payv2DayCompanyClearMapper payv2DayCompanyClearMapper;
    
    @Autowired
    private Payv2ChannelMapper payv2ChannelMapper;
    
    @Autowired
    private Payv2BussCompanyMapper payv2BussCompanyMapper;

    public Payv2DayCompanyClearServiceImpl() {
        setMapperClass(Payv2DayCompanyClearMapper.class, Payv2DayCompanyClear.class);
    }

	public PageObject<Payv2DayCompanyClear> dayClearList(Map<String, Object> map) {		
		int totalData = payv2DayCompanyClearMapper.getDayClearListCount(map);//查询商户日账单总条数
		PageHelper pageHelper = new PageHelper(totalData, map);
		List<Payv2DayCompanyClear> list = payv2DayCompanyClearMapper.getDayClearList(pageHelper.getMap());
		for(Payv2DayCompanyClear payv2DayCompanyClear : list){
			if(payv2DayCompanyClear.getSuccessMoney() !=null && payv2DayCompanyClear.getRefundMoney() !=null && payv2DayCompanyClear.getRateMoney() !=null){
				payv2DayCompanyClear.setCleanpayMoney(DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(), payv2DayCompanyClear.getRefundMoney()));
				payv2DayCompanyClear.setAccountsMonry(DecimalUtil.sub(DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(), payv2DayCompanyClear.getRefundMoney()),payv2DayCompanyClear.getRateMoney()));
			}
		}	
		PageObject<Payv2DayCompanyClear> pageObject = pageHelper.getPageObject();
		pageObject.setDataList(list);
		return pageObject;
	}
	
	public void moneyClear(String[] companyCheckId) {
		payv2DayCompanyClearMapper.moneyClear(companyCheckId);
	}	
	
	public Map<Long, Payv2DayCompanyClear> mouthClearList(Map<String, Object> map) {
		List<Payv2DayCompanyClear> list = payv2DayCompanyClearMapper.getMouthsClearList(map);
		//商户对应的金额数据
		Map<Long, Payv2DayCompanyClear> map2=new HashMap<Long, Payv2DayCompanyClear>();
		
		for(Payv2DayCompanyClear payv2DayCompanyClear : list){
			if(map2.containsKey(payv2DayCompanyClear.getCompanyId())){
				//将个金额字段相加
				Payv2DayCompanyClear allDayCompanyClear=map2.get(payv2DayCompanyClear.getCompanyId());				
				allDayCompanyClear.setSuccessCount(allDayCompanyClear.getSuccessCount()+payv2DayCompanyClear.getSuccessCount());//交易笔数
				allDayCompanyClear.setSuccessMoney(DecimalUtil.add(allDayCompanyClear.getSuccessMoney(),payv2DayCompanyClear.getSuccessMoney()));//订单交易金额
				if(payv2DayCompanyClear.getRefundCount()!=null){
					allDayCompanyClear.setRefundCount(allDayCompanyClear.getRefundCount()+payv2DayCompanyClear.getRefundCount());//退款笔数
				}			
				if(payv2DayCompanyClear.getRefundMoney()!=null){
					allDayCompanyClear.setRefundMoney(DecimalUtil.add(allDayCompanyClear.getRefundMoney(),payv2DayCompanyClear.getRefundMoney()));//退款金额
				}
				allDayCompanyClear.setCleanpayMoney(DecimalUtil.add(allDayCompanyClear.getCleanpayMoney(),DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(),payv2DayCompanyClear.getRefundMoney() )));//订单支付净额
				allDayCompanyClear.setRateMoney(DecimalUtil.add(allDayCompanyClear.getRateMoney(),payv2DayCompanyClear.getRateMoney()));
				allDayCompanyClear.setRateProfit(DecimalUtil.add(allDayCompanyClear.getRateProfit(),payv2DayCompanyClear.getRateProfit() ));
				allDayCompanyClear.setAccountsMonry(DecimalUtil.add(allDayCompanyClear.getAccountsMonry(), DecimalUtil.sub(DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(), payv2DayCompanyClear.getRefundMoney()),payv2DayCompanyClear.getRateMoney()) ));
			}else{
				Payv2DayCompanyClear allDayCompanyClear = new Payv2DayCompanyClear();
				allDayCompanyClear.setChannelName(payv2DayCompanyClear.getChannelName());
				allDayCompanyClear.setCompanyName(payv2DayCompanyClear.getCompanyName());
				allDayCompanyClear.setSuccessCount(payv2DayCompanyClear.getSuccessCount());//交易笔数
				allDayCompanyClear.setSuccessMoney(payv2DayCompanyClear.getSuccessMoney());//订单交易金额
				allDayCompanyClear.setRefundCount(payv2DayCompanyClear.getRefundCount()==null?0:payv2DayCompanyClear.getRefundCount());//退款笔数				
				allDayCompanyClear.setRefundMoney(payv2DayCompanyClear.getRefundMoney()==null?0.00:payv2DayCompanyClear.getRefundMoney());//退款金额
				allDayCompanyClear.setCleanpayMoney(DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(),payv2DayCompanyClear.getRefundMoney()));//交易支付净额
				allDayCompanyClear.setRateMoney(payv2DayCompanyClear.getRateMoney());//商户手续费
				allDayCompanyClear.setRateProfit(payv2DayCompanyClear.getRateProfit());//手续费利润
				allDayCompanyClear.setAccountsMonry( DecimalUtil.sub(DecimalUtil.sub(payv2DayCompanyClear.getSuccessMoney(), payv2DayCompanyClear.getRefundMoney()),payv2DayCompanyClear.getRateMoney()));
				map2.put(payv2DayCompanyClear.getCompanyId(), allDayCompanyClear);				
			}
		}
		return map2;
	}

	public Payv2DayCompanyClear dayClearAllMoney(Map<String, Object> map) {
		Payv2DayCompanyClear allClear = new Payv2DayCompanyClear();		
		//数据库计算allClear的各个字段
		allClear = payv2DayCompanyClearMapper.dayClearAllMoney(map);
		if(allClear.getSuccessMoney() == null){
			allClear.setSuccessMoney(0.00);
		}
		if(allClear.getRefundMoney() == null){
			allClear.setRefundMoney(0.00);
		}
		if(allClear.getRateMoney() == null){
			allClear.setRateMoney(0.00);
		}
		if(allClear.getRateProfit() == null){
			allClear.setRateProfit(0.00);
		}
		allClear.setCleanpayMoney(DecimalUtil.sub(allClear.getSuccessMoney(),allClear.getRefundMoney()));
		allClear.setAccountsMonry(DecimalUtil.sub(allClear.getCleanpayMoney(), allClear.getRateMoney()));		
		return allClear;
	}
	
	public Payv2DayCompanyClear selectObject(Map<String, Object> map) {
		return payv2DayCompanyClearMapper.selectObject(map);
	}
	
	public void updateStatus(Payv2DayCompanyClear payv2DayCompanyClear) {
		payv2DayCompanyClearMapper.updateByEntity(payv2DayCompanyClear);
	}
	
	public void insertCompanyToClear(){
		List<Map<String, Object>> companys = payv2DayCompanyClearMapper.getCompanys();
		for (Map<String, Object> map : companys) {
			try {
				String companycheckId = System.currentTimeMillis() + "" + map.get("id");
				payv2DayCompanyClearMapper.insertClear(companycheckId, map.get("id").toString(), map.get("channel_id").toString());
				
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}	
}
