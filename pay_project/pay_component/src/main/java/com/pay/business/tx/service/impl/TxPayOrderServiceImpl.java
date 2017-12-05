package com.pay.business.tx.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.core.teamwork.base.util.page.PageHelper;
import com.core.teamwork.base.util.page.PageObject;
import com.pay.business.tx.entity.TxPayOrder;
import com.pay.business.tx.entity.TxPayRateBalance;
import com.pay.business.tx.mapper.TxPayOrderMapper;
import com.pay.business.tx.service.TxPayOrderService;
import com.pay.business.tx.service.TxPayRateBalanceService;
import com.pay.business.util.minshengbank.junhu.JunHuPay;
import com.pay.business.util.minshengbank.xm.MinShengXMPay;

/**
 * @author cyl
 * @version 
 */
@Service("txPayOrderService")
public class TxPayOrderServiceImpl extends BaseServiceImpl<TxPayOrder, TxPayOrderMapper> implements TxPayOrderService {
	private Logger log = Logger.getLogger(TxPayOrderServiceImpl.class);
	
	// 注入当前dao对象
    @Autowired
    private TxPayOrderMapper txPayOrderMapper;
    
    @Autowired
    private TxPayRateBalanceService txPayRateBalanceService;

    public TxPayOrderServiceImpl() {
        setMapperClass(TxPayOrderMapper.class, TxPayOrder.class);
    }
	
	public Double getIngAmount(Map<String, Object> map) {
		return txPayOrderMapper.getIngAmount(map);
	}
	
	public Double getSucAmount(Map<String, Object> map) {
		return txPayOrderMapper.getSucAmount(map);
	}
	
	public TxPayOrder getNowAmount(Map<String, Object> map) {
		return txPayOrderMapper.getNowAmount(map);
	}
	
	@SuppressWarnings("unchecked")
	public PageObject<TxPayOrder> getPageObject(Map<String, Object> map) {
		int totalData = txPayOrderMapper.getCount2(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<TxPayOrder> orderList = txPayOrderMapper.getPageObject2(helper.getMap());
		PageObject<TxPayOrder> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
	}
    
	public void setEnchashmentByCom(String id) {
		List<Map<String, Object>> listMap = txPayOrderMapper.getEnchashmentRateByCom(id);
		Date x = new Date();
			for (int i = 0; i < listMap.size(); i++) {
				Map<String, Object> map = listMap.get(i);
				try {
					if(5 > Double.valueOf(String.valueOf(map.get("balance")))){
						continue;
					}
					
					long orderNum = System.currentTimeMillis();
					orderNum = orderNum + (long)i;
					System.out.println("定时单号：AUTO"+orderNum);
					
					//判断通道
					String txnType = "";
					if(map.get("name").toString().contains("_ALI")){
						txnType = "2";
					}else if (map.get("name").toString().contains("_QQ")) {
						txnType = "3";
					}else if (map.get("name").toString().contains("_WEIXIN")) {
						txnType = "1";
					}
					Map<String, String> drawPay = new HashMap<String, String>();
					
					if(map.get("name").toString().contains("_JUNHU")) {
						drawPay = JunHuPay.drawPay(map.get("key1")+"", "AUTO" + orderNum, txnType, map.get("key2")+"");
					}else if(map.get("name").toString().contains("_MSXM")) {
						drawPay = MinShengXMPay.drawPay(map.get("key1")+"", "AUTO" + orderNum, txnType, map.get("key2")+"");
					}
					
					
					if("0003".equals(drawPay.get("code")) || "0000".equals(drawPay.get("code"))){
						//更新提现余额为0
						TxPayRateBalance txPayRateBalance = new TxPayRateBalance();
						txPayRateBalance.setId(Long.valueOf(map.get("id")+""));
						txPayRateBalance.setTxBalance(0.00);
						txPayRateBalanceService.update(txPayRateBalance);
						//新增提现记录
						TxPayOrder txPayOrder = new TxPayOrder();
						txPayOrder.setCompanyId(Long.valueOf(id));
						txPayOrder.setOrderNum("AUTO"+orderNum);
						txPayOrder.setRateId(Long.valueOf(String.valueOf(map.get("rateId"))));
						txPayOrder.setTxTime(x);
						txPayOrder.setPayPlatform(Integer.valueOf(txnType));
						txPayOrder.setStatus(2);
						txPayOrder.setAmount(Double.valueOf(String.valueOf(map.get("balance"))));
						txPayOrder.setAccountBank(map.get("accountBank")+"");
						txPayOrder.setAccountName(map.get("accountName")+"");
						txPayOrder.setAccountCard(map.get("accountCard")+"");
						
						txPayOrderMapper.insertByEntity(txPayOrder);
						System.out.println(txPayOrder.getId());
						//根据订单号写缓存
						//RedisDBUtil.redisDao.setString("AUTO" + orderNum, txPayOrder.getId()+","+map.get("key2")+","+map.get("key1"), 18000);
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error("提现error："+e.getMessage());
					continue;
				}
				
			}
		
	}

	public Map<String,Object> queryToday(Map<String, Object> map) {
		return txPayOrderMapper.queryToday(map);
	}

	public PageObject<TxPayOrder> searchTxOrderList(Map<String, Object> map) {
		int totalData = txPayOrderMapper.searchTxOrderListCount(map);
		PageHelper helper = new PageHelper(totalData, map);
		List<TxPayOrder> orderList = txPayOrderMapper.searchTxOrderList(helper.getMap());
		PageObject<TxPayOrder> pageList = helper.getPageObject();
		pageList.setDataList(orderList);
		return pageList;
	}

	
	public Map<String, Object> selectOrderKey(String orderNum) {
		return txPayOrderMapper.selectOrderKey(orderNum);
	}
}
