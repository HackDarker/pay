package com.pay.manger.controller.job;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.business.tx.service.TxPayOrderClearService;

@Component
@Controller
@RequestMapping("/TxPayOrderClearJob")
public class TxPayOrderClearJob {
	@Autowired
	private TxPayOrderClearService txPayOrderClearService;
	
	/**
	 * startUp 
	 * 每小时执行订单统计
	 * void    返回类型
	 */
	@RequestMapping("/startUp")
	public void startUp() {
		Date d = new Date(new Date().getTime()-24*60*60*1000);
		try {
			txPayOrderClearService.job(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
