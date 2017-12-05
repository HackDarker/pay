package com.pay.business.order.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.teamwork.base.service.impl.BaseServiceImpl;
import com.pay.business.order.entity.Payv2PayOrderNotify;
import com.pay.business.order.mapper.Payv2PayOrderNotifyMapper;
import com.pay.business.order.service.Payv2PayOrderNotifyService;

/**
 * @author cyl
 * @version 
 */
@Service("payv2PayOrderNotifyService")
public class Payv2PayOrderNotifyServiceImpl extends BaseServiceImpl<Payv2PayOrderNotify, Payv2PayOrderNotifyMapper> implements Payv2PayOrderNotifyService {
	// 注入当前dao对象
    @Autowired
    private Payv2PayOrderNotifyMapper payv2PayOrderNotifyMapper;

    public Payv2PayOrderNotifyServiceImpl() {
        setMapperClass(Payv2PayOrderNotifyMapper.class, Payv2PayOrderNotify.class);
    }
    
    public void insetNotify(String orderNum,String notifyUrl,String param,long time,String result){
    	Payv2PayOrderNotify t = new Payv2PayOrderNotify();
    	Date date = new Date();
    	t.setCallTime(date);
    	t.setCreateTime(date);
    	t.setNotifyUrl(notifyUrl);
    	t.setOrderNum(orderNum);
    	t.setParam(param);
    	t.setResult(result);
    	t.setTime(Integer.valueOf(time+""));
    	if(result!=null){
    		t.setResultLen(result.toCharArray().length);
    	}else{
    		t.setResultLen(0);
    	}
    	payv2PayOrderNotifyMapper.insertByEntity(t);
    }
}
